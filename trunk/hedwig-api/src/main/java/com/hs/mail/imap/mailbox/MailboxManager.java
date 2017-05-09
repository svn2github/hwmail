/*
 * Copyright 2010 the original author or authors.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.hs.mail.imap.mailbox;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.Flags;

import com.hs.mail.imap.event.EventDispatcher;
import com.hs.mail.imap.event.EventListener;
import com.hs.mail.imap.mailbox.MailboxACL.EditMode;
import com.hs.mail.imap.message.FetchData;
import com.hs.mail.imap.message.MailMessage;
import com.hs.mail.imap.message.search.SearchKey;
import com.hs.mail.imap.message.search.SortKey;

/**
 * 
 * @author WonChul Doh
 * @since Feb 2, 2010
 * 
 */
public interface MailboxManager {

	public EventDispatcher getEventDispatcher();

	public void addEventListener(EventListener listener);

	public void removeEventListener(EventListener listener);

	/**
	 * Returns user's <code>Mailbox</code> with a given path
	 * 
	 * @param ownerID
	 *            owner of the mailbox
	 * @param mailboxName
	 *            fully qualified name of the mailbox
	 * @return the mailbox as <code>Mailbox</code>
	 */
	public Mailbox getMailbox(long ownerID, String mailboxName);

	/**
	 * Check if given mailbox exists.
	 * 
	 * @param ownerID
	 *            owner of this mailbox
	 * @param mailboxName
	 *            fully qualified name of the mailbox for checking existence
	 * @return true when the mailbox exists, false otherwise
	 */
	public boolean mailboxExists(long ownerID, String mailboxName);

	/**
	 * Get the child mailboxes of the give mailbox.
	 * 
	 * @param userID
	 *            ID of user
	 * @param ownerID
	 *            owner of the given mailbox
	 * @param mailboxName
	 *            fully qualified name of the mailbox to retrieve children
	 * @param subscribed
	 *            true to filter only subscribed mailboxes
	 * @return the children of the given mailbox
	 */
	public List<Mailbox> getChildren(long userID, long ownerID,
			String mailboxName, boolean subscribed);
	
	public List<Long> getMailboxIDList(String mailboxName);

	/**
	 * Does the mailbox have inferior child mailboxes?
	 * 
	 * @param mailbox
	 *            the mailbox to test 
	 * @return true when the mailbox has children, false otherwise
	 */
	public boolean hasChildren(Mailbox mailbox);

	/**
	 * Gets the UIDs of messages which has /Deleted flag.
	 * 
	 * @param mailboxID
	 *            ID of mailbox
	 * @return list of UIDs of messages which has /Deleted flag
	 */
	public List<Long> expunge(long mailboxID);

	/**
	 * Searches for the messages matching the given query.
	 * 
	 * @param map
	 *            UID to MSN converting map
	 * @param mailboxID
	 *            ID of mailbox to search
	 * @param key
	 *            the search query
	 * @return list of UIDs of messages searched
	 */
	public List<Long> search(UidToMsnMapper map, long mailboxID, SearchKey key,
			List<SortKey> sortKeys);

	/**
	 * Creates a new mailbox. Any intermediary mailboxes missing from the
	 * hierarchy will be created.
	 * 
	 * @param ownerID
	 *            owner of the mailbox
	 * @param mailboxName
	 *            fully qualified name of the mailbox to create
	 * @return <code>Mailbox</code> created
	 */
	public Mailbox createMailbox(final long ownerID, final String mailboxName);

	/**
	 * Renames a mailbox.
	 * 
	 * @param source
	 *            original mailbox
	 * @param targetName
	 *            new name for the mailbox
	 */
	public void renameMailbox(final Mailbox source, final String targetName);
	
	/**
	 * Delete or empty a mailbox from store.
	 * 
	 * @param ownerID
	 *            ID of the user who owns the mailbox
	 * @param mailboxID
	 *            ID of the mailbox
	 * @param delete
	 *            true when delete, false when empty
	 */
	public void deleteMailbox(final long ownerID, final long mailboxID,
			final boolean delete);

	/**
	 * Check if given mailbox is subscribed to user
	 * 
	 * @param userID
	 *            ID of user
	 * @param mailboxName
	 *            fully qualified name of the mailbox
	 * @return true if mailbox is subscribed, otherwise false
	 */
	public boolean isSubscribed(long userID, String mailboxName);

	/**
	 * Subscribe the user to the given mailbox.
	 * 
	 * @param userID
	 *            ID of the user
	 * @param mailboxID
	 *            ID of the mailbox to subscribe
	 * @param mailboxName
	 *            fully qualified name of the mailbox
	 */
	public void addSubscription(final long userID, final long mailboxID,
			final String mailboxName);
	/**
	 * Cancel subscription of the given mailbox by the user.
	 * 
	 * @param userID
	 *            ID of the user
	 * @param mailboxName
	 *            fully qualified name of the mailbox to cancel subscription
	 */
	public void deleteSubscription(final long userID, final String mailboxName);

	/**
	 * Get the fetch data of message.
	 * 
	 * @param uid
	 *            ID of the message to fetch data
	 * @return the <code>FetchData</code> of the message
	 */
	public FetchData getMessageFetchData(long uid);

	/**
	 * Returns the <code>Flags</code> object containing the flags of the message
	 * 
	 * @param uid
	 *            UID of the message
	 * @return <code>Flags</code> object containing the flags of the message
	 */
	public Flags getFlags(long uid);

	/**
	 * Returns all the ID of the messages stored in the given mailbox.
	 * 
	 * @param mailboxID
	 *            ID of the mailbox
	 * @return all the ID of messages in the given mailbox
	 */
	public List<Long> getMessageIDList(long mailboxID);

	/**
	 * Appends a message to the user's INBOX. This method is called by MDA.
	 */
	public void addMessage(final long ownerID, final MailMessage message,
			String mailboxName);

	/**
	 * Appends a message to this mailbox.
	 * 
	 * @param mailboxID
	 *            ID of mailbox to append a message
	 * @param internalDate
	 *            the time of addition to be set
	 * @param flags
	 *            optionally set these flags on created message, or null when no
	 *            additional flags should be set
	 * @param file
	 *            the file containing the mail contents
	 * @throws IOException
	 */
	public MailMessage appendMessage(long mailboxID, Date internalDate,
			Flags flags, File file) throws IOException;
	
	/**
	 * Deletes the given message.
	 * 
	 * @param uid
	 *            ID of the message to delete
	 */
	public void deleteMessage(final long uid);

	/**
	 * Deletes dangling messages (physical messages which are not referenced by
	 * any mailbox message).
	 */
	public void purgeMessages();

	/**
	 * Copies a message to the given mailbox.
	 * 
	 * @param uid
	 *            ID of the message to copy
	 * @param mailboxID
	 *            ID of mailbox where message will be copied
	 */
	public void copyMessage(final long uid, final long mailboxID);

	/**
	 * Clear \Recent flag for the messages in this mailbox.
	 * 
	 * @param mailboxID
	 *            ID of the mailbox to clear \Recent flag
	 */
	public void resetRecent(final long mailboxID);

	/**
	 * Sets flags on this message.
	 * 
	 * @param uid
	 *            ID of the message to set flags
	 * @param flags
	 *            flags to be set
	 * @param replace
	 *            true if to replace
	 * @param set
	 *            true if to set, false to reset
	 */
	public void setFlags(final long uid, final Flags flags, final boolean replace, final boolean set);

	/**
	 * Gets the headers of the message.
	 * 
	 * @param physMessageID
	 *            ID of the physical message
	 * @return map containing header name and value entries
	 */
	public Map<String, String> getHeader(long physMessageID);
	
	/**
	 * Gets the headers of the message.
	 * 
	 * @param physMessageID
	 *            ID of the physical message
	 * @param fields
	 *            Array of fields to retrieve
	 * @return map containing header name and value entries
	 */
	public Map<String, String> getHeader(long physMessageID, String[] fields);

	/**
	 * Get all the messages having the given RFC822 "Message-ID" from the given
	 * users mailboxes.
	 * 
	 * @param userID
	 *            ID of user
	 * @param messageID
	 *            RFC822 Message-ID value
	 * @return the list of the messages with same Message-ID
	 */
	public List<Map<String, Object>> getMessageByMessageID(long userID, String messageID);

	/**
	 * Get all the rights allowed to the user for the given mailbox.
	 * 
	 * @param userID
	 *            ID of the user to retrieve the rights
	 * @param mailboxID
	 *            ID of the mailbox controlled by the access control list
	 * @return all the rights allowed to the user
	 */
	public String getRights(long userID, long mailboxID);

	/**
	 * Set the rights to the access control entry of the user for the given
	 * mailbox.
	 * 
	 * @param userID
	 *            the ID of user of the access control entry
	 * @param mailboxID
	 *            ID of the mailbox of access control list
	 * @param editMode
	 *            editing mode
	 * @param rights
	 *            the rights of the access control entry
	 */
	public void setACL(long userID, long mailboxID, EditMode editMode, String rights);

	/**
	 * Get the access control list entries for the given mailbox
	 * 
	 * @param mailboxID
	 *            the mailbox to retrieve the access control list
	 * @return access control list of the mailbox
	 */
	public MailboxACL getACL(long mailboxID);

	/**
	 * Check if given user has right on the mailbox
	 * 
	 * @param userID
	 *            the ID of user to check right
	 * @param mailboxName
	 *            name of the mailbox to check right
	 * @param right
	 *            the right to check
	 * @return true if the user has right on the mailbox, otherwise false
	 */
	public boolean hasRight(long userID, String mailboxName, char right);

}
