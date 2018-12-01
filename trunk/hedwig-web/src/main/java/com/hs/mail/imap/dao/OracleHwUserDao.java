package com.hs.mail.imap.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.user.Alias;
import com.hs.mail.imap.user.User;
import com.hs.mail.web.model.PublicFolder;

public class OracleHwUserDao extends AnsiHwUserDao {

	public List<User> getUserList(String domain, int page, int pageSize) {
		int offset = (page - 1) * pageSize;
		String sql = 
				"SELECT * "
				+ "FROM (SELECT u.*, ROW_NUMBER() OVER( ORDER BY loginid ) rn "
						+ "FROM hw_user u "
						+ "WHERE u.loginid LIKE ?) "
				+ "WHERE rn BETWEEN ? AND ?";
		return getJdbcTemplate().query(sql,
				new Object[] {
						new StringBuilder("%@").append(escape(domain)).toString(), 
						new Integer(offset + 1),
						new Integer(offset + pageSize) }, AnsiUserDao.userMapper);
	}

	public long addUser(final User user) {
		final String sql = 
				"INSERT "
				+ "INTO hw_user (userid, loginid, passwd, maxmail_size, forward) "
				+ "VALUES(sq_hw_user.NEXTVAL, ?, ?, ?, ?)";
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
		String sql = 
				"SELECT * "
				+ "FROM (SELECT a.*, ROW_NUMBER() OVER( ORDER BY alias ) rn "
						+ "FROM hw_alias a "
						+ "WHERE a.alias LIKE ?) "
				+ "WHERE rn BETWEEN ? AND ?";
		return getJdbcTemplate().query(sql,
				new Object[] {
						new StringBuilder("%@").append(escape(domain)).toString(), 
						new Integer(offset + 1),
						new Integer(offset + pageSize) }, AnsiUserDao.aliasMapper);
	}

	public long addAlias(final Alias alias) {
		final String sql = 
				"INSERT "
				+ "INTO hw_alias (aliasid, alias, deliver_to) "
				+ "VALUES(sq_hw_alias.NEXTVAL, ?, ?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement pstmt = con.prepareStatement(sql,
						new String[] { "aliasid" });
				pstmt.setString(1, alias.getAlias());
				pstmt.setString(2, alias.getDeliverTo());
				return pstmt;
			}
		}, keyHolder);
		long id = keyHolder.getKey().longValue();
		alias.setID(id);
		return id;
	}
	
	public PublicFolder getPublicFolder(String namespace, long mailboxID) {
		final String sql = 
				"SELECT mailboxid,name,nvl(aliasid,0) aliasid,alias "
				+ "FROM hw_mailbox m LEFT OUTER JOIN hw_alias a ON m.name=a.deliver_to "
				+ "WHERE m.mailboxid = ?";
		final String prefix = new StringBuilder(ImapConstants.SHARED_PREFIX)
				.append(escape(namespace)).append(Mailbox.folderSeparator)
				.toString();
		return getJdbcTemplate().queryForObject(sql,
				new Object[] { mailboxID },
				new PublicFolderRowMapper(namespace, prefix));
	}

	public List<PublicFolder> getPublicFolders(long ownerid, final String namespace) {
		final String sql = 
				"SELECT mailboxid,name,nvl(aliasid,0) aliasid,alias "
				+ "FROM hw_mailbox m LEFT OUTER JOIN hw_alias a ON m.name=a.deliver_to "
				+ "WHERE m.ownerid = ? AND m.name LIKE ?";
		final String prefix = new StringBuilder(ImapConstants.SHARED_PREFIX)
				.append(escape(namespace)).append(Mailbox.folderSeparator)
				.toString();
		return getJdbcTemplate().query(sql,
				new Object[] { ownerid, new StringBuilder(prefix).append('%').toString() },
				new PublicFolderRowMapper(namespace, prefix));
	}

	public List<Map<String, Object>> getHeaderCounts() {
		final String sql = 
				"SELECT COUNT(1) AS count, n.headernameid, n.headername "
				+ "FROM hw_headervalue v LEFT JOIN hw_headername n ON v.headernameid = n.headernameid "
				+ "GROUP BY n.headernameid, n.headername "
				+ "ORDER BY count DESC";
		return getJdbcTemplate().queryForList(sql);
	}
	
}
