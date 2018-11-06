package com.hs.mail.imap.processor.ext;

import org.jboss.netty.channel.Channel;

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.mailbox.MailboxPath;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.GetACLRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.responder.ext.ACLResponder;
import com.hs.mail.imap.message.response.HumanReadableText;
import com.hs.mail.imap.message.response.ext.ACLResponse;

/**
 * The GETACL command returns the access control list for mailbox in an untagged
 * ACL response.
 * 
 * @author Wonchul Doh
 * @since December 2, 2016
 *
 */
public class GetACLProcessor extends AbstractACLProcessor {

	@Override
	protected void doProcess(ImapSession session, ImapRequest message,
			Responder responder) throws Exception {
		doProcess(session, (GetACLRequest) message, (ACLResponder) responder);
	}
	
	protected void doProcess(ImapSession session, GetACLRequest request,
			ACLResponder responder) throws Exception {
		MailboxManager manager = getMailboxManager();
		MailboxPath path = new MailboxPath(session, request.getMailbox());
		Mailbox mailbox = manager.getMailbox(path.getUserID(),
				path.getFullName());
		if (mailbox == null) {
			responder.taggedNo(request, HumanReadableText.MAILBOX_NOT_FOUND);
		} else {
			String rights = manager.getRights(session.getUserID(),
					mailbox.getMailboxID());
			if (rights.indexOf('l') == -1) {
				// RFC 4314 section 6
				// If not have permission to LIST, respond with the same error
				// that would be used if the mailbox did not exist.
				responder.taggedNo(request, 
						HumanReadableText.MAILBOX_NOT_FOUND);
			} else if (rights.indexOf('a') == -1) {
				responder.taggedNo(request,
						HumanReadableText.INSUFFICIENT_RIGHTS);
			} else {
				MailboxACL acl = manager.getACL(mailbox.getMailboxID());
				acl.setMailbox(request.getMailbox());
				responder.respond(new ACLResponse(acl));
				responder.okCompleted(request);
			}
		}
	}

	@Override
	protected Responder createResponder(Channel channel, ImapRequest request) {
		return new ACLResponder(channel, request);
	}

}
