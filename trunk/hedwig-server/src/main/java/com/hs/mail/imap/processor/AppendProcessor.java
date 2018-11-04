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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.mail.Flags;
import javax.mail.Flags.Flag;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;

import com.hs.mail.container.config.Config;
import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.mailbox.MailboxPath;
import com.hs.mail.imap.message.request.AppendRequest;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.response.HumanReadableText;

/**
 * 
 * RFC 3501 - 6.3.11 APPEND command implementation
 * 
 * @author Won Chul Doh
 * @since Feb 1, 2010
 *
 */
public class AppendProcessor extends AbstractImapProcessor {

	public AppendProcessor() {
		super();
	}

	@Override
	protected void doProcess(ImapSession session, ImapRequest message,
			Responder responder) throws Exception {
		AppendRequest request = (AppendRequest) message;
		MailboxManager manager = getMailboxManager();
		MailboxPath path = new MailboxPath(session, request.getMailbox());
		Mailbox mailbox = manager.getMailbox(path.getUserID(),
				path.getFullName());
		if (mailbox == null) {
			// SHOULD NOT automatically create the mailbox.
			responder.taggedNo(request, "[TRYCREATE]",
					HumanReadableText.MAILBOX_NOT_FOUND);
		} else {
			if (path.getNamespace() != null) {
				// Before performing a COPY/APPEND command, the server MUST
				// check if the user has "i" right for the target mailbox.
				String rights = manager.getRights(session.getUserID(),
						mailbox.getMailboxID());
				if (rights.indexOf('i') == -1) {
					responder.taggedNo(request,
							HumanReadableText.INSUFFICIENT_RIGHTS);
					return;
				}
				Flags flags = request.getFlags();
				if (flags != null) {
					// The server MUST NOT fail a COPY/APPEND if the user has no
					// rights to set a particular flag.
					flags = removeUnauthorizedFlags(flags, rights);
					request.setFlags(flags);
				}
			}
			File temp = File.createTempFile("mail", null, Config.getTempDirectory());
			ChannelBuffer buffer = request.getMessage();
			try {
				writeMessage(buffer, temp);
				manager.appendMessage(mailbox.getMailboxID(),
						request.getDatetime(), request.getFlags(), temp);
			} catch (Exception ex) {
				forceDelete(temp);
				throw ex;
			}
			responder.okCompleted(request);
		}
	}
	
	private Flags removeUnauthorizedFlags(Flags flags, String rights) {
		if (!flags.contains(Flag.DELETED) && !flags.contains(Flag.SEEN)) {
			if (rights.indexOf('w') == -1)
				return null;
		}
		if (flags.contains(Flag.DELETED) && rights.indexOf('t') == -1) {
			flags.remove(Flag.DELETED);
		}
		if (flags.contains(Flag.SEEN) && rights.indexOf('s') == -1) {
			flags.remove(Flag.SEEN);
		}
		return flags;
	}

	private void writeMessage(ChannelBuffer buffer, File dst)
			throws IOException {
		ChannelBufferInputStream is = new ChannelBufferInputStream(buffer);
		OutputStream os = null;
		try {
			os = new FileOutputStream(dst);
			IOUtils.copyLarge(is, os);
		} finally {
			IOUtils.closeQuietly(os);
		}
	}

	private void forceDelete(File file) {
		try {
			FileUtils.forceDelete(file);
		} catch (IOException e) {
			// Don't re-throw this exception
		}
	}	

}
