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
import java.sql.Timestamp;
import java.util.Date;

import javax.mail.Flags;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.hs.mail.imap.message.MailMessage;
import com.hs.mail.imap.message.MessageHeader;

/**
 * 
 * @author Won Chul Doh
 * @since July 26, 2015
 *
 */
public class OracleMessageDao extends AnsiMessageDao {

	@Override
	protected long addPhysicalMessage(final MailMessage message) {
		final String sql = "INSERT INTO hw_physmessage (physmessageid, rfcsize, internaldate, subject, sentdate, fromaddr) VALUES(sq_hw_physmessage.NEXTVAL, ?, ?, ?, ?, ?)";
		final MessageHeader header = message.getHeader();
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement pstmt = con.prepareStatement(sql,
						new String[] { "physmessageid" });
				pstmt.setLong(1, message.getSize()); // size
				pstmt.setTimestamp(2, new Timestamp(message.getInternalDate()
						.getTime())); // internaldate
				pstmt.setString(3, header.getSubject()); // subject
				Date sent = header.getDate();
				pstmt.setTimestamp(4, (sent != null) ? new Timestamp(sent
						.getTime()) : null); // sentdate
				pstmt.setString(5, (header.getFrom() != null) ? header
						.getFrom().getDisplayString() : null); // fromaddr
				return pstmt;
			}
		}, keyHolder);
		long physmessageid = keyHolder.getKey().longValue();
		message.setPhysMessageID(physmessageid);
		addHeader(physmessageid, header);
		return physmessageid;
	}

	@Override
	protected void addMessage(long physMessageID, long mailboxID) {
		String sql = "INSERT INTO hw_message (messageid, physmessageid, mailboxid) VALUES(sq_hw_message.NEXTVAL, ?, ?)";
		getJdbcTemplate().update(sql,
				new Object[] { new Long(physMessageID), new Long(mailboxID) });
	}

	@Override
	protected void addMessage(long physMessageID, long mailboxID, Flags flags) {
		String sql = "INSERT INTO hw_message (messageid, physmessageid, mailboxid, seen_flag, answered_flag, deleted_flag, flagged_flag, draft_flag) VALUES(sq_hw_message.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
		getJdbcTemplate().update(
				sql,
				new Object[] { new Long(physMessageID), new Long(mailboxID),
						FlagUtils.getParam(flags, Flags.Flag.SEEN),
						FlagUtils.getParam(flags, Flags.Flag.ANSWERED),
						FlagUtils.getParam(flags, Flags.Flag.DELETED),
						FlagUtils.getParam(flags, Flags.Flag.FLAGGED),
						FlagUtils.getParam(flags, Flags.Flag.DRAFT) });
	}

	@Override
	public void copyMessage(long messageID, long mailboxID) {
		// The flags and internal date of the message SHOULD be preserved, and
		// the Recent flag SHOULD be set, in the copy.
		String sql = "INSERT INTO hw_message (messageid, mailboxid, physmessageid, seen_flag, answered_flag, deleted_flag, flagged_flag, draft_flag) SELECT sq_hw_message.NEXTVAL, ?, physmessageid, seen_flag, answered_flag, deleted_flag, flagged_flag, draft_flag FROM hw_message WHERE messageid = ?";
		getJdbcTemplate().update(sql,
				new Object[] { new Long(mailboxID), new Long(messageID) });
	}

	@Override
	protected long addHeaderName(final String headerName) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				String sql = "INSERT INTO hw_headername (headernameid, headername) VALUES(sq_hw_headername.NEXTVAL, ?)";
				PreparedStatement pstmt = con.prepareStatement(sql,
						new String[] { "headernameid" } );
				pstmt.setString(1, headerName);
				return pstmt;
			}
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

	@Override
	protected void addHeaderValue(long physMessageID, long headerNameID, String headerValue) {
		String sql = "INSERT INTO hw_headervalue (headervalueid, physmessageid, headernameid, headervalue) VALUES(sq_hw_headervalue.NEXTVAL, ?, ?, ?)";
		if (headerValue.length() > 4000) {
			// FIXME - Save to CLOB, and make header value column to point the CLOB 
			headerValue = headerValue.substring(0, 3990) + "...";
		}
		getJdbcTemplate().update(
				sql,
				new Object[] { new Long(physMessageID), new Long(headerNameID),
						headerValue });
	}

}
