package com.hs.mail.imap.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.mail.Quota;

import org.springframework.jdbc.core.RowMapper;

import com.hs.mail.imap.user.Alias;
import com.hs.mail.imap.user.User;

/**
 * 
 * @author Won Chul Doh
 * @since July 26, 2015
 *
 */
abstract class AnsiUserDao extends AbstractDao implements UserDao {

	public User getUser(long id) {
		String sql = "SELECT * FROM hw_user WHERE userid = ?";
		return queryForObject(sql, new Object[] { new Long(id) },
				userMapper);
	}

	public long getUserID(String address) {
		String sql = "SELECT userid FROM hw_user WHERE loginid = ?";
		return queryForLong(sql, new Object[] { address });
	}
	
	public User getUserByAddress(String address) {
		String sql = "SELECT * FROM hw_user WHERE loginid = ?";
		return queryForObject(sql, new Object[] { address }, userMapper);
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
		return getJdbcTemplate().update(
				sql,
				new Object[] { user.getUserID(), user.getPassword(),
						new Long(user.getQuota()), user.getForwardTo(),
						new Long(user.getID()) });
	}
	
	public int deleteUser(long id) {
		String sql = "DELETE FROM hw_user WHERE userid = ?";
		return getJdbcTemplate().update(sql, new Object[] { new Long(id) });
	}
	
	public Alias getAlias(long id) {
		String sql = "SELECT a.*, u.loginid FROM hw_alias a, hw_user u WHERE a.aliasid = ? AND a.deliver_to = u.userid";
		return queryForObject(sql, new Object[] { new Long(id) },
				aliasMapper);
	}
	
	public int getAliasCount(String domain) {
		String sql = "SELECT COUNT(*) FROM hw_alias a, hw_user u WHERE a.alias LIKE ? AND a.deliver_to = u.userid";
		return queryForInt(
				sql,
				new Object[] { new StringBuilder("%@").append(escape(domain))
						.toString() });
	}

	public List<Alias> expandAlias(String alias) {
		String sql = "SELECT a.*, u.loginid FROM hw_alias a, hw_user u WHERE a.alias = ? AND a.deliver_to = u.userid";
		return getJdbcTemplate()
				.query(sql, new Object[] { alias }, aliasMapper);
	}
	
	public int updateAlias(Alias alias) {
		String sql = "UPDATE hw_alias SET alias = ?, deliver_to = ? WHERE aliasid = ?";
		return getJdbcTemplate()
				.update(
						sql,
						new Object[] { alias.getAlias(),
								new Long(alias.getDeliverTo()),
								new Long(alias.getID()) });
	}
	
	public int deleteAlias(long id) {
		String sql = "DELETE FROM hw_alias WHERE aliasid = ?";
		return getJdbcTemplate().update(sql, new Object[] { new Long(id) });
	}
	
	public long getQuotaLimit(long ownerID) {
		String sql = "SELECT maxmail_size FROM hw_user WHERE userid = ?";
		long limit = queryForLong(sql, new Object[] { new Long(ownerID) });
		return limit * 1024 * 1024;
	}

	public Quota getQuota(long ownerID, long mailboxID, String quotaRoot) {
		Quota quota = new Quota(quotaRoot);
		quota.setResourceLimit("STORAGE", getQuotaLimit(ownerID));
		quota.resources[0].usage = getQuotaUsage(ownerID, mailboxID);
		return quota;
	}
	
	public void setQuota(long ownerID, Quota quota) {
		String sql = "UPDATE hw_user SET maxmail_size = ? WHERE userid = ?";
		for (int i = 0; i < quota.resources.length; i++) {
			if ("STORAGE".equals(quota.resources[i].name)) {
				getJdbcTemplate().update(
						sql,
						new Object[] { new Long(quota.resources[i].limit),
								new Long(ownerID) });
				quota.resources[i].usage = getQuotaUsage(ownerID, 0);
				return;
			}
		}
	}

	protected static RowMapper<User> userMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setID(rs.getLong("userid"));
			user.setUserID(rs.getString("loginid"));
			user.setPassword(rs.getString("passwd"));
			user.setQuota(rs.getLong("maxmail_size"));
			user.setForwardTo(rs.getString("forward"));
			return user;
		}
	};
	
	protected static RowMapper<Alias> aliasMapper = new RowMapper<Alias>() {
		public Alias mapRow(ResultSet rs, int rowNum) throws SQLException {
			Alias alias = new Alias();
			alias.setID(rs.getLong("aliasid"));
			alias.setAlias(rs.getString("alias"));
			alias.setDeliverTo(rs.getLong("deliver_to"));
			alias.setUserID(rs.getString("loginid"));
			return alias;
		}
	};
	
}
