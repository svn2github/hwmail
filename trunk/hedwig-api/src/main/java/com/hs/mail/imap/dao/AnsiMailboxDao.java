package com.hs.mail.imap.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.mailbox.Mailbox;

/**
 * 
 * @author Won Chul Doh
 * @since July 26, 2015
 *
 */
abstract class AnsiMailboxDao extends AbstractDao implements MailboxDao {
	
	public Mailbox createMailbox(long ownerID, String mailboxName) {
		Mailbox result = doCreateMailbox(ownerID, mailboxName);
		forceCreate(ownerID, Mailbox.getParent(mailboxName));
		return result;
	}

	public void renameMailbox(Mailbox source, String dest) {
		long ownerid = source.getOwnerID();

		// If the server¡¯s hierarchy separator character appears in the
		// name, the server SHOULD create any superior hierarchical names
		// that are needed.
		forceCreate(ownerid, Mailbox.getParent(dest));

		// If INBOX is renamed, then all messages in INBOX MUST be moved to
		// a new mailbox with the given name, leaving INBOX empty.
		String base = source.getName();
		if (ImapConstants.INBOX_NAME.equals(base)) {
			doCreateMailbox(ownerid, ImapConstants.INBOX_NAME);
		}
		doRenameMailbox(source.rename(base, dest));

		// If source name has inferior hierarchical names, then the inferior
		// hierarchical names MUST also be renamed.
		List<Mailbox> children = getChildren(ownerid, ownerid, base);
		for (Mailbox child : children) {
			doRenameMailbox(child.rename(base, dest));
		}
	}

	private void forceCreate(long ownerID, String mailboxName) {
		while (!"".equals(mailboxName) && !mailboxExists(ownerID, mailboxName)) {
			doCreateMailbox(ownerID, mailboxName);
			mailboxName = Mailbox.getParent(mailboxName);
		}
	}
	
	abstract protected Mailbox doCreateMailbox(final long ownerID, final String mailboxName);

	abstract public List<Mailbox> getChildren(long userID, long ownerID, String mailboxName);

	public List<Long> getMailboxIDList(String mailboxName) {
		if (mailboxName.endsWith("*")) {
			String sql = "SELECT mailboxid FROM hw_mailbox WHERE name LIKE ?";
			return getJdbcTemplate().queryForList(
					sql,
					new Object[] { new StringBuilder(escape(mailboxName))
							.append('%').toString() }, Long.class);
		} else {
			String sql = "SELECT mailboxid FROM hw_mailbox WHERE name = ?";
			return getJdbcTemplate().queryForList(sql,
					new Object[] { mailboxName }, Long.class);
		}
	}
	
	public List<Mailbox> getSubscriptions(long userID, long ownerID,
			String mailboxName) {
		if (StringUtils.isEmpty(mailboxName)) {
			String sql = "SELECT b.* FROM hw_mailbox b, hw_subscription s WHERE s.userid = ? AND b.ownerid = ? AND b.name = s.name ORDER BY b.name";
			return getJdbcTemplate().query(sql,
					new Object[] { new Long(userID), new Long(ownerID) },
					mailboxRowMapper);
		} else {
			String sql = "SELECT b.* FROM hw_mailbox b, hw_subscription s WHERE s.userid = ? AND b.ownerid = ? AND b.name LIKE ? AND b.name = s.name ORDER BY b.name";
			return getJdbcTemplate().query(
					sql,
					new Object[] {
							new Long(userID),
							new Long(ownerID),
							new StringBuilder(escape(mailboxName))
									.append(Mailbox.folderSeparator).append('%')
									.toString() }, mailboxRowMapper);
		}
	}

	public boolean isSubscribed(long userID, String mailboxName) {
		String sql = "SELECT COUNT(1) FROM hw_subscription WHERE userid = ? AND name = ?";
		int count = queryForInt(sql, new Object[] { new Long(userID),
				mailboxName });
		return (count > 0);
	}

	public void addSubscription(long userID, long mailboxID, String mailboxName) {
		if (!isSubscribed(userID, mailboxName)) {
			String sql = "INSERT INTO hw_subscription (userid, mailboxid, name) VALUES(?, ?, ?)";
			getJdbcTemplate().update(sql, userID, mailboxID, mailboxName);
		} else {
			// already subscribed to the mailbox, verified after attempt to
			// subscribe
		}
	}

	public void deleteSubscription(long userID, String mailboxName) {
		String sql = "DELETE FROM hw_subscription WHERE userid = ? AND name = ?";
		getJdbcTemplate().update(sql, userID, mailboxName);
	}

	private int doRenameMailbox(Mailbox mailbox) {
		String sql = "UPDATE hw_mailbox SET name = ? WHERE mailboxid = ?";
		return getJdbcTemplate().update(sql, mailbox.getName(),
				mailbox.getMailboxID());
	}

	public List<Long> getDeletedMessageIDList(long mailboxID) {
		String sql = "SELECT messageid FROM hw_message WHERE mailboxid = ? AND deleted_flag = 'Y'";
		return getJdbcTemplate().queryForList(sql, Long.class, mailboxID);
	}

	public void deleteMailboxes(long ownerID) {
		String sql = "DELETE FROM hw_mailbox WHERE ownerid = ?";
		getJdbcTemplate().update(sql, ownerID);
	}
	
	public void deleteMailbox(long ownerID, long mailboxID) {
		String[] sqls = { "DELETE FROM hw_subscription WHERE mailboxid = ?",
				"DELETE FROM hw_acl WHERE mailboxid = ?",
				"DELETE FROM hw_mailbox WHERE mailboxid = ?" };
		update(sqls, mailboxID);
	}

	public void forbidSelectMailbox(long ownerID, long mailboxID) {
		String sql = "UPDATE hw_mailbox SET noselect_flag = 'Y' WHERE mailboxid = ?";
		getJdbcTemplate().update(sql, mailboxID);
	}

	public void deleteMessages(long ownerID) {
		String[] sql = {
			"DELETE FROM hw_keyword k "
			+     "WHERE k.messageid IN "
			+       "(SELECT m.messageid "
			+          "FROM hw_message m, hw_mailbox b "
			+         "WHERE b.ownerid = ? "
			+           "AND m.mailboxid = b.mailboxid)",
			"DELETE FROM hw_message m "
			+     "WHERE m.mailboxid IN "
			+       "(SELECT b.mailboxid "
			+          "FROM hw_mailbox b "
			+         "WHERE b.ownerid = ?)"
		};
		Object[] param = new Object[] { new Long(ownerID) };
		getJdbcTemplate().update(sql[0], param);
		getJdbcTemplate().update(sql[1], param);
	}

	public void deleteMessages(long ownerID, long mailboxID) {
		String[] sql = {
			"DELETE FROM hw_keyword k "
			+     "WHERE k.messageid IN "
			+       "(SELECT m.messageid FROM hw_message m WHERE m.mailboxid = ?)",
			"DELETE FROM hw_message m WHERE m.mailboxid = ?"	
		};
		Object[] param = new Object[] { new Long(mailboxID) };
		getJdbcTemplate().update(sql[0], param);
		getJdbcTemplate().update(sql[1], param);
	}

	public int getMessageCount(long mailboxID) {
		String sql = "SELECT COUNT(1) FROM hw_message WHERE mailboxid = ?";
		return queryForInt(sql, new Object[] { new Long(mailboxID) });
	}
	
	public int getRecentMessageCount(long mailboxID) {
		String sql = "SELECT COUNT(1) FROM hw_message WHERE mailboxid = ? AND recent_flag = 'Y'";
		return queryForInt(sql, new Object[] { new Long(mailboxID) });
	}
	
	public int getUnseenMessageCount(long mailboxID) {
		String sql = "SELECT COUNT(1) FROM hw_message WHERE mailboxid = ? AND seen_flag = 'N'";
		return queryForInt(sql, new Object[] { new Long(mailboxID) });
	}
	
	protected static RowMapper<Mailbox> mailboxRowMapper = new RowMapper<Mailbox>() {
		public Mailbox mapRow(ResultSet rs, int rowNum) throws SQLException {
			Mailbox mailbox = new Mailbox();
			mailbox.setMailboxID(rs.getLong("mailboxid"));
			mailbox.setOwnerID(rs.getLong("ownerid"));
			mailbox.setName(rs.getString("name"));
			mailbox.setNoInferiors("Y".equals(rs.getString("noinferiors_flag")));
			mailbox.setNoSelect("Y".equals(rs.getString("noselect_flag")));
			mailbox.setReadOnly("Y".equals(rs.getString("readonly_flag")));
			mailbox.setNextUID(rs.getLong("nextuid"));
			mailbox.setUidValidity(rs.getLong("uidvalidity"));
			return mailbox;
		}
	 };
 
}
