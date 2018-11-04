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
package com.hs.mail.imap.processor;

import org.apache.commons.lang3.StringUtils;

import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.mailbox.MailboxPath;
import com.hs.mail.imap.message.request.CreateRequest;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.response.HumanReadableText;

/**
 * 
 * @author Won Chul Doh
 * @since Feb 1, 2010
 *
 */
public class CreateProcessor extends AbstractImapProcessor {
	
	@Override
	protected void doProcess(ImapSession session, ImapRequest message,
			Responder responder) {
		CreateRequest request = (CreateRequest) message;
		String mailboxName = StringUtils.removeEnd(request.getMailbox(),
				Mailbox.folderSeparator);

		if (ImapConstants.INBOX_NAME.equalsIgnoreCase(mailboxName)) {
			responder.taggedNo(request,
					HumanReadableText.FAILED_TO_CREATE_INBOX);
		} else {
			MailboxPath path = new MailboxPath(session, mailboxName);
			MailboxManager manager = getMailboxManager();
			if (manager.mailboxExists(path.getUserID(), mailboxName)) {
				responder.taggedNo(request, HumanReadableText.MAILBOX_EXISTS);
			} else {
				if (path.getNamespace() != null) {
					Mailbox mailbox = manager.getMailbox(path.getUserID(),
							Mailbox.getParent(mailboxName));
					if (mailbox == null
							|| !manager.hasRight(session.getUserID(),
									mailbox.getMailboxID(),
									MailboxACL.k_CreateMailbox_RIGHT)) {
						responder.taggedNo(request,
								HumanReadableText.INSUFFICIENT_RIGHTS);
						return;
					}
				}
				// TODO Check for \Noinferiors flag
				manager.createMailbox(path.getUserID(), mailboxName);
				responder.okCompleted(request);
			}
		}
	}

}
