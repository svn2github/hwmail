/*
 * Copyright 2018 the original author or authors.
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
package com.hs.mail.pop3.processor;

import java.util.StringTokenizer;

import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;

import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.user.UserManager;
import com.hs.mail.pop3.POP3Exception;
import com.hs.mail.pop3.POP3Session;

/**
 * Handler for PASS command.
 * 
 * Reads in and validates the password.
 * 
 * @author Won Chul Doh
 * @since April 12, 2018
 * 
 */
public class PassProcessor extends RsetProcessor {

	@Override
	protected void doProcess(POP3Session session, TcpTransport trans,
			StringTokenizer st) {
		if (session.getState() == POP3Session.State.AUTHORIZATION
				&& session.getUser() != null) {
			if (st.countTokens() == 1) {
				doPASS(session, trans, nextToken(st));
			} else {
				throw POP3Exception.INVALID_ARGS;
			}
		} else {
			throw POP3Exception.INVALID_STATE;
		}
	}

	private void doPASS(POP3Session session, TcpTransport trans, String pass) {
		try {
			UserManager manager = getUserManager();
			long userID = manager.login(session.getUser(), pass);
			session.setState(POP3Session.State.TRANSACTION);

			MailboxManager mailboxManager = getMailboxManager();
			final String inboxName = ImapConstants.INBOX_NAME;
			Mailbox inbox = mailboxManager.getMailbox(userID, inboxName);
			if (inbox != null) {
				session.setMailboxID(inbox.getMailboxID());
			}
			stat(session);
			StringBuilder responseBuffer = new StringBuilder(64)
					.append(OK_RESPONSE)
					.append(" ")
					.append(StringUtils.substringBefore(session.getUser(), "@"))
					.append("'s maildrop ready");
			session.writeResponse(responseBuffer.toString());
		} catch (LoginException e) {
			throw new POP3Exception("Authentication failed");
		}
	}

}
