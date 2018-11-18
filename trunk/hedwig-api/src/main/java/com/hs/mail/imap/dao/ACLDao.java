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
	
	public String getRights(long userID, long mailboxID, boolean includeAnyone);

	public void setRights(long userID, long mailboxID, String rights);

	public void setRights(long userID, long mailboxID, String rights, boolean set);

	public MailboxACL getACL(long mailboxID);

	public boolean hasRights(long userID, long mailboxID, String rights);

	public List<Long> getAuthorizedMailboxes(long userID, String rights);

}
