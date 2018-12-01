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

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.hs.mail.container.config.ComponentManager;
import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.user.Alias;
import com.hs.mail.imap.user.User;
import com.hs.mail.imap.user.UserManager;
import com.hs.mail.smtp.SmtpException;
import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.message.Recipient;
import com.hs.mail.smtp.message.SmtpMessage;

public class ValidRcptHook implements RcptHook {

	private HookResult isValidRecipient(SmtpSession session, Recipient rcpt) {
		User user = getUserManager().getUserByAddress(rcpt.getMailbox());
		if (user != null) {
			if (StringUtils.isEmpty(user.getForwardTo())) {
				rcpt.setID(user.getID());
			}
		} else {
			List<Alias> expanded = getUserManager().expandAlias(rcpt.getMailbox());
			if (CollectionUtils.isNotEmpty(expanded)) {
				for (Alias alias : expanded) {
					if (alias.getDeliverTo().startsWith(ImapConstants.SHARED_PREFIX)) {
						// Aliased mailbox is a public folder.
						Mailbox mailbox = getMailboxManager().getMailbox(
								ImapConstants.ANYONE_ID, alias.getDeliverTo());
						if (mailbox == null) {
							return HookResult.reject(SmtpException.NO_SUCH_USER);
						} else if (session.getAuthID() < 0	// anonymous
								|| !getMailboxManager().hasRights(
										session.getAuthID(), mailbox, "p")) {	// p_Post_RIGHT
							// User does not have right to post to the folder.
							return HookResult.reject(SmtpException.RECIPIENT_REJECTED);
						}
					}
				}
			} else {
				// User and alias does not exist
				return HookResult.reject(SmtpException.NO_SUCH_USER);
			}
		}
		return HookResult.DUNNO;
	}
	
	@Override
	public HookResult doRcpt(SmtpSession session, SmtpMessage message, Recipient rcpt) {
		try {
			return isValidRecipient(session, rcpt);
		} catch (Exception _) {
			// Maybe JDBC connection exception.
			// We have a second chance at spool.
		}
		return HookResult.DUNNO;
	}

	private UserManager getUserManager() {
		return (UserManager) ComponentManager.getBean("userManager");
	}

	private MailboxManager getMailboxManager() {
		return (MailboxManager) ComponentManager.getBean("mailboxManager");
	}
	
}
