package com.hs.mail.imap.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hs.mail.imap.user.Alias;
import com.hs.mail.imap.user.User;
import com.hs.mail.web.model.PublicFolder;

abstract class AnsiHwUserDao extends AbstractDao implements HwUserDao {

	public User getUser(long id) {
		String sql = "SELECT * FROM hw_user WHERE userid = ?";
		return queryForObject(sql, new Object[] { new Long(id) },
				AnsiUserDao.userMapper);
	}

	public int getUserCount(String domain) {
		String sql = "SELECT COUNT(*) FROM hw_user WHERE loginid LIKE ?";
		return queryForInt(
				sql,
				new Object[] { new StringBuilder("%@").append(escape(domain))
						.toString() });
	}

	public int updateUser(User user) {
		String sql = "UPDATE hw_user SET loginid = ?, passwd = ?, maxmail_size = ?, forward = ? WHERE userid = ?";
		return getJdbcTemplate().update(sql, 
				user.getUserID(),
				user.getPassword(), 
				user.getQuota(), 
				user.getForwardTo(),
				user.getID());
	}
	
	public int deleteUser(long id) {
		String[] sqls = {"DELETE FROM hw_subscription WHERE userid = ?",
				"DELETE FROM hw_acl WHERE userid = ?",
				"DELETE FROM hw_user WHERE userid = ?"};
		return update(sqls, id);
	}

	public Alias getAlias(long id) {
		String sql = "SELECT * FROM hw_alias WHERE aliasid = ?";
		return queryForObject(sql, new Object[] { new Long(id) },
				AnsiUserDao.aliasMapper);
	}

	public int getAliasCount(String domain) {
		String sql = "SELECT COUNT(*) FROM hw_alias WHERE alias LIKE ?";
		return queryForInt(sql,
				new Object[] { new StringBuilder("%@").append(escape(domain))
						.toString() });
	}
	
	public int updateAlias(Alias alias) {
		String sql = "UPDATE hw_alias SET alias = ?, deliver_to = ? WHERE aliasid = ?";
		return getJdbcTemplate().update(
				sql,
				new Object[] { alias.getAlias(), alias.getDeliverTo(),
						alias.getID() });
	}
	
	public int deleteAlias(long id) {
		String sql = "DELETE FROM hw_alias WHERE aliasid = ?";
		return getJdbcTemplate().update(sql, new Object[] { new Long(id) });
	}

	public int deleteHeaderValues(String headerNameID) {
		final String sql = "DELETE FROM hw_headervalue WHERE headernameid = ?";
		return getJdbcTemplate().update(sql, headerNameID);
	}

	class PublicFolderRowMapper implements RowMapper<PublicFolder> {

		private String namespace;
		private String prefix;

		public PublicFolderRowMapper(String namespace, String prefix) {
			this.namespace = namespace;
			this.prefix = prefix;
		}

		@Override
		public PublicFolder mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			PublicFolder pf = new PublicFolder();
			pf.setNamespace(namespace);
			pf.setMailboxID(rs.getLong("mailboxid"));
			pf.setName(rs.getString("name").substring(prefix.length()));
			pf.setAliasID(rs.getLong("aliasid"));
			pf.setSubmissionAddress(rs.getString("alias"));
			return pf;
		}

	}
	
}
