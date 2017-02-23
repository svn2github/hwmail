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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Flags;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.james.mime4j.parser.Field;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.message.FetchData;
import com.hs.mail.imap.message.MailMessage;
import com.hs.mail.imap.message.MessageHeader;
import com.hs.mail.imap.message.PhysMessage;

/**
 * 
 * @author Won Chul Doh
 * @since July 26, 2015
 *
 */
@SuppressWarnings("unchecked")
abstract class AnsiMessageDao extends AbstractDao implements MessageDao {

	public List<Long> getMessageIDList(long mailboxID) {
		String sql = "SELECT messageid FROM hw_message WHERE mailboxid = ? ORDER BY messageid";
		return (List<Long>) getJdbcTemplate().queryForList(sql,
				new Object[] { new Long(mailboxID) }, Long.class);
	}

	public void addMessage(long mailboxID, MailMessage message) {
		if (message.getPhysMessageID() == 0) {
			addPhysicalMessage(message);
		}
		addMessage(message.getPhysMessageID(), mailboxID);
	}

	abstract protected long addPhysicalMessage(final MailMessage message);

	abstract protected void addMessage(long physMessageID, long mailboxID);

	public void addMessage(long mailboxID, MailMessage message, Flags flags) {
		if (message.getPhysMessageID() == 0) {
			addPhysicalMessage(message);
		}
		addMessage(message.getPhysMessageID(), mailboxID, flags);
	}

	abstract protected void addMessage(long physMessageID, long mailboxID, Flags flags);
	
	abstract public void copyMessage(long messageID, long mailboxID);
	
	public FetchData getMessageFetchData(long messageID) {
		String sql = "SELECT m.*, p.rfcsize, p.internaldate "
				+      "FROM hw_message m, hw_physmessage p "
				+     "WHERE m.messageid = ? AND m.physmessageid = p.physmessageid";
		FetchData fd = (FetchData) queryForObject(sql, new Object[] { new Long(
				messageID) }, fetchDataRowMapper);
		if (fd != null) {
			List<String> ufs = getUserFlags(messageID);
			if (CollectionUtils.isNotEmpty(ufs)) {
				Flags flags = fd.getFlags();
				for (String uf : ufs) {
					flags.add(uf);
				}
			}
		}
		return fd;
	}
	
	public void deleteMessage(long messageID) {
		String[] sqls = { "DELETE FROM hw_keyword WHERE messageid = ?",
				"DELETE FROM hw_message WHERE messageid = ?" };
		update(sqls, messageID);
	}

	public PhysMessage getDanglingMessageID(long messageID) {
		String sql = "SELECT m.physmessageid, p.internaldate "
				+      "FROM hw_message m, hw_physmessage p "
				+     "WHERE m.physmessageid = (SELECT physmessageid FROM hw_message WHERE messageid = ?) "
				+       "AND p.physmessageid = m.physmessageid "
				+     "GROUP BY m.physmessageid, p.internaldate "
				+    "HAVING COUNT(m.physmessageid) = 1";
		return queryForObject(sql,
				new Object[] { new Long(messageID) }, new RowMapper<PhysMessage>() {
					public PhysMessage mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						PhysMessage pm = new PhysMessage();
						pm.setPhysMessageID(rs.getLong("physmessageid"));
						pm.setInternalDate(new Date(rs.getTimestamp("internaldate").getTime()));
						return pm;
					}
			}
		);
	}
	
	public void deletePhysicalMessage(long physMessageID) {
		String[] sql = { "DELETE FROM hw_physmessage WHERE physmessageid = ?",
				"DELETE FROM hw_headervalue WHERE physmessageid = ?" };
		Object[] param = { new Long(physMessageID) };
		getJdbcTemplate().update(sql[0], param);
		getJdbcTemplate().update(sql[1], param);
	}
	
	public List<Long> resetRecent(long mailboxID) {
		String sql = "SELECT messageid FROM hw_message WHERE mailboxid = ? AND recent_flag = 'Y'";
		Object[] param = new Object[] { new Long(mailboxID) };
		List<Long> result = getJdbcTemplate().queryForList(sql, param,
				Long.class);
		if (CollectionUtils.isNotEmpty(result)) {
			sql = "UPDATE hw_message SET recent_flag = 'N' WHERE mailboxid = ? AND recent_flag = 'Y'";
			getJdbcTemplate().update(sql, param);
		}
		return result;
	}
	
	public void setFlags(long messageID, Flags flags, boolean replace,
			boolean set) {
		setSystemFlags(messageID, flags.getSystemFlags(), replace, set);
		setUserFlags(messageID, flags.getUserFlags(), replace, set);
	}
	
	@SuppressWarnings("rawtypes")
	private int setSystemFlags(long messageID, Flags.Flag[] flags,
			boolean replace, boolean set) {
		if (ArrayUtils.isEmpty(flags)) {
			return 0;
		}
		StringBuilder sql = new StringBuilder("UPDATE hw_message SET ");
		List params = new ArrayList();
		sql.append(FlagUtils.buildParams(flags, replace, set, params));
		if (params.isEmpty()) {
			return 0;
		}
		sql.append(" WHERE messageid = ?");
		params.add(new Long(messageID));
		return getJdbcTemplate().update(sql.toString(), params.toArray());
	}
	
	private void setUserFlags(long messageID, String[] flags, boolean replace,
			boolean set) {
		if (replace) {
			String sql = "DELETE FROM hw_keyword WHERE messageid = ?";
			getJdbcTemplate().update(sql, new Object[] { new Long(messageID) });
		}
		if (!ArrayUtils.isEmpty(flags)) {
			String sql = (replace || set) ? "INSERT INTO hw_keyword (messageid, keyword) VALUES(?, ?)"
					: "DELETE FROM hw_keyword WHERE messageid = ? AND keyword = ?";
			for (int i = 0; i < flags.length; i++) {
				if (!(set && hasUserFlag(messageID, flags[i]))) {
					getJdbcTemplate().update(sql,
							new Object[] { new Long(messageID), flags[i] });
				}
			}
		}
	}
	
	public Flags getFlags(long messageID) {
		Flags flags = getSystemFlags(messageID);
		if (flags == null) {
			flags = new Flags();
		}
		List<String> ufs = getUserFlags(messageID);
		if (CollectionUtils.isNotEmpty(ufs)) {
			for (String uf : ufs) {
				flags.add(uf);
			}
		}
		return flags;
	}

	private Flags getSystemFlags(long messageID) {
		String sql = "SELECT * FROM hw_message WHERE messageid = ?";
		return queryForObject(sql,
				new Object[] { new Long(messageID) }, new RowMapper<Flags>() {
					public Flags mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return FlagUtils.getFlags(rs);
					}
				});
	}
	
	private List<String> getUserFlags(long messageID) {
		String sql = "SELECT keyword FROM hw_keyword WHERE messageid = ?";
		return getJdbcTemplate().queryForList(sql,
				new Object[] { new Long(messageID) }, String.class);
	}
	
	private boolean hasUserFlag(long messageID, String flag) {
		String sql = "SELECT COUNT(1) FROM hw_keyword WHERE messageid = ? AND keyword = ?";
		return queryForInt(sql, new Object[] { new Long(messageID), flag }) > 0;
	}
	
//-------------------------------------------------------------------------
// Methods dealing with message header
//-------------------------------------------------------------------------

	public Map<String, String> getHeader(long physMessageID) {
		String sql = "SELECT headername, headervalue FROM hw_headername n, hw_headervalue v WHERE v.physmessageid = ? AND v.headernameid = n.headernameid";
		SqlRowSet rs = getJdbcTemplate().queryForRowSet(sql,
				new Object[] { new Long(physMessageID) });
		Map<String, String> results = new HashMap<String, String>();
		while (rs.next()) {
			results.put(rs.getString(1), rs.getString(2));
		}
		return results;
	}
	
	public Map<String, String> getHeader(long physMessageID, String[] fields) {
		StringBuilder sql = new StringBuilder(256)
				.append("SELECT headername, headervalue FROM hw_headername n, hw_headervalue v WHERE v.physmessageid = ? AND v.headernameid = n.headernameid AND UPPER(n.headername) IN ");
		Object[] param = new Object[fields.length + 1];
		param[0] = new Long(physMessageID);
		
		System.arraycopy(fields, 0, param, 1, fields.length);
		sql.append("(")
				.append(StringUtils.repeat("UPPER(?)", ",", fields.length))
				.append(")");

		Map<String, String> results = new HashMap<String, String>();
		SqlRowSet rs = getJdbcTemplate().queryForRowSet(sql.toString(), param);
		while (rs.next()) {
			results.put(rs.getString(1), rs.getString(2));
		}
		return results;
	}
	
	public void addHeader(long physMessageID, MessageHeader header) {
		List<Field> fields = header.getHeader().getFields();
		if (!CollectionUtils.isEmpty(fields)) {
			for (Field field : fields) {
				addField(physMessageID, field);
			}
		}
	}
	
	private void addField(long physMessageID, Field field) {
		long id = getHeaderNameID(field.getName());
		addHeaderValue(physMessageID, id, field.getBody());
	}

	public long getHeaderNameID(String headerName) {
		String sql = "SELECT headernameid FROM hw_headername WHERE headername = ?";
		long result = queryForLong(sql, new Object[] { headerName });
		if (result == 0) {
			result = addHeaderName(headerName);
		}
		return result;
	}

	public List<Map<String, Object>> getMessageByMessageID(long userId,
			String messageID) {
		String sql = "SELECT m.messageid, m.seen_flag, m.deleted_flag, m.recent_flag "
				+      "FROM hw_message m, hw_mailbox b "
				+     "WHERE m.mailboxid = b.mailboxid "
				+       "AND b.ownerid = ? "
				+       "AND physmessageid IN (" 
				+           "SELECT physmessageid FROM hw_headervalue " 
				+            "WHERE headernameid IN ("
				+                  "SELECT headernameid FROM hw_headername WHERE UPPER(headername) = UPPER(?))"
				+              "AND headervalue = ?)";
		return getJdbcTemplate().queryForList(
				sql,
				new Object[] { new Long(userId), ImapConstants.RFC822_MESSAGE_ID,
						messageID });
	}
	
	abstract protected long addHeaderName(final String headerName);

	abstract protected void addHeaderValue(long physMessageID, long headerNameID, String headerValue);

	protected static RowMapper<FetchData> fetchDataRowMapper = new RowMapper<FetchData>() {
		public FetchData mapRow(ResultSet rs, int rowNum) throws SQLException {
			FetchData fd = new FetchData();
			fd.setMessageID(rs.getLong("messageid"));
			fd.setPhysMessageID(rs.getLong("physmessageid"));
			fd.setSize(rs.getLong("rfcsize"));
			fd.setFlags(FlagUtils.getFlags(rs));
			fd.setInternalDate(new Date(rs.getTimestamp("internaldate").getTime()));
			return fd;
		}
	 };

}
