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
package com.hs.mail.smtp.processor.fastfail;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.hs.mail.imap.user.Alias;
import com.hs.mail.imap.user.User;
import com.hs.mail.smtp.SmtpException;
import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.message.Recipient;
import com.hs.mail.smtp.message.SmtpMessage;
import com.hs.mail.smtp.processor.RcptProcessor;

/**
 * 
 * @author Won Chul Doh
 * @since Jan 04, 2017
 *
 */
public class ValidRcptProcessor extends RcptProcessor {

	private boolean isValidRecipient(Recipient rcpt) {
		User user = getUserManager().getUserByAddress(rcpt.getMailbox());
		if (user != null) {
			if (StringUtils.isEmpty(user.getForwardTo())) {
				rcpt.setID(user.getID());
			}
		} else {
			List<Alias> expanded = getUserManager().expandAlias(rcpt.getMailbox());
			if (CollectionUtils.isEmpty(expanded)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	protected void doRcpt(SmtpSession session, SmtpMessage message, Recipient rcpt) {
		if (isValidRecipient(rcpt)) {
			message.addRecipient(rcpt);
		} else {
			throw new SmtpException(SmtpException.NO_SUCH_USER);
		}
	}

}
