package com.hs.mail.imap.dao;

import java.util.List;

import com.hs.mail.imap.mailbox.MailboxACL;

/**
 * 
 * @author Wonchul Doh
 * @since December 2, 2016
 *
 */
public interface ACLDao extends DaoSupport {
	
	public String getRights(long userID, long mailboxID);

	public void setRights(long userID, long mailboxID, String rights);

	public void setRights(long userID, long mailboxID, String rights, boolean set);

	public MailboxACL getACL(long mailboxID);

	public boolean hasRight(long userID, String mailboxName, char right);

	public List<Long> getGrantedMailboxes(long userID, char right);

}
