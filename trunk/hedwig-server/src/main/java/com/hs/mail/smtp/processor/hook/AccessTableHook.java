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

import java.io.File;
import java.io.IOException;

import com.hs.mail.exception.ConfigException;
import com.hs.mail.smtp.SmtpException;
import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.message.MailAddress;
import com.hs.mail.smtp.message.Recipient;
import com.hs.mail.smtp.message.SmtpMessage;

public class AccessTableHook implements MailHook, RcptHook {

	private AccessTable access;

	public AccessTableHook(String path) throws ConfigException {
		File config = new File(path);
		if (!config.exists()) {
			throw new ConfigException(
					"Access file '" + path + "' does not exist");
		}
		try {
			this.access = new AccessTable(config);
		} catch (IOException e) {
			throw new ConfigException(e);
		}
	}

	@Override
	public HookResult doMail(SmtpSession session, MailAddress sender) {
		if (access.findAction(sender) == AccessTable.Action.REJECT) {
			return HookResult.reject(SmtpException.SENDER_REJECTED);
		}
		return HookResult.DUNNO;
	}

	@Override
	public HookResult doRcpt(SmtpSession session, SmtpMessage message, Recipient rcpt) {
		if (access.findAction(rcpt) == AccessTable.Action.REJECT) {
			return HookResult.reject(SmtpException.RECIPIENT_REJECTED);
		}
		return HookResult.DUNNO;
	}

}
