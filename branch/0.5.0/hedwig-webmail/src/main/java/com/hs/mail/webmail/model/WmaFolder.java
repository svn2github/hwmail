package com.hs.mail.webmail.model;

import java.util.List;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.impl.WmaDisplayMessage;

/**
 * An interface defining the contract for interaction with
 * the WmaFolder model.
 * <p>
 * The JwmaFolder allows a view programmer to obtain
 * information about a folder.
 * 
 * @author WonChul,Do
 *
 */
public interface WmaFolder {

	Folder getFolder();

	/**
	 * Returns a <tt>String</tt> representing the name
	 * of this folder.
	 * 
	 * @return the name of this folder as String.
	 */
	String getName();
	
	/**
	 * Returns a <tt>String</tt> representing the path
	 * of this folder object.
	 * 
	 * @return the path of this folder as String.
	 */
	String getPath();
	
	/**
	 * Returns an <tt>int</tt> representing the type
	 * of this folder.
	 * 
	 * @return the type of this folder object as <tt>int</tt>.
	 */
	int getType();
	
	boolean isParent(WmaFolder child, char separator);

	/**
	 * Convenience method that returns a<tt>WmaFolder[]</tt>
	 * containing all subfolders within this folder.
	 * <p>
	 * If this folder does not contain any subfolder, then this
	 * method returns an empty array. Otherwise it contains
	 * one <tt>WmaFolder</tt> for each subfolder.
	 * 
	 * @return a <tt>WmaFolder[]</tt> containing all subfolders of
	 *         this folder. The array will be empty if there are none.
	 */
	List<WmaFolder> getSubfolders();
	
	void setSubfolders(List<WmaFolder> subfolders);
	
	WmaFolder addChild(WmaFolder folder);
	
	boolean hasSubfolders();
	
	int getMessageCount();
	
	WmaDisplayMessage getWmaMessage(int num) throws WmaException;

	WmaDisplayMessage getWmaMessage(int num, String msgid) throws WmaException;
	
	MimeMessage getMimeMessage(String msgid) throws WmaException;

	void deleteMessages(WmaStore wstore, int[] numbers, boolean purge)
			throws WmaException;

	void copyMessages(WmaStore wstore, int[] numbers, String destfolder,
			boolean move) throws WmaException;

	void setFlagMessages(int[] numbers, Flags flags, boolean set)
			throws WmaException;
	
	void writeMimeMessage(HttpServletResponse response, int num, String filename)
			throws WmaException;

    /**
	 * Defines folder type that can only hold messages.
	 */
	static final int TYPE_MAILBOX = Folder.HOLDS_MESSAGES;
	
	/**
	 * Defines folder type that can only hold folders.
	 */
	static final int TYPE_FOLDER = Folder.HOLDS_FOLDERS;
	
	/**
	 * Defines folder type that can hold messages and folders.
	 */
	static final int TYPE_MIXED = TYPE_MAILBOX + TYPE_FOLDER;
	
	/**
	 * Defines a virtual type that represents all folders that
	 * can hold messages.
	 */
	static final int TYPE_MESSAGE_CONTAINER = TYPE_MAILBOX + TYPE_MIXED;
	
	/**
	 * Defines a virtual type that represents all folders that can
	 * hold folders.
	 */
	static final int TYPE_FOLDER_CONTAINER = TYPE_FOLDER + TYPE_MIXED;
	
	/**
	 * Defines a virtual type that represents all of the above.
	 */
	static final int TYPE_ALL = 10;

}
