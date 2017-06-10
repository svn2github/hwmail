/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.hs.mail.smtp.processor.hook;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.message.MailAddress;

public class MailLog {

	private static Logger logger = LoggerFactory.getLogger(MailLog.class);
	
	public static void connect(SmtpSession session) {
		logger.debug("connect from {} [{}]",
				session.getRemoteHost(), 
				session.getRemoteIP());
	}
	
	public static void reject(SmtpSession session, String message) {
		logger.error("NOQUEUE: reject: CONNECT from {} [{}]; {};", 
				session.getRemoteHost(), 
				session.getRemoteIP(),
				message);
	}
	
	public static void reject(SmtpSession session, MailAddress sender,
			String message) {
		logger.error("NOQUEUE: reject: MAIL from {} [{}]; {}; from={}, proto={}, helo={}",
				session.getRemoteHost(), 
				session.getRemoteIP(),
				message, 
				defaultString(sender),
				session.getProtocol(),
				session.getClientDomain());
	}
	
	public static void reject(SmtpSession session, MailAddress from,
			MailAddress to, String message) {
		logger.error("NOQUEUE: reject: RCPT from {} [{}]; {}; from={}, to={}, proto={}, helo={}",
				session.getRemoteHost(), 
				session.getRemoteIP(),
				message, 
				defaultString(from),
				defaultString(to), 
				session.getProtocol(),
				session.getClientDomain());
	}

	private static String defaultString(MailAddress address) {
		return (address != null)
				? new StringBuilder("<").append(address.getMailbox())
						.append(">").toString()
				: StringUtils.EMPTY;
	}
	
}
