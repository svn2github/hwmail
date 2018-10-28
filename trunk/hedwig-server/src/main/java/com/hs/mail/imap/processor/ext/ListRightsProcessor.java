package com.hs.mail.imap.processor.ext;

import javax.security.auth.login.AccountNotFoundException;

import org.jboss.netty.channel.Channel;

import com.hs.mail.container.config.Config;
import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.ListRightsRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.responder.ext.ListRightsResponder;
import com.hs.mail.imap.message.response.ext.ListRightsResponse;

/**
 * 
 * @author Wonchul Doh
 * @since December 3, 2016
 *
 */
public class ListRightsProcessor extends AbstractACLProcessor {

	@Override
	protected void doProcess(ImapSession session, ImapRequest message, Responder responder) throws Exception {
		doProcess(session, (ListRightsRequest) message, (ListRightsResponder) responder);
	}

	protected void doProcess(ImapSession session, ListRightsRequest request,
			ListRightsResponder responder) throws Exception {
		Mailbox mailbox = getAuthorizedMailbox(session, request);
		String rights = listRights(request.getIdentifier(), mailbox);
		responder.respond(new ListRightsResponse(request.getMailbox(), request
				.getIdentifier(), rights));
		responder.okCompleted(request);
	}
	
	@Override
	protected Responder createResponder(Channel channel, ImapRequest request) {
		return new ListRightsResponder(channel, request);
	}

	private String listRights(String identifier, Mailbox mailbox)
			throws AccountNotFoundException {
		long userid = getUserID(identifier);
		if (ImapConstants.ANYONE_ID == userid) {
			return Config.getProperty(ACL_ANYONE_RIGHTS, MailboxACL.STD_RIGHTS);
		} else if (mailbox.getOwnerID() == userid) {
			return Config.getProperty(ACL_OWNER_RIGHTS, MailboxACL.STD_RIGHTS);
		} else {
			return Config.getProperty(ACL_OTHER_RIGHTS, MailboxACL.STD_RIGHTS);
		}
	}	

}
