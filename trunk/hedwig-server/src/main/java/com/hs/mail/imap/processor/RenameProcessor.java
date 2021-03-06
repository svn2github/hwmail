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

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.mailbox.MailboxPath;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.RenameRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.response.HumanReadableText;

/**
 * 
 * @author Won Chul Doh
 * @since Feb 1, 2010
 *
 */
public class RenameProcessor extends AbstractImapProcessor {

	@Override
	protected void doProcess(ImapSession session, ImapRequest message,
			Responder responder) throws Exception {
		RenameRequest request = (RenameRequest) message;
		String sourceName = request.getOldName();
		String targetName = request.getNewName();
		MailboxManager manager = getMailboxManager();
		Mailbox source = manager.getMailbox(session.getUserID(), sourceName);
		MailboxPath targetPath = buildMailboxPath(session, targetName);
		// Attempt to rename from a mailbox name that does not exist
		if (source == null) {
			responder.taggedNo(request, HumanReadableText.MAILBOX_NOT_FOUND);
		} else {
			if (manager.mailboxExists(targetPath.getUserID(), targetName)) {
				// Attempt to rename to a mailbox name that already exists
				responder.taggedNo(request, HumanReadableText.MAILBOX_EXISTS);
			} else {
				// RENAME - Moving a mailbox from one parent to another requires
				// the "x" right on the mailbox itself and the "k" right for the
				// new parent.
				MailboxPath sourcePath = buildMailboxPath(session, sourceName);
				if (!sourcePath.isPersonalNamespace()) {
					if (!manager.hasRights(session.getUserID(), source, "x")) { // x_DeleteMailbox_RIGHT
						responder.taggedNo(request,
								HumanReadableText.INSUFFICIENT_RIGHTS);
						return;
					}
				}
				if (!targetPath.isPersonalNamespace()) {
					Mailbox target = manager.getMailbox(targetPath.getUserID(),
							Mailbox.getParent(targetName));
					if (target == null
							|| !manager.hasRights(session.getUserID(), target,
									"k")) { // k_CreateMailbox_RIGHT
						responder.taggedNo(request,
								HumanReadableText.INSUFFICIENT_RIGHTS);
						return;
					}
				}
				if (targetName.startsWith(sourceName + Mailbox.folderSeparator)) {
					// Check if new name invade structure as in "rename foo foo.bar"
					// would create foo.bar but delete foo.
					responder.taggedNo(request, HumanReadableText.INVADE_STRUCTURE);
				} else {
					manager.renameMailbox(source, targetName);
					responder.okCompleted(request);
				}
			}
		}
	}

}
