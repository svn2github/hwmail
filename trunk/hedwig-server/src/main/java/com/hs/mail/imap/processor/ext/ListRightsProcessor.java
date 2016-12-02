package com.hs.mail.imap.processor.ext;

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.ListRightsRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.response.HumanReadableText;
import com.hs.mail.imap.processor.AbstractImapProcessor;
import com.hs.mail.imap.user.UserManager;

public class ListRightsProcessor extends AbstractImapProcessor {

	@Override
	protected void doProcess(ImapSession session, ImapRequest message,
			Responder responder) throws Exception {
		ListRightsRequest request = (ListRightsRequest) message;
		
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
				if (mailbox.getOwnerID() == userid) {
					
				} else {
					
				}
			}
		}
	}

}
