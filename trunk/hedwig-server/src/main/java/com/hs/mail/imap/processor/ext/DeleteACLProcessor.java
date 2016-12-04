package com.hs.mail.imap.processor.ext;

import javax.security.auth.login.AccountNotFoundException;

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxACL.EditMode;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.DeleteACLRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.response.HumanReadableText;

/**
 * 
 * @author Wonchul Doh
 * @since December 2, 2016
 *
 */
public class DeleteACLProcessor extends AbstractACLProcessor {

	@Override
	protected void doProcess(ImapSession session, ImapRequest message, Responder responder) throws Exception {
		DeleteACLRequest request = (DeleteACLRequest) message;

		MailboxManager mailboxManager = getMailboxManager();
		Mailbox mailbox = mailboxManager.getMailbox(session.getUserID(), request.getMailbox());
		if (mailbox == null) {
			responder.taggedNo(request, HumanReadableText.MAILBOX_NOT_FOUND);
		} else {
			try {
				long userid = getUserID(request.getIdentifier());
				mailboxManager.setACL(userid, mailbox.getMailboxID(), EditMode.REPLACE, null);
				responder.okCompleted(request);
			} catch (AccountNotFoundException e) {
				responder.taggedNo(request, e.getMessage());
			}
		}
	}

}
