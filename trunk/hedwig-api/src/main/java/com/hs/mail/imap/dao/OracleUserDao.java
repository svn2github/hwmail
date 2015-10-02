package com.hs.mail.imap.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.hs.mail.imap.user.Alias;
import com.hs.mail.imap.user.User;

/**
 * 
 * @author Won Chul Doh
 * @since July 26, 2015
 *
 */
public class OracleUserDao extends AnsiUserDao {

	public List<User> getUserList(String domain, int page, int pageSize) {
		int offset = (page - 1) * pageSize;
		String sql = "SELECT * FROM (SELECT u.*, ROW_NUMBER() OVER( ORDER BY loginid ) rn FROM hw_user u WHERE u.loginid LIKE ?) WHERE rn BETWEEN ? AND ?";
		return getJdbcTemplate().query(sql,
				new Object[] {
						new StringBuilder("%@").append(escape(domain)).toString(), 
						new Integer(offset + 1),
						new Integer(offset + pageSize) }, userMapper);
	}

	public long addUser(final User user) {
		final String sql = "INSERT INTO hw_user (userid, loginid, passwd, maxmail_size, forward) VALUES(sq_hw_user.NEXTVAL, ?, ?, ?, ?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement pstmt = con.prepareStatement(sql, new String[] { "userid" });
				pstmt.setString(1, user.getUserID());
				pstmt.setString(2, user.getPassword());
				pstmt.setLong(3, user.getQuota());
				pstmt.setString(4, user.getForwardTo());
				return pstmt;
			}
		}, keyHolder);
		long id = keyHolder.getKey().longValue();
		user.setID(id);
		return id;
	}

	public List<Alias> getAliasList(String domain, int page, int pageSize) {
		int offset = (page - 1) * pageSize;
		String sql = "SELECT * FROM (SELECT a.*, u.loginid, ROW_NUMBER() OVER( ORDER BY a.alias ) rn FROM hw_alias a, hw_user u WHERE a.alias LIKE ? AND a.deliver_to = u.userid) WHERE rn BETWEEN ? AND ?";
		return getJdbcTemplate().query(sql,
				new Object[] {
						new StringBuilder("%@").append(escape(domain)).toString(), 
						new Integer(offset + 1),
						new Integer(offset + pageSize) }, aliasMapper);
	}

	public long addAlias(final Alias alias) {
		final String sql = "INSERT INTO hw_alias (aliasid, alias, deliver_to) VALUES(sq_hw_alias.NEXTVAL, ?, ?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement pstmt = con.prepareStatement(sql, new String[] { "aliasid" });
				pstmt.setString(1, alias.getAlias());
				pstmt.setLong(2, alias.getDeliverTo());
				return pstmt;
			}
		}, keyHolder);
		long id = keyHolder.getKey().longValue();
		alias.setID(id);
		return id;
	}

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
