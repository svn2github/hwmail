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

	public long getUserID(String address) {
		String sql = "SELECT userid FROM hw_user WHERE loginid = ?";
		return queryForLong(sql, new Object[] { address });
	}
	
	public User getUserByAddress(String address) {
		String sql = "SELECT * FROM hw_user WHERE loginid = ?";
		return queryForObject(sql, new Object[] { address }, userMapper);
	}
	
	public List<Alias> expandAlias(String alias) {
		String sql = "SELECT * FROM hw_alias WHERE alias = ?";
		return getJdbcTemplate()
				.query(sql, new Object[] { alias }, aliasMapper);
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
			alias.setDeliverTo(rs.getString("deliver_to"));
			return alias;
		}
	};
	
}
