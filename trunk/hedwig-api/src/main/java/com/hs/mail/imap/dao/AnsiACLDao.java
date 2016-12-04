package com.hs.mail.imap.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.hs.mail.imap.UnsupportedRightException;
import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.mailbox.MailboxACL.MailboxACLEntry;

/**
 * 
 * @author Wonchul Doh
 * @since December 2, 2016
 *
 */
public class AnsiACLDao extends AbstractDao implements ACLDao {
	
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
		MailboxACLEntry entry = queryForObject(sql, new Object[] { mailboxID, userID }, aclMapper);
		return (entry != null) ? entry.getRights() : null;
	}

	public void setRights(long userID, long mailboxID, String rights) throws UnsupportedRightException {
		if (StringUtils.isEmpty(rights)) {
			final String sqlDelete = "DELETE FROM hw_acl WHERE userid = ? AND mailboxid = ?";
			getJdbcTemplate().update(sqlDelete, userID, mailboxID);
		} else {
			final String sqlUpdate = "UPDATE hw_acl SET "
					+ StringUtils.join(flagArray, "=?,")
					+ "=? WHERE userid = ? AND mailboxid = ?";
			Object[] args = buildParams(rights);
			if (getJdbcTemplate().update(sqlUpdate,
					ArrayUtils.addAll(args, userID, mailboxID)) == 0) {
				final String sqlInsert = "INSERT INTO hw_acl (userid,mailboxid,"
						+ StringUtils.join(flagArray, ",")
						+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
				getJdbcTemplate().update(
						sqlInsert,
						ArrayUtils.addAll(new Object[] { userID, mailboxID },
								args));
			}
		}
	}
	
	public void setRights(long userID, long mailboxID, String rights,
			boolean set) throws UnsupportedRightException {
		final String sql = "UPDATE hw_acl SET "
				+ StringUtils.join(buildFlags(rights), "=?,")
				+ "=? WHERE WHERE userid = ? AND mailboxid = ?";
		Object[] args = new Object[rights.length()];
		Arrays.fill(args, set ? 'Y' : 'N');
		getJdbcTemplate().update(sql,
				ArrayUtils.addAll(args, userID, mailboxID));
	}
	
	public MailboxACL getACL(long mailboxID) {
		final String sql = "SELECT u.loginid, a.* FROM hw_acl a, hw_user u WHERE a.mailboxid = ? AND a.userid = u.userid";
		List<MailboxACLEntry> entries = getJdbcTemplate().query(sql, new Object[] { mailboxID }, aclMapper);
		String rights = getRights(MailboxACL.ANYONE_ID, mailboxID);
		if (StringUtils.isNotEmpty(rights)) {
			MailboxACLEntry entry = new MailboxACLEntry();
			entry.setIdentifier(MailboxACL.ANYONE);
			entry.setRights(rights);
			entries.add(entry);
		}
		MailboxACL acl = new MailboxACL();
		acl.setEntries(entries);
		return acl;
	}
	
	private static Object[] buildParams(String rights) throws UnsupportedRightException {
		Object[] params = new Object[flagArray.length];
		Arrays.fill(params, 'N');
		for (int i = 0; i < rights.length(); i++) {
			int j = indexOfRight(rights.charAt(i));
			params[j] = 'Y';
		}
		return params;
	}
	
	private static String[] buildFlags(String rights) throws UnsupportedRightException {
		String[] array = new String[rights.length()];
		for (int i = 0; i < rights.length(); i++) {
			int j = indexOfRight(rights.charAt(i));
			array[i] = flagArray[j];
		}
		return array;
	}
	
	private static int indexOfRight(char flag) throws UnsupportedRightException {
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
