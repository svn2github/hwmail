package com.hs.mail.webmail.model.impl;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.WmaSession;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaFolder;
import com.hs.mail.webmail.model.WmaStore;

public class WmaStoreImpl implements WmaStore {
	
	// logging
	private static Logger log = LoggerFactory.getLogger(WmaStoreImpl.class);

	// instance attributes
	private WmaSession session;
	private Store store;
	private char folderSeparator = '.';

	// special folders
	private WmaFolder inboxFolder;
	private WmaFolder trashFolder;
	private WmaFolder draftFolder;
	private WmaFolder sentMailFolder;
	private WmaFolder toSendFolder;
	private WmaFolder personalFolder;

	/**
	 * Constructs a <tt>WmaStoreImpl</tt> instance.
	 * 
	 * @param session the <tt>WmaSession</tt> instance this store
	 *        belongs to.
	 */
	public WmaStoreImpl(WmaSession session, Store store) {
		this.session = session;
		this.store = store;
	}
	
	/**
     * @return Returns the session.
     */
    public WmaSession getSession() {
        return session;
    }
    
    /**
	 * @return Returns the store.
	 */
	public Store getStore() {
		return store;
	}

	public char getFolderSeparator() {
		return folderSeparator;
	}

    public void setFolderSeparator(char folderSeparator) {
		this.folderSeparator = folderSeparator;
	}

	/*** wma special folders **********************************/

    public Folder getRootFolder() throws MessagingException {
		return store.getDefaultFolder();
	}

	/**
	 * Returns the <tt>WmaFolder</tt> instance that
	 * can be used to retrieve information about the store's
	 * INBOX folder (i.e. where new messages should be arriving).
	 * 
	 * @return the store's INBOX folder as <tt>WmaFolder</tt>.
	 */
	public WmaFolder getInboxInfo() {
		return inboxFolder;
	}

	/**
	 * Returns the <tt>WmaFolder</tt> instance that
	 * can  be used to retrieve information about the store's
	 * trash folder (i.e. where deleted messages end up first).
	 * 
	 * @return the store's trash folder as <tt>WmaFolder</tt>.
	 */
	public WmaFolder getTrashInfo() {
		return trashFolder;
	}

	private Folder getTrashFolder() throws WmaException {
		String name = session.getPreferences().getTrashFolder();
		return getFolder(name);
	}

	private void setTrashFolder() throws WmaException {
		try {
			Folder trash = getTrashFolder();
			if (!trash.exists()) {
				if (!trash.create(WmaFolderImpl.TYPE_MAILBOX)) {
					throw new WmaException("wma.store.createfolder.failed");
				}
			}
			// ensure subscription
			trash.setSubscribed(true);
			trashFolder = WmaFolderImpl.createLight(trash);
		} catch (MessagingException mex) {
			throw new WmaException(mex.getMessage()).setException(mex);
		}
	}

	public WmaFolder getDraftInfo() {
		return draftFolder;
	}

	private Folder getDraftFolder() throws WmaException {
		String name = session.getPreferences().getDraftFolder();
		return getFolder(name);
	}

	private void setDraftFolder() throws WmaException {
		try {
			Folder draft = getDraftFolder();
			if (!draft.exists()) {
				if (!draft.create(WmaFolderImpl.TYPE_MAILBOX)) {
					throw new WmaException("wma.store.createfolder.failed");
				}
			}
			// ensure subscription
			draft.setSubscribed(true);
			draftFolder = WmaFolderImpl.createLight(draft);
		} catch (MessagingException mex) {
			throw new WmaException(mex.getMessage()).setException(mex);
		}
	}

	public WmaFolder getSentMailArchive() {
		return sentMailFolder;
	}

	private Folder getSentMailFolder() throws WmaException {
		String name = session.getPreferences().getSentMailArchive();
		return getFolder(name);
	}
	
	private void setSentMailFolder() throws WmaException {
		try {
			Folder sentMail = getSentMailFolder();
			if (!sentMail.exists()) {
				if (!sentMail.create(WmaFolderImpl.TYPE_MAILBOX)) {
					throw new WmaException("wma.store.createfolder.failed");
				}
			}
			// ensure subscription
			sentMail.setSubscribed(true);
			sentMailFolder = WmaFolderImpl.createLight(sentMail);
		} catch (MessagingException mex) {
			throw new WmaException(mex.getMessage()).setException(mex);
		}
	}

	public WmaFolder getToSendArchive() {
		return toSendFolder;
	}

	private Folder getToSendFolder() throws WmaException {
		String name = session.getPreferences().getToSendFolder();
		return getFolder(name);
	}

	private void setToSendFolder() throws WmaException {
		try {
			Folder toSend = getToSendFolder();
			if (!toSend.exists()) {
				if (!toSend.create(WmaFolderImpl.TYPE_MAILBOX)) {
					throw new WmaException("wma.store.createfolder.failed");
				}
			}
			// ensure subscription
			toSend.setSubscribed(true);
			toSendFolder = WmaFolderImpl.createLight(toSend);
		} catch (MessagingException mex) {
			throw new WmaException(mex.getMessage()).setException(mex);
		}
	}

	public WmaFolder getPersonalArchive() {
		return personalFolder;
	}

	private Folder getPersonalFolder() throws WmaException {
		String name = session.getPreferences().getPersonalFolder();
		return getFolder(name);
	}

	private void setPersonalFolder() throws WmaException {
		try {
			Folder personal = getPersonalFolder();
			if (!personal.exists()) {
				if (!personal.create(WmaFolderImpl.TYPE_MIXED)) {
					throw new WmaException("wma.store.createfolder.failed");
				}
			}
			// ensure subscription
			personal.setSubscribed(true);
			personalFolder = WmaFolderImpl.createLight(personal);
		} catch (MessagingException mex) {
			throw new WmaException(mex.getMessage()).setException(mex);
		}
	}

	/**
	 * Put's a message into the sent-mail archive, if archiving is enabled.
	 * 
	 * @param message
	 *            the <tt>Message</tt> to be archived.
	 * 
	 * @throws WmaException
	 *             if it fails to archive the message.
	 * 
	 * @see javax.mail.Message
	 */
	public void archiveMail(Folder archive, Message message)
			throws WmaException {
		try {
			// open it read write
			archive.open(Folder.READ_WRITE);
			// save the message in archive, append only works as array
			Message[] tosave = { message };
			// append it
			archive.appendMessages(tosave);
			// close without expunging
			archive.close(false);
		} catch (MessagingException mex) {
			throw new WmaException("wma.store.archivemail.failed")
					.setException(mex);
		} finally {
			if (archive != null) {
				try {
					if (archive.isOpen()) {
						// close without expunging
						archive.close(false);
					}
				} catch (Exception e) {
					// ignore, will be closed anyway
				}
			}
		}
	}

	/*** end wma special folders ******************************/

	/**
	 * Closes the associated mail store.
	 */
	public void close() {
		try {
			store.close();
		} catch (Exception mex) {
			log.error(mex.getMessage(), mex);
		}
	}

	/*** folder management methods *****************************/

	/**
	 * Returns a <tt>WmaFolderImpl</tt> with the given path
	 * from the store.
	 * 
	 * @return the folder as <tt>WmaFolderImpl</tt>.
	 * @throws WmaException
	 */
	private WmaFolder getWmaFolder(String fullname) throws WmaException {
		return WmaFolderImpl.createLight(getFolder(fullname));
	}

	public Folder getFolder(String fullname) throws WmaException {
		try {
			// FIXME: Microsoft Exchange returns "" as Default Folder, but
			// asking for the folder "" does not return any subfolders.
			if (fullname.length() > 0) {
				return store.getFolder(fullname);
			} else {
				// assume to return the default folder...
				return store.getDefaultFolder();
			}
		} catch (MessagingException mex) {
			throw new WmaException("wma.store.getfolder").setException(mex);
		}
	}

	public Folder createFolder(String fullname, int type) throws WmaException {
		return null;
	}

	public void renameFolder(String fullname, String destfolder)
			throws WmaException {
	}

	public void deleteFolder(String fullname) throws WmaException {
	}

	public void deleteFolders(String[] folders) throws WmaException {
	}

	public void moveFolder(String foldername, String destfolder)
			throws WmaException {
	}

	public void moveFolders(String[] foldernames, String destfolder)
			throws WmaException {
	}

	/**
	 * Tests if a given path is a special wma folder.
	 */
	public boolean isSpecialFolder(String fullname) {
		return ("".equals(fullname)
				|| fullname.equals(inboxFolder.getPath())
				|| fullname.equals(trashFolder.getPath())
				|| fullname.equals(draftFolder.getPath())
				|| fullname.equals(sentMailFolder.getPath())
				|| fullname.equals(toSendFolder.getPath())
				|| fullname.equals(personalFolder.getPath()));
	}

	private void prepare() throws WmaException {
		try {
			Folder root = getRootFolder();

			if (!root.exists()) {
				throw new WmaException("wma.store.rootfolder");
			}
			// set folder separator
			setFolderSeparator(root.getSeparator());

			// Inbox the folder that contains the incoming mail
			// this has to exist, regarding to the IMAP specification
			inboxFolder = getWmaFolder("INBOX");

			// Trash
			setTrashFolder();
			
			// Draft
			setDraftFolder();
			
			// sent-mail archive
			setSentMailFolder();
			
			// send-mail archive
			setToSendFolder();
			
			// Personal
			setPersonalFolder();
		} catch (MessagingException ex) {
			throw new WmaException("wma.store.prepare").setException(ex);
		}
	}
	
	/**
	 * Creates a new <tt>WmaStore</tt> instance.
	 * 
	 * @param session the actual <tt>WmaSession</tt>.
	 * @param store the mail store that should be wrapped.
	 * @return the newly created <tt>WmaStore</tt> instance.
	 * @throws WmaException 
	 * 
	 * @see com.hs.mail.webmail.WmaSession
	 * @see javax.mail.Store
	 */
	public static WmaStore createStore(WmaSession session, Store store)
			throws WmaException {
		WmaStoreImpl wstore = new WmaStoreImpl(session, store);
		// prepare this store
		wstore.prepare();
		return wstore;
	}
	
}
