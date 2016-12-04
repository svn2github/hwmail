package com.hs.mail.imap.dao;

import com.hs.mail.imap.UnsupportedRightException;
import com.hs.mail.imap.mailbox.MailboxACL;

/**
 * 
 * @author Wonchul Doh
 * @since December 2, 2016
 *
 */
public interface ACLDao {

	public String getRights(long userID, long mailboxID);

	public void setRights(long userID, long mailboxID, String rights) throws UnsupportedRightException;

	public void setRights(long userID, long mailboxID, String rights, boolean set) throws UnsupportedRightException;

	public MailboxACL getACL(long mailboxID);

}
