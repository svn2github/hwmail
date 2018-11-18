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
import org.jboss.netty.channel.Channel;

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.mailbox.MailboxPath;
import com.hs.mail.imap.mailbox.SelectedMailbox;
import com.hs.mail.imap.mailbox.UidToMsnMapper;
import com.hs.mail.imap.message.request.AbstractMailboxRequest;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.responder.SelectResponder;
import com.hs.mail.imap.message.response.HumanReadableText;
import com.hs.mail.imap.message.response.SelectResponse;
import com.hs.mail.imap.message.response.SelectResponseBuilder;

/**
 * 
 * @author Won Chul Doh
 * @since Mar 24, 2010
 *
 */
public abstract class AbstractSelectProcessor extends AbstractImapProcessor {
	
	private SelectResponseBuilder builder = new SelectResponseBuilder();

	@Override
	protected void doProcess(ImapSession session, ImapRequest message,
			Responder responder) throws Exception {
		doProcess(session, (AbstractMailboxRequest) message,
				(SelectResponder) responder);
	}

	private void doProcess(ImapSession session, AbstractMailboxRequest request,
			SelectResponder responder) throws Exception {
		MailboxPath path = buildMailboxPath(session, request.getMailbox());
		MailboxManager manager = getMailboxManager();
		Mailbox mailbox = manager.getMailbox(path.getUserID(),
				path.getFullName());
		if (mailbox == null) {
			responder.taggedNo(request, HumanReadableText.MAILBOX_NOT_FOUND);
		} else if (mailbox.isNoSelect()) {
			responder.taggedNo(request,
					HumanReadableText.MAILBOX_NOT_SELECTABLE);
		} else {
			String rights = null;
			if (path.getNamespace() != null) {
				rights = manager.getRights(session.getUserID(),
						mailbox.getMailboxID(), true);
				if (!StringUtils.contains(rights, MailboxACL.r_Read_RIGHT)) {
					responder.taggedNo(request,
							HumanReadableText.INSUFFICIENT_RIGHTS);
					return;
				}
			}
			SelectedMailbox selected = session.getSelectedMailbox();
			if (selected != null && !selected.isReadOnly()
					&& selected.isRecent()) {
				// If not personal namespace, preserve the \Recent flag.
				if (path.getNamespace() == null) {
					// If the session is read-write, subsequent sessions will
					// not see \Recent set for the messages in this mailbox.
					manager.resetRecent(selected.getMailboxID());
				}
			}

			if (selected == null
					|| selected.getMailboxID() != mailbox.getMailboxID()) {
				manager.removeEventListener(selected);
				selected = new SelectedMailbox(session.getSessionID(),
						mailbox.getMailboxID(), isReadOnly());
				selected.setRights(rights);
				session.selected(selected);
				manager.addEventListener(selected);
			} else {
				selected.setReadOnly(isReadOnly());
			}
			
			UidToMsnMapper map = new UidToMsnMapper(selected, false);
			mailbox.setReadOnly(isReadOnly());
			SelectResponse response = builder.build(map, mailbox);
			responder.respond(response);
			
			selected.setRecent(response.getRecentMessageCount() > 0);
			
			responder.okCompleted(request, "[" + getResponseCode() + "]");
		}
	}
	
	private String getResponseCode() {
		return isReadOnly() ? "READ-ONLY" : "READ-WRITE";
	}
	
	@Override
	protected Responder createResponder(Channel channel, ImapRequest request) {
		return new SelectResponder(channel, request);
	}
	
	protected abstract boolean isReadOnly();

}
