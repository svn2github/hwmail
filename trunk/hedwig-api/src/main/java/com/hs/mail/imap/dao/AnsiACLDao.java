package com.hs.mail.imap.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.mailbox.MailboxACL.MailboxACLEntry;

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

	public void setRights(long userID, long mailboxID, String rights) {
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
				String sqlInsert = "INSERT INTO hw_acl (userid,mailboxid,"
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
			boolean set) {
		String sql = "UPDATE hw_acl SET "
				+ StringUtils.join(buildFlags(rights), "=?,")
				+ "=? WHERE WHERE userid = ? AND mailboxid = ?";
		Object[] args = new Object[rights.length()];
		Arrays.fill(args, set ? 'Y' : 'N');
		getJdbcTemplate().update(sql,
				ArrayUtils.addAll(args, userID, mailboxID));
	}
	
	public MailboxACL getACL(long mailboxID) {
		String sql = "SELECT u.loginid, a.* FROM hw_acl a, hw_user u WHERE a.mailboxid = ? AND a.userid = u.userid";
		List<MailboxACLEntry> entries = getJdbcTemplate().query(sql,
				new Object[] { mailboxID }, aclMapper);
		MailboxACL acl = new MailboxACL();
		acl.setEntries(entries);
		return acl;
	}

	private static Object[] buildParams(String rights) {
		Object[] params = new Object[flagArray.length];
		Arrays.fill(params, 'N');
		for (int i = 0; i < rights.length(); i++) {
			int j = indexOfRight(rights.charAt(i));
			if (j >= 0) {
				params[j] = 'Y';
			} else {
				// TODO: throw exception
			}
		}
		return params;
	}
	
	private static String[] buildFlags(String rights) {
		String[] array = new String[rights.length()];
		for (int i = 0; i < rights.length(); i++) {
			int j = indexOfRight(rights.charAt(i));
			if (j >= 0) {
				array[i] = flagArray[j];
			} else {
				// TODO: throw exception
			}
		}
		return array;
	}
	
	private static int indexOfRight(char flag) {
		return MailboxACL.STD_RIGHTS.indexOf(flag);
	}
	
	private static RowMapper<MailboxACLEntry> aclMapper = new RowMapper<MailboxACLEntry>() {
		public MailboxACLEntry mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			MailboxACLEntry acl = new MailboxACLEntry();
			acl.setIdentifier(rs.getString("loginid"));
			String rights = "";
			for (int i = 0; i < flagArray.length; i++) {
				if ("Y".equals(rs.getString(flagArray[i]))) {
					rights += MailboxACL.STD_RIGHTS.charAt(i);
				}
			}
			acl.setRights(rights);
			return acl;
		}
	};

}
