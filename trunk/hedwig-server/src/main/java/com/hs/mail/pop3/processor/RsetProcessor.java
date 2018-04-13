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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.message.MessageMetaData;
import com.hs.mail.pop3.POP3Exception;
import com.hs.mail.pop3.POP3Session;

/**
 * Handler for RSET command.
 * 
 * @author Won Chul Doh
 * @since April 12, 2018
 * 
 */
public class RsetProcessor extends AbstractPOP3Processor {

	@Override
	protected void doProcess(POP3Session session, TcpTransport trans,
			StringTokenizer st) {
		if (session.getState() == POP3Session.State.TRANSACTION) {
			stat(session);
			session.writeResponse(OK_RESPONSE);
		} else {
			throw POP3Exception.INVALID_STATE;
		}
	}

	protected void stat(POP3Session session) {
		if (session.getMailboxID() >= 0) {
			MailboxManager manager = getMailboxManager();
			List<MessageMetaData> uidList = manager.getMessageMetaData(session.getMailboxID());
			session.setUidList(uidList);
			session.setDeletedUidList(new ArrayList<Long>());
		}
	}

}
