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
package com.hs.mail.smtp.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.hs.mail.container.config.Config;
import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.exception.ConfigException;
import com.hs.mail.smtp.SmtpException;
import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.message.Recipient;
import com.hs.mail.smtp.message.SmtpMessage;
import com.hs.mail.smtp.processor.hook.MaxRcptHook;
import com.hs.mail.smtp.processor.hook.RcptAccessHook;
import com.hs.mail.smtp.processor.hook.RcptHook;
import com.hs.mail.smtp.processor.hook.RelayRcptHook;
import com.hs.mail.smtp.processor.hook.ValidRcptHook;

/**
 * Handler for RCPT command. Read recipient. Does some recipient verification.
 * 
 * @author Won Chul Doh
 * @since May 29, 2010
 * 
 */
public class RcptProcessor extends AbstractSmtpProcessor {

	private List<RcptHook> hooks = null;

	@Override
	public void configure() throws ConfigException {
		hooks = new ArrayList<RcptHook>();

		int maxRcpt = (int) Config.getNumberProperty("smtp_recipient_limit", 0);
		if (maxRcpt > 0) {
			hooks.add(new MaxRcptHook(maxRcpt));
		}

		String restrictions = Config.getProperty("smtpd_relay_restrictions", "permit_mynetworks, permit_sasl_authenticated");
		if (StringUtils.isNotBlank(restrictions)) {
			String[] array = StringUtils.stripAll(StringUtils.split(restrictions, ","));
			hooks.add(new RelayRcptHook(array));
		}
		
		restrictions = Config.getProperty("smtpd_recipient_restrictions", null);
		if (StringUtils.isNotBlank(restrictions)) {
			String[] array = StringUtils.stripAll(StringUtils.split(restrictions, ","));
			for (String restriction : array) {
				String[] tokens = StringUtils.split(restriction);
				if (ArrayUtils.isNotEmpty(tokens)) {
					if ("check_recipient_access".equals(tokens[0])) {
						hooks.add(new RcptAccessHook(tokens.length > 1
								? tokens[1]
								: Config.replaceByProperties("${app.home}/conf/recipient_access")));
					}
					if ("reject_unlisted_recipient".equals(tokens[0])) {
						hooks.add(new ValidRcptHook());
					}
				}
			}
		}
	}

	@Override
	protected void doProcess(SmtpSession session, TcpTransport trans,
			StringTokenizer st) throws SmtpException {
		SmtpMessage message = session.getMessage();
		if (message == null || message.getFrom() == null) {
			throw new SmtpException(SmtpException.COMMAND_OUT_OF_SEQUENCE);
		}
		if (st.countTokens() < 1) {
			throw new SmtpException(SmtpException.INVALID_COMMAND_PARAM);
		}
		String to = nextToken(st);
		if (!startsWith(to, "TO:")) {
			throw new SmtpException(SmtpException.INVALID_COMMAND_PARAM);
		}
		if (to.length() == 3) {
			if (!st.hasMoreTokens()) {
				throw new SmtpException(SmtpException.MISSING_RECIPIENT_ADDRESS);
			}
			to = nextToken(st);
		} else {
			to = to.substring(3);
		}
		// FIXME: RFC5321 - Page 36
		if ("postmaster".equalsIgnoreCase(to)) {
			to = Config.getPostmaster();
		}

		Recipient recipient = new Recipient(to);
		// Reject if invalid recipient
		doRcpt(session, message, recipient);

		message.addRecipient(recipient);
		session.writeResponse("250 2.1.5 Recipient " + to + " OK");
	}

	void doRcpt(SmtpSession session, SmtpMessage message, Recipient rcpt) {
		if (hooks != null) {
			for (RcptHook hook : hooks) {
				hook.doRcpt(session, message, rcpt);
			}
		}
	}
	
}
