package com.hs.mail.imap.dao;

import com.hs.mail.imap.mailbox.MailboxACL;

/**
 * 
 * @author Wonchul Doh
 * @since December 2, 2016
 *
 */
public interface ACLDao {
	
	public String getRights(long userID, long mailboxID);

	public void setRights(long userID, long mailboxID, String rights);

	public void setRights(long userID, long mailboxID, String rights,
			boolean set);

	public MailboxACL getACL(long mailboxID);

}
