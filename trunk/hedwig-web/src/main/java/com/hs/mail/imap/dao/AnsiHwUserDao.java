package com.hs.mail.imap.dao;

import com.hs.mail.imap.user.Alias;
import com.hs.mail.imap.user.User;

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

}
