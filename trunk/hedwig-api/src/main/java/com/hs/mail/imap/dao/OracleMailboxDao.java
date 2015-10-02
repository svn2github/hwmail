package com.hs.mail.imap.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.hs.mail.imap.mailbox.Mailbox;

/**
 * 
 * @author Won Chul Doh
 * @since July 26, 2015
 *
 */
public class OracleMailboxDao extends AnsiMailboxDao {

	public Mailbox getMailbox(long ownerID, String mailboxName) {
		String sql = "SELECT * FROM hw_mailbox WHERE ownerid = ? AND name = ?";
		return queryForObject(sql, new Object[] { new Long(ownerID),
				mailboxName }, mailboxRowMapper);
	}

	public boolean mailboxExists(long ownerID, String mailboxName) {
		String sql = "SELECT COUNT(1) FROM hw_mailbox WHERE ownerid = ? AND name = ?";
		return queryForInt(sql, new Object[] { new Long(ownerID), mailboxName }) > 0;
	}

	@Override
	protected Mailbox doCreateMailbox(final long ownerID, final String mailboxName) {
		final String sql = "INSERT INTO hw_mailbox (mailboxid, name, ownerid, nextuid, uidvalidity) VALUES(sq_hw_mailbox.NEXTVAL, ?, ?, ?, ?)";
		final long uidValidity = System.currentTimeMillis();
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement pstmt = con.prepareStatement(sql, 
						new String[] { "mailboxid" });
				pstmt.setString(1, mailboxName);
				pstmt.setLong(2, ownerID);
				pstmt.setLong(3, 1);
				pstmt.setLong(4, uidValidity);
				return pstmt;
			}
		}, keyHolder);
		
		Mailbox mailbox = new Mailbox();
		mailbox.setMailboxID(keyHolder.getKey().longValue());
		mailbox.setOwnerID(ownerID);
		mailbox.setName(mailboxName);
		mailbox.setNextUID(1);
		mailbox.setUidValidity(uidValidity);
		
		return mailbox;
	}

	@Override
	protected List<Mailbox> getChildren(long ownerID, String mailboxName) {
		if (StringUtils.isEmpty(mailboxName)) {
			String sql = "SELECT * FROM hw_mailbox WHERE ownerid = ? ORDER BY name";
			return getJdbcTemplate().query(sql,
					new Object[] { new Long(ownerID) }, mailboxRowMapper);
		} else {
			String sql = "SELECT * FROM hw_mailbox WHERE ownerid = ? AND name LIKE ? ORDER BY name";
			return getJdbcTemplate().query(
					sql,
					new Object[] {
							new Long(ownerID),
							new StringBuilder(escape(mailboxName))
									.append(Mailbox.folderSeparator).append('%')
									.toString() }, mailboxRowMapper);
		}
	}
	
	public int getChildCount(long ownerID, String mailboxName) {
		if (StringUtils.isEmpty(mailboxName)) {
			String sql = "SELECT COUNT(1) FROM hw_mailbox WHERE ownerid = ?";
			Object[] params = { new Long(ownerID) };
			return queryForInt(sql, params);
		} else {
			String sql = "SELECT COUNT(1) FROM hw_mailbox WHERE ownerid = ? AND name LIKE ?";
			Object[] params = {
					new Long(ownerID),
					new StringBuilder(escape(mailboxName)).append(
							Mailbox.folderSeparator).append('%').toString() };
			return queryForInt(sql, params);
		}
	}
	
	public long getFirstUnseenMessageID(long mailboxID) {
		String sql = "SELECT messageid FROM (SELECT messageid FROM hw_message WHERE mailboxid = ? AND seen_flag = 'N' ORDER BY messageid) WHERE rownum = 1";
		return queryForLong(sql, new Object[] { new Long(mailboxID) });
	}
	
	public List<Long> getGarbageMailboxList() {
		String sql = "SELECT m.mailboxid FROM hw_mailbox m WHERE NOT EXISTS (SELECT 1 FROM hw_user u WHERE u.userid = m.ownerid)";
		return getJdbcTemplate().queryForList(sql, Long.class);
	}
	
}
