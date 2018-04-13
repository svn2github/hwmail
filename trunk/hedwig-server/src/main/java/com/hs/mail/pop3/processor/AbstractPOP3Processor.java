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

import org.apache.commons.lang3.StringUtils;

import com.hs.mail.container.config.ComponentManager;
import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.user.UserManager;
import com.hs.mail.pop3.POP3Session;

/**
 * Abstract base class for {#link POP3Processor} implementations
 * 
 * @author Won Chul Doh
 * @since April 11, 2018
 * 
 */
public abstract class AbstractPOP3Processor implements POP3Processor {

	@Override
	public void process(POP3Session session, TcpTransport trans,
			StringTokenizer st) {
		try {
			doProcess(session, trans, st);
		} catch (Exception ex) {
			if (StringUtils.isNotEmpty(ex.getMessage()))
				session.writeResponse(ERR_RESPONSE + " " + ex.getMessage());
			else
				session.writeResponse(ERR_RESPONSE);
		}
	}

	abstract void doProcess(POP3Session session, TcpTransport trans, StringTokenizer st);

	protected MailboxManager getMailboxManager() {
		return ComponentManager.getBeanOfType(MailboxManager.class);
	}

	protected UserManager getUserManager() {
		return ComponentManager.getBeanOfType(UserManager.class);
	}
	
	protected String nextToken(StringTokenizer st) {
		return st.nextToken().trim();
	}
	
}
