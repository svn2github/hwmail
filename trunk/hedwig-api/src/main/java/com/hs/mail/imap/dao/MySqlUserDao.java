/*
 * Copyright 2010 the original author or authors.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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

/**
 * 
 * @author Won Chul Doh
 * @since Mar 23, 2010
 *
 */
public class MySqlUserDao extends AnsiUserDao {

	public List<User> getUserList(String domain, int page, int pageSize) {
		int offset = (page - 1) * pageSize;
		String sql = "SELECT * FROM hw_user WHERE loginid LIKE ? ORDER BY loginid LIMIT ?, ?";
		return getJdbcTemplate().query(sql,
				new Object[] {
						new StringBuilder("%@").append(escape(domain)).toString(), 
						new Integer(offset),
						new Integer(pageSize) }, userMapper);
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
						new Integer(pageSize) }, aliasMapper);
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
	
	public long getQuotaUsage(long ownerID, long mailboxID) {
		if (mailboxID != 0) {
			String sql = "SELECT SUM(rfcsize) FROM hw_message m, hw_physmessage p WHERE m.mailboxid = ?  AND m.physmessageid=p.physmessageid";
			return queryForLong(sql, new Object[] { new Long(mailboxID) });
		} else {
			String sql = "SELECT SUM(rfcsize) FROM hw_mailbox b, hw_message m, hw_physmessage p WHERE b.ownerid=? AND b.mailboxid=m.mailboxid AND m.physmessageid=p.physmessageid";
			return queryForLong(sql, new Object[] { new Long(ownerID) });
		}
	}

}
