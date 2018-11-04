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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.UnsupportedRightException;
import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.mailbox.MailboxACL.MailboxACLEntry;

/**
 * 
 * @author Wonchul Doh
 * @since December 2, 2016
 *
 */
abstract class AnsiACLDao extends AbstractDao implements ACLDao {
	
	private static final String[] flagArray = { 
		"lookup_flag", 
		"read_flag",
		"seen_flag", 
		"write_flag", 
		"insert_flag", 
		"post_flag",
		"create_flag", 
		"delete_flag", 
		"deletemsg_flag", 
		"expunge_flag",
		"admin_flag" 
	};
	
	public String getRights(long userID, long mailboxID) {
		final String sql = "SELECT * FROM hw_acl WHERE mailboxid = ? AND userid = ?";
		MailboxACLEntry entry = queryForObject(sql, new Object[] { mailboxID,
				userID }, aclMapper);
		return (entry != null) ? entry.getRights() : "";
	}

	public void setRights(long userID, long mailboxID, String rights) {
		if (StringUtils.isEmpty(rights)) {
			final String sql = "DELETE FROM hw_acl WHERE userid = ? AND mailboxid = ?";
			getJdbcTemplate().update(sql, userID, mailboxID);
		} else {
			String sql = "UPDATE hw_acl SET "
					+ StringUtils.join(flagArray, "=?,")
					+ "=? WHERE userid = ? AND mailboxid = ?";
			Object[] args = buildParams(rights);
			if (getJdbcTemplate().update(sql,
					ArrayUtils.addAll(args, userID, mailboxID)) == 0) {
				sql = "INSERT INTO hw_acl (userid,mailboxid,"
						+ StringUtils.join(flagArray, ",")
						+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
				getJdbcTemplate().update(sql, ArrayUtils
						.addAll(new Object[]{userID, mailboxID}, args));
			}
		}
	}
	
	public void setRights(long userID, long mailboxID, String rights,
			boolean set) {
		final String sql = "UPDATE hw_acl SET " + joinFlags(rights, "=?,", "=?")
				+ " WHERE WHERE userid = ? AND mailboxid = ?";
		Object[] args = new Object[rights.length()];
		Arrays.fill(args, set ? 'Y' : 'N');
		getJdbcTemplate().update(sql,
				ArrayUtils.addAll(args, userID, mailboxID));
	}
	
	public MailboxACL getACL(long mailboxID) {
		final String sql = "SELECT 'anyone' as loginid, a.* FROM hw_acl a WHERE a.mailboxid = ? AND a.userid = ? "
				+ "UNION SELECT u.loginid, a.* FROM hw_acl a, hw_user u WHERE a.mailboxid = ? AND a.userid = u.userid";
		List<MailboxACLEntry> entries = getJdbcTemplate().query(sql,
				new Object[]{mailboxID, ImapConstants.ANYONE_ID, mailboxID},
				aclMapper);
		MailboxACL acl = new MailboxACL();
		acl.setEntries(entries);
		return acl;
	}
	
	public boolean hasRights(long userID, long mailboxID, String rights) {
		final String sql = 
				"SELECT userid "
				+ "FROM hw_acl "
				+ "WHERE mailboxid = ? "
				+   "AND " + joinFlags(rights, " = 'Y' AND ", " = 'Y'");
		List<Long> list = getJdbcTemplate().queryForList(sql, Long.class,
				mailboxID);
		if (CollectionUtils.isNotEmpty(list)) {
			// TODO - Resolve group membership
			if (list.contains(userID)
					|| list.contains(ImapConstants.ANYONE_ID)) {
				return true;
			}
		}
		return false;
	}
	
	public List<Long> getAuthorizedMailboxes(long userID, String rights) {
		final String sql = 
				"SELECT mailboxid "
				+ "FROM hw_acl "
				+ "WHERE (userid = ? OR userid = 0) "
				+   "AND " + joinFlags(rights, " = 'Y' AND ", " = 'Y'");
		return getJdbcTemplate().queryForList(sql, Long.class, userID);
	}

	private static Object[] buildParams(String rights) {
		Object[] params = new Object[flagArray.length];
		Arrays.fill(params, "N");
		for (int i = 0; i < rights.length(); i++) {
			int j = indexOfRight(rights.charAt(i));
			params[j] = "Y";
		}
		return params;
	}
	
	private static String joinFlags(String rights, String mid, String tail) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < rights.length(); i++) {
			int j = indexOfRight(rights.charAt(i));
			if (i > 0)
				buffer.append(mid);
			buffer.append(flagArray[j]);
		}
		return buffer.append(tail).toString();
	}

	private static int indexOfRight(char flag) {
		int index = MailboxACL.STD_RIGHTS.indexOf(flag);
		if (index < 0) {
			throw new UnsupportedRightException(flag);
		}
		return index;
	}

	private static RowMapper<MailboxACLEntry> aclMapper = new RowMapper<MailboxACLEntry>() {
		public MailboxACLEntry mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			MailboxACLEntry entry = new MailboxACLEntry();
			entry.setIdentifier(rs.getString("loginid"));
			String rights = "";
			for (int i = 0; i < flagArray.length; i++) {
				if ("Y".equals(rs.getString(flagArray[i]))) {
					rights += MailboxACL.STD_RIGHTS.charAt(i);
				}
			}
			entry.setRights(rights);
			return entry;
		}
	};

}
