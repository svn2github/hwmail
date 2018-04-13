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

import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.collections.CollectionUtils;

import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.pop3.POP3Exception;
import com.hs.mail.pop3.POP3Session;

/**
 * Handler for QUIT command.
 * 
 * @author Won Chul Doh
 * @since April 12, 2018
 * 
 */
public class QuitProcessor extends AbstractPOP3Processor {

	private static final String SIGN_OFF = "Hedwig POP3 server signing off.";
	
	@Override
	protected void doProcess(POP3Session session, TcpTransport trans,
			StringTokenizer st) {
		if (session.getState() == POP3Session.State.AUTHORIZATION
				|| session.getState() == POP3Session.State.TRANSACTION) {
			if (session.getState() == POP3Session.State.TRANSACTION) {
				session.setState(POP3Session.State.UPDATE);
				List<Long> deletedUidList = session.getDeletedUidList();
				if (CollectionUtils.isNotEmpty(deletedUidList)) {
					MailboxManager manager = getMailboxManager();
					try {
						manager.deleteMessages(deletedUidList);
					} catch (Exception ex) {
						trans.endSession();
						throw new POP3Exception(
								"Some deleted messages were not removed", ex);
					}
				}
			}
			session.writeResponse(OK_RESPONSE + " " + SIGN_OFF);
			trans.endSession();
		} else {
			throw POP3Exception.INVALID_STATE;
		}
	}

}
