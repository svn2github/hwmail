package com.hs.mail.imap.processor.ext;

import javax.security.auth.login.AccountNotFoundException;

import org.jboss.netty.channel.Channel;

import com.hs.mail.container.config.Config;
import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.ListRightsRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.responder.ext.ListRightsResponder;
import com.hs.mail.imap.message.response.HumanReadableText;
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

	protected void doProcess(ImapSession session, ListRightsRequest request, ListRightsResponder responder)
			throws Exception {
		MailboxManager mailboxManager = getMailboxManager();
		Mailbox mailbox = mailboxManager.getMailbox(session.getUserID(),
				request.getMailbox());
		if (mailbox == null) {
			responder.taggedNo(request, HumanReadableText.MAILBOX_NOT_FOUND);
		} else {
			try {
				long userid = getUserID(request.getIdentifier());
				String rights = MailboxACL.STD_RIGHTS;
				if (MailboxACL.ANYONE_ID == userid) {
					rights = Config.getProperty(ACL_ANYONE_RIGHTS, rights);
				} else if (mailbox.getOwnerID() == userid) {
					rights = Config.getProperty(ACL_OWNER_RIGHTS, rights);
				} else {
					rights = Config.getProperty(ACL_OTHER_RIGHTS, rights);
				}
				responder.responde(new ListRightsResponse(request.getMailbox(), request.getIdentifier(), rights));
				responder.okCompleted(request);
			} catch (AccountNotFoundException e) {
				responder.taggedNo(request, "failed. " + e.getMessage());
			}
		}
	}
	
	@Override
	protected Responder createResponder(Channel channel, ImapRequest request) {
		return new ListRightsResponder(channel, request);
	}
	
}
