package com.hs.mail.imap.processor.ext;

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.SetACLRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.response.HumanReadableText;
import com.hs.mail.imap.processor.AbstractImapProcessor;
import com.hs.mail.imap.user.UserManager;

public class SetACLProcessor extends AbstractImapProcessor {

	@Override
	protected void doProcess(ImapSession session, ImapRequest message,
			Responder responder) throws Exception {
		SetACLRequest request = (SetACLRequest) message;

		MailboxACL.EditMode editMode = MailboxACL.EditMode.REPLACE;
		String rights = request.getRights();
		switch (rights.charAt(0)) {
		case '+':
			editMode = MailboxACL.EditMode.ADD;
			rights = rights.substring(1);
			break;
		case '-':
			editMode = MailboxACL.EditMode.DELETE;
			rights = rights.substring(1);
			break;
		}

		MailboxManager mailboxManager = getMailboxManager();
		Mailbox mailbox = mailboxManager.getMailbox(session.getUserID(),
				request.getMailbox());
		if (mailbox == null) {
			responder.taggedNo(request, HumanReadableText.MAILBOX_NOT_FOUND);
		} else {
			UserManager manager = getUserManager();
			String address = manager.toAddress(request.getIdentifier());
			long userid = manager.getUserID(address);
			if (userid == 0) {
				responder.taggedNo(request,
						"Identifier for " + request.getIdentifier()
								+ " not found");
			} else {
				mailboxManager.setACL(userid, mailbox.getMailboxID(), editMode,
						rights);
				responder.okCompleted(request);
			}
		}
	}

}
