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

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.hs.mail.imap.mailbox.Mailbox;

/**
 * 
 * @author Won Chul Doh
 * @since Mar 23, 2010
 *
 */
public class MySqlMailboxDao extends AnsiMailboxDao {

	public Mailbox getMailbox(long ownerID, String mailboxName) {
		String sql = "SELECT * FROM hw_mailbox USE INDEX (ix_hw_mailbox_1) WHERE ownerid = ? AND name = ?";
		return queryForObject(sql, new Object[] { new Long(ownerID),
				mailboxName }, mailboxRowMapper);
	}

	public boolean mailboxExists(long ownerID, String mailboxName) {
		String sql = "SELECT COUNT(1) FROM hw_mailbox USE INDEX (ix_hw_mailbox_1) WHERE ownerid = ? AND name = ?";
		return queryForInt(sql, new Object[] { new Long(ownerID), mailboxName }) > 0;
	}

	@Override
	protected Mailbox doCreateMailbox(final long ownerID, final String mailboxName) {
		final String sql = "INSERT INTO hw_mailbox (name, ownerid, noselect_flag, nextuid, uidvalidity) VALUES(?, ?, ?, ?, ?)";
		final long uidValidity = System.currentTimeMillis();
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement pstmt = con.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, mailboxName);
				pstmt.setLong(2, ownerID);
				pstmt.setString(3, "N");
				pstmt.setLong(4, 1);
				pstmt.setLong(5, uidValidity);
				return pstmt;
			}
		}, keyHolder);
		
		Mailbox mailbox = new Mailbox();
		mailbox.setMailboxID(keyHolder.getKey().longValue());
		mailbox.setOwnerID(ownerID);
		mailbox.setName(mailboxName);
		mailbox.setNoSelect(false);
		mailbox.setNextUID(1);
		mailbox.setUidValidity(uidValidity);
		
		return mailbox;
	}

	@Override
	public List<Mailbox> getChildren(long userID, long ownerID,
			String mailboxName) {
		if (StringUtils.isEmpty(mailboxName)) {
			String sql = "SELECT * FROM hw_mailbox USE INDEX (ix_hw_mailbox_1) WHERE ownerid = ? ORDER BY name";
			return getJdbcTemplate().query(sql, mailboxRowMapper, ownerID);
		} else {
			String sql = "SELECT * FROM hw_mailbox USE INDEX (ix_hw_mailbox_1) WHERE ownerid = ? AND name LIKE ? ORDER BY name";
			return getJdbcTemplate().query(sql, mailboxRowMapper, ownerID,
					new StringBuilder(escape(mailboxName))
							.append(Mailbox.folderSeparator).append('%')
							.toString());
		}
	}
	
	public int getChildCount(long ownerID, String mailboxName) {
		if (StringUtils.isEmpty(mailboxName)) {
			String sql = "SELECT COUNT(1) FROM hw_mailbox USE INDEX (ix_hw_mailbox_1) WHERE ownerid = ?";
			Object[] params = { new Long(ownerID) };
			return queryForInt(sql, params);
		} else {
			String sql = "SELECT COUNT(1) FROM hw_mailbox USE INDEX (ix_hw_mailbox_1) WHERE ownerid = ? AND name LIKE ?";
			Object[] params = {
					new Long(ownerID),
					new StringBuilder(escape(mailboxName)).append(
							Mailbox.folderSeparator).append('%').toString() };
			return queryForInt(sql, params);
		}
	}

	public long getFirstUnseenMessageID(long mailboxID) {
		String sql = "SELECT messageid FROM hw_message WHERE mailboxid = ? AND seen_flag = 'N' ORDER BY messageid LIMIT 1";
		return queryForLong(sql, new Object[] { new Long(mailboxID) });
	}
	
	public List<Long> getGarbageMailboxList() {
		String sql = "SELECT m.mailboxid FROM hw_mailbox AS m LEFT JOIN hw_user AS u ON m.ownerid = u.userid WHERE u.userid IS NULL";
		return getJdbcTemplate().queryForList(sql, Long.class);
	}
	
}
