package com.hs.mail.imap.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.hs.mail.imap.user.Alias;
import com.hs.mail.imap.user.User;

public class MySqlHwUserDao extends AnsiHwUserDao implements HwUserDao {

	public List<User> getUserList(String domain, int page, int pageSize) {
		int offset = (page - 1) * pageSize;
		String sql = "SELECT * FROM hw_user WHERE loginid LIKE ? ORDER BY loginid LIMIT ?, ?";
		return getJdbcTemplate().query(sql,
				new Object[] {
						new StringBuilder("%@").append(escape(domain)).toString(), 
						new Integer(offset),
						new Integer(pageSize) }, AnsiUserDao.userMapper);
	}
	
	public long addUser(final User user) {
		final String sql = "INSERT INTO hw_user (loginid, passwd, maxmail_size, forward) VALUES(?, ?, ?, ?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement pstmt = con.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
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
		String sql = "SELECT * FROM hw_alias WHERE alias LIKE ? ORDER BY alias LIMIT ?, ?";
		return getJdbcTemplate().query(sql,
				new Object[] {
						new StringBuilder("%@").append(escape(domain)).toString(), 
						new Integer(offset),
						new Integer(pageSize) }, AnsiUserDao.aliasMapper);
	}

	public long addAlias(final Alias alias) {
		final String sql = "INSERT INTO hw_alias (alias, deliver_to) VALUES(?, ?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement pstmt = con.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, alias.getAlias());
				pstmt.setString(2, alias.getDeliverTo());
				return pstmt;
			}
		}, keyHolder);
		long id = keyHolder.getKey().longValue();
		alias.setID(id);
		return id;
	}
	
}
