package com.hs.mail.imap.dao;

import com.hs.mail.imap.mailbox.MailboxACL;

public interface ACLDao {

	public void setRights(long userID, long mailboxID, String rights);
	
	public void setRights(long userID, long mailboxID, String rights, boolean set);

	public MailboxACL getACL(long mailboxID);

}
