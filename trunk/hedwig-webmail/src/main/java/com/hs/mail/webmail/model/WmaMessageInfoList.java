package com.hs.mail.webmail.model;

import java.util.ArrayList;
import java.util.List;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.SearchTerm;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.impl.WmaMessageInfoImpl;
import com.hs.mail.webmail.util.Pager;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.SortTerm;

public class WmaMessageInfoList {

	private static Logger log = LoggerFactory.getLogger(WmaMessageInfoList.class);
	
	public static WmaMessageInfoList EMPTY_LIST = new WmaMessageInfoList();

	// instance attributes
	protected List<WmaMessageInfo> messageInfos;

	/**
	 * Constructs a new <tt>WmaMessageInfoList</tt>.
	 */
	private WmaMessageInfoList() {
	}

	public List<WmaMessageInfo> getMessageInfos() {
		return messageInfos;
	}

	/**
	 * Returns the size of this list.
	 * 
	 * @return the size of this list.
	 */
	public int size() {
		return messageInfos.size();
	}
	
	/**
	 * Builds the list of <tt>WmaMessageInfoImpl</tt> instances from the given
	 * array of messages.
	 * 
	 * @param msgs
	 *            array of <tt>javax.mail.Message</tt> instances.
	 * 
	 * @throws WmaException
	 *             if it fails to create a <tt>JwmaMessageInfoImpl</tt>
	 *             instance.
	 */
	private void buildMessageInfoList(Message[] msgs, boolean asc)
			throws WmaException {
		this.messageInfos = new ArrayList<WmaMessageInfo>(msgs.length);
		WmaMessageInfo msginfo = null;
		if (asc) {
			for (int i = 0; i < msgs.length; i++) {
				msginfo = WmaMessageInfoImpl.createMessageInfo(msgs[i]);
				this.messageInfos.add(msginfo);
			}
		} else {
			for (int i = msgs.length - 1; i >= 0; i--) {
				msginfo = WmaMessageInfoImpl.createMessageInfo(msgs[i]);
				this.messageInfos.add(msginfo);
			}
		}
	}

	/**
	 * Factory method that creates a new <tt>WmaMessageInfoListImpl</tt>
	 * instance from the given array of messages.
	 * 
	 * @param msgs
	 *            array of <tt>javax.mail.Message</tt> instances.
	 * 
	 * @return the newly created <tt>WmaMessageInfoListImpl</tt> instance.
	 * 
	 * @throws WmaException
	 *             if it fails to build the list.
	 */
	private static WmaMessageInfoList createWmaMessageInfoList(Message[] msgs,
			boolean asc) throws WmaException {
		WmaMessageInfoList msglist = new WmaMessageInfoList();
		msglist.buildMessageInfoList(msgs, asc);
		return msglist;
	}

	/**
	 * Factory method that creates a new <tt>WmaMessageInfoList</tt>
	 * instance wrapping the list of messages in the given folder.
	 * 
	 * @param f
	 *            the <tt>javax.mail.Folder</tt> instance, the new list instance
	 *            should be created for.
	 * 
	 * @return the newly created <tt>WmaMessageInfoList</tt> instance.
	 * 
	 * @throws WmaException
	 *             if it fails retrieve the list of <tt>javax.mail.Message</tt>
	 *             instances from the folder, or when it fails to build the
	 *             list.
	 * @throws MessagingException 
	 */
	private static Message[] createWmaMessageInfoList(Folder f,
			SortTerm[] sortterm) throws MessagingException {
		if (f.getMessageCount() == 0) {
			return new Message[0];
		}
		if (sortterm != null) {
			return ((IMAPFolder) f).getSortedMessages(sortterm);
		} else {
			return f.getMessages();
		}
	}

	private static Message[] createWmaMessageInfoList(Folder f,
			SearchTerm term, SortTerm[] sortterm) throws MessagingException {
		if (sortterm != null) {
			return ((IMAPFolder) f).getSortedMessages(sortterm, term);
		} else {
			return f.search(term);
		}
	}
	
	public static WmaMessageInfoList createWmaMessageInfoList(Folder f,
			SearchTerm term, SortTerm[] sortterm, Pager pager)
			throws WmaException {
		try {
			// for listing only
			if (!f.isOpen()) {
				f.open(Folder.READ_ONLY);
			}
			Message[] mesgs = null;
			if (term != null) {
				mesgs = createWmaMessageInfoList(f, term, sortterm);
			} else {
				mesgs = createWmaMessageInfoList(f, sortterm);
			}
			pager.setItemCount(mesgs.length);
			
			Message[] fetch = (Message[]) ArrayUtils.subarray(mesgs,
					pager.getBegin(), pager.getEnd() + 1);
		    // fetch messages with a slim profile
			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.ENVELOPE); // contains the headers
			fp.add(FetchProfile.Item.FLAGS); // contains the flags
			fp.add(FetchProfile.Item.CONTENT_INFO); // contains the content types
			f.fetch(fetch, fp);

			return createWmaMessageInfoList(fetch, pager.isAscending());
		} catch (MessagingException mex) {
			log.error(mex.getMessage(), mex);
			throw new WmaException("wma.messagelist.failedcreation")
					.setException(mex);
		} finally {
			try {
				// close the folder
				if (f.isOpen()) {
					f.close(false);
				}
			} catch (MessagingException mesx) {
				// don't care, the specs say it IS closed anyway
			}
		}
	}

}
