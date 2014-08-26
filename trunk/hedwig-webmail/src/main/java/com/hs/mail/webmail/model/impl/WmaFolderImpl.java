package com.hs.mail.webmail.model.impl;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.SearchTerm;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaFolder;
import com.hs.mail.webmail.model.WmaStore;
import com.hs.mail.webmail.util.WmaUtils;

public class WmaFolderImpl implements WmaFolder {
	
	private static Logger log = LoggerFactory.getLogger(WmaFolderImpl.class);

	// associations
	protected Folder folder;

	// instance attributes
	protected List<WmaFolder> subfolders;
	protected int type;
    protected int messageCount = -1;

	/**
	 * Creates a <tt>WmaFolderImpl</tt> instance.
	 * 
	 * @param folder the <tt>javax.mail.Folder</tt> instance to be wrapped.
	 */
	private WmaFolderImpl(Folder folder) {
		this.folder = folder;
	}

	/*** Basic info ************************************************************/
	
	/**
	 * Returns this folder's wrapped mail folder instance.
	 * 
	 * @return wrapped instance as <tt>javax.mail.Folder</tt>.
	 */
	public Folder getFolder() {
		return folder;
	}

	public String getName() {
		return folder.getName();
	}

	public String getPath() {
		return folder.getFullName();
	}

	public int getType() {
		return type;
	}

	/**
	 * Sets this folder's type.
	 * 
	 * @param type
	 *            this folder's type as <tt>int</tt>.
	 */
	private void setType(int type) {
		this.type = type;
	}

	public boolean isParent(WmaFolder child, char separator) {
		return child.getPath().startsWith(getPath() + separator);
	}

	public List<WmaFolder> getSubfolders() {
		return subfolders;
	}
    
	public void setSubfolders(List<WmaFolder> subfolders) {
		this.subfolders = subfolders;
	}

	public WmaFolder addChild(WmaFolder folder) {
		if (subfolders == null) {
			subfolders = new ArrayList<WmaFolder>();
		}
		subfolders.add(folder);
		return folder;
	}
	
	public boolean hasSubfolders() {
		return (subfolders != null && subfolders.size() > 0);
	}


	/*** Messages related ******************************************************/
	
	public int getMessageCount() {
        try {
            // NOTE - Message count must be cached. Otherwise continuous call of
            // this method causes pending of IMAP server.
            if (messageCount < 0)
                messageCount = folder.getMessageCount();
            return messageCount;
        } catch (MessagingException ex) {
            return 0;
        }
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

	/**
	 * Returns a <tt>WmaDisplayMessage</tt> instance that wraps the mail message
	 * with the given number.
	 * 
	 * @param num
	 *            the number of the message to be retrieved as <tt>int</tt>.
	 * @return the <tt>WmaDisplayMessage</tt> instance wrapping the retrieved
	 *         message.
	 * @throws WmaException
	 *             if the message does not exist, or cannot be retrieved from
	 *             the store.
	 */
	public WmaDisplayMessage getWmaMessage(int num) throws WmaException {
		return getWmaMessage(num, null);
	}

	public WmaDisplayMessage getWmaMessage(int num, String msgid)
			throws WmaException {
		try {
			folder.open(Folder.READ_WRITE);
			// get message and create wrapper
			Message msg = folder.getMessage(num);
			if (msgid != null) {
				// TODO
			}
			WmaDisplayMessage message = WmaDisplayMessage
					.createWmaDisplayMessage(msg);
			// set body as String processed with the users msgprocessor
			// MessageBodyHandler.process(message);
			// close without expunge
			folder.close(false);
			return message;
		} catch (MessagingException mex) {
			throw new WmaException("wma.folder.wmamessage").setException(mex);
		} finally {
			// close the folder
			shutdownFolder(folder);
		}
	}

	public MimeMessage getMimeMessage(String msgid) throws WmaException {
		try {
			folder.open(Folder.READ_ONLY);
			SearchTerm term = new MessageIDTerm(msgid);
			Message[] msgs = folder.search(term);
			return (!ArrayUtils.isEmpty(msgs)) ? (MimeMessage) msgs[0] : null;
		} catch (MessagingException ex) {
			throw new WmaException("wma.folder.wmamessage").setException(ex);
		} finally {
			shutdownFolder(folder);
		}
	}

	/**
	 * Deletes the messages with the given numbers.
	 * 
	 * @param numbers
	 *            the messages to be deleted as <tt>int[]</tt>.
	 * @param purge
	 *            true if delete permanently
	 * 
	 * @throws WmaException
	 *             if it fails to delete any of the given messages.
	 */
	public void deleteMessages(WmaStore wstore, int[] numbers, boolean purge)
			throws WmaException {
		// don't work with null or empty arrays
		if (numbers == null || numbers.length == 0) {
			return;
		}
		try {
			folder.open(Folder.READ_WRITE);
			// prepare messages
			Message[] msgs = folder.getMessages(numbers);
			if (msgs.length > 0) {
				Folder trash = wstore.getTrashInfo().getFolder();
				if (!folder.getFullName().equals(trash.getFullName()) && !purge) {
					// if not the trash copy the messages to the trash
					folder.copyMessages(msgs, trash);
				}
				// flag deleted, so when closing with expunge
				// the messages are erased.
				folder.setFlags(msgs, new Flags(Flags.Flag.DELETED), true);
			}
			// close with expunge
			folder.close(true);
		} catch (MessagingException mex) {
			throw new WmaException("wma.folder.deletemessage.failed")
					.setException(mex);
		} finally {
			// close the folder
			shutdownFolder(folder);
		}
	}
	
	/**
	 * Copy or moves the messages with the given numbers to the given
	 * destination folder.
	 * 
	 * @param numbers
	 *            the messages to be moved as <tt>int[]</tt>.
	 * @param destfolder
	 *            the destination folder path as <tt>String</tt>.
	 * @param move
	 *            true if delete original messages (i.e copy)
	 * 
	 * @throws WmaException
	 *             if it fails to move if the destination folder does not exist,
	 *             or if any of the given messages cannot be moved.
	 */
	public void copyMessages(WmaStore wstore, int[] numbers, String destfolder,
			boolean move) throws WmaException {
		// dont work with null or empty arrays
		if (destfolder == null || destfolder.length() == 0) {
			return;
		}
		try {
			Folder dest = wstore.getFolder(destfolder);
			if (!dest.exists()) {
				throw new WmaException(
						"wma.folder.movemessage.destination.missing");
			}
			// check destination type
			if (WmaFolder.TYPE_FOLDER == dest.getType()) {
				throw new WmaException(
						"wma.folder.movemessage.destination.foul");
			}
			folder.open((move) ? Folder.READ_WRITE : Folder.READ_ONLY);
			// prepare messages
			Message[] msgs = (null == numbers || numbers.length == 0) 
					? folder.getMessages() : folder.getMessages(numbers);
			if (msgs.length > 0) {
				folder.copyMessages(msgs, dest);
				if (move) {
					folder.setFlags(msgs, new Flags(Flags.Flag.DELETED), true);
				}
			}
			folder.close(move);
		} catch (MessagingException mex) {
			throw new WmaException("wma.folder.movemessage.failed")
					.setException(mex);
		} finally {
			// close the folder
			shutdownFolder(folder);
		}
	}

	public void setFlagMessages(int[] numbers, Flags flags, boolean set)
			throws WmaException {
		// don't work with null or empty arrays
		if (numbers == null || numbers.length == 0) {
			return;
		}
		try {
			folder.open(Folder.READ_WRITE);
			// prepare messages
			Message[] msgs = folder.getMessages(numbers);
			if (msgs.length > 0) {
				folder.setFlags(msgs, flags, set);
			}
			// close with expunge
			folder.close(false);
		} catch (MessagingException mex) {
			throw new WmaException("wma.folder.flagmessage.failed")
					.setException(mex);
		} finally {
			// close the folder
			shutdownFolder(folder);
		}
	}

	public void writeMimeMessage(HttpServletResponse response, int num,
			String filename) throws WmaException {
		try {
			folder.open(Folder.READ_ONLY);
			Message msg = folder.getMessage(num);
			String type = "";
			String disposition = "";
			// we do it all for fun or not?
			if (filename == null) {
				filename = WmaUtils.prepareString(WmaUtils.getHeader(msg,
						"Subject")) + ".eml";
				filename = StringUtils.replaceChars(filename, "\\/:*?\"<>|",
						"_");
				type = "message/rfc822";
				disposition = Part.ATTACHMENT;
			} else {
				type = "text/plain";
				disposition = Part.INLINE;
			}
			// set content type and file name
			response.setContentType(type);
			response.setHeader("Content-Disposition", disposition + "; "
					+ "filename=\"" + filename + "\"");

			// stream out message
			ServletOutputStream out = response.getOutputStream();
			msg.writeTo(out);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new WmaException("message.displaymime.failed")
					.setException(ex);
		} finally {
			// close the folder
			shutdownFolder(folder);
		}
	}
	
	/**
	 * Creates a <tt>WmaFolderImpl</tt> instance from the given <tt>Folder</tt>
	 * .
	 * 
	 * @param folder
	 *            mail <tt>Folder</tt> this instance will "wrap".
	 * 
	 * @return the newly created instance.
	 * 
	 * @throws WmaException
	 *             if it fails to create the new instance.
	 */
	public static WmaFolder createLight(Folder folder) throws WmaException {
		try {
			WmaFolderImpl wmafolder = new WmaFolderImpl(folder);
			wmafolder.setMessageCount(wmafolder.getMessageCount());
			wmafolder.setType(folder.getType());
			return wmafolder;
		} catch (MessagingException mex) {
			throw new WmaException("wma.folder.failedcreation");
		}
	}

	/*** Helper methods ****************************************************/

	private static void shutdownFolder(Folder f) {
		try {
			// close the folder
			if (f.isOpen()) {
				f.close(false);
			}
		} catch (MessagingException mesx) {
			// don't care, the specs say it IS closed anyway
		}
	}

	/*** End Helper methods ************************************************/

}
