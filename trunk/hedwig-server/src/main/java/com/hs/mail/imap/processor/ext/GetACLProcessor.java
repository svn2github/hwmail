package com.hs.mail.imap.processor.ext;

import org.jboss.netty.channel.Channel;

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.GetACLRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.responder.ext.ACLResponder;
import com.hs.mail.imap.message.response.HumanReadableText;
import com.hs.mail.imap.message.response.ext.ACLResponse;

/**
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
		MailboxManager mailboxManager = getMailboxManager();
		Mailbox mailbox = mailboxManager.getMailbox(session.getUserID(),
				request.getMailbox());
		if (mailbox == null) {
			responder.taggedNo(request, HumanReadableText.MAILBOX_NOT_FOUND);
		} else {
			MailboxACL acl = mailboxManager.getACL(mailbox.getMailboxID());
			acl.setMailbox(request.getMailbox());
			responder.responde(new ACLResponse(acl));
			responder.okCompleted(request);
		}
	}

	@Override
	protected Responder createResponder(Channel channel, ImapRequest request) {
		return new ACLResponder(channel, request);
	}

}
