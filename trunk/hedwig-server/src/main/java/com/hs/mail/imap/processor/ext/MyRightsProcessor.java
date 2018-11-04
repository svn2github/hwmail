package com.hs.mail.imap.processor.ext;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.channel.Channel;

import com.hs.mail.container.config.Config;
import com.hs.mail.exception.MailboxNotFoundException;
import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.mailbox.MailboxPath;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.MyRightsRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.responder.ext.MyRightsResponder;
import com.hs.mail.imap.message.response.HumanReadableText;
import com.hs.mail.imap.message.response.ext.MyRightsResponse;

/**
 * The MYRIGHTS command returns the set of rights that the user has to mailbox
 * in an untagged MYRIGHTS reply.
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

	protected void doProcess(ImapSession session, MyRightsRequest request,
			MyRightsResponder responder) throws Exception {
		MailboxPath path = new MailboxPath(session, request.getMailbox());
		MailboxManager manager = getMailboxManager();
		Mailbox mailbox = manager.getMailbox(path.getUserID(),
				path.getFullName());
		if (mailbox == null) {
			throw new MailboxNotFoundException(
					HumanReadableText.MAILBOX_NOT_FOUND);
		}

		String rights = (session.getUserID() == mailbox.getOwnerID()) 
				? Config.getProperty(ACL_OWNER_RIGHTS, MailboxACL.STD_RIGHTS)
				: manager.getRights(session.getUserID(),
						mailbox.getMailboxID());
		/*
		 * RFC 4314 section 4.
		 * MYRIGHTS - any of the following rights is required to perform the
		 * operations: "l", "r", "i", "k", "x", "a"
		 */
		if (!StringUtils.containsAny(rights, "lrikxa")) {
			throw new MailboxNotFoundException(
					HumanReadableText.MAILBOX_NOT_FOUND);
		}
		responder.respond(new MyRightsResponse(request.getMailbox(), rights));
		responder.okCompleted(request);
	}
	
	@Override
	protected Responder createResponder(Channel channel, ImapRequest request) {
		return new MyRightsResponder(channel, request);
	}	

}
