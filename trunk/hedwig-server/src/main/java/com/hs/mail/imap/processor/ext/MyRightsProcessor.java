package com.hs.mail.imap.processor.ext;

import org.jboss.netty.channel.Channel;

import com.hs.mail.container.config.Config;
import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.MyRightsRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.responder.ext.MyRightsResponder;
import com.hs.mail.imap.message.response.HumanReadableText;
import com.hs.mail.imap.message.response.ext.MyRightsResponse;

/**
 * 
 * @author Wonchul Doh
 * @since December 4, 2016
 *
 */
public class MyRightsProcessor extends AbstractACLProcessor {

	@Override
	protected void doProcess(ImapSession session, ImapRequest message, Responder responder) throws Exception {
		doProcess(session, (MyRightsRequest) message, (MyRightsResponder) responder);
	}

	protected void doProcess(ImapSession session, MyRightsRequest request, MyRightsResponder responder)
			throws Exception {
		MailboxManager mailboxManager = getMailboxManager();
		Mailbox mailbox = mailboxManager.getMailbox(session.getUserID(), request.getMailbox());
		if (mailbox == null) {
			responder.taggedNo(request, HumanReadableText.MAILBOX_NOT_FOUND);
		} else {
			String rights = (session.getUserID() == mailbox.getOwnerID())
					? Config.getProperty(ACL_OWNER_RIGHTS, MailboxACL.STD_RIGHTS)
					: mailboxManager.getRights(session.getUserID(), mailbox.getMailboxID());
			responder.reponde(new MyRightsResponse(request.getMailbox(), rights));
			responder.okCompleted(request);
		}
	}
	
	@Override
	protected Responder createResponder(Channel channel, ImapRequest request) {
		return new MyRightsResponder(channel, request);
	}	

}
