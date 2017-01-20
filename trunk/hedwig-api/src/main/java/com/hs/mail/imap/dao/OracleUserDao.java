package com.hs.mail.imap.dao;


/**
 * 
 * @author Won Chul Doh
 * @since July 26, 2015
 *
 */
public class OracleUserDao extends AnsiUserDao {

	public long getQuotaUsage(long ownerID, long mailboxID) {
		if (mailboxID != 0) {
			String sql = "SELECT NVL(SUM(rfcsize), 0) FROM hw_message m, hw_physmessage p WHERE m.mailboxid=? AND m.physmessageid=p.physmessageid";
			return queryForLong(sql, new Object[] { new Long(mailboxID) });
		} else {
			String sql = "SELECT NVL(SUM(rfcsize), 0) FROM hw_mailbox b, hw_message m, hw_physmessage p WHERE b.ownerid=? AND b.mailboxid=m.mailboxid AND m.physmessageid=p.physmessageid";
			return queryForLong(sql, new Object[] { new Long(ownerID) });
		}
	}
	
}
