package com.hs.mail.imap.message.responder.ext;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.channel.Channel;

import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.mailbox.MailboxACL.MailboxACLEntry;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.responder.DefaultImapResponder;
import com.hs.mail.imap.message.response.ext.ACLResponse;

/**
 * 
 * @author Wonchul Doh
 * @since December 2, 2016
 *
 */
public class ACLResponder extends DefaultImapResponder {

	public ACLResponder(Channel channel, ImapRequest request) {
		super(channel, request);
	}
	
	public void respond(ACLResponse response) {
		MailboxACL acl = response.getACL();
		untagged("ACL");
		message(acl.getMailbox());
		for (MailboxACLEntry entry : acl.getEntries()) {
			message(entry.getIdentifier());
			message(StringUtils.isNotEmpty(entry.getRights()) ? entry.getRights() : "\"\"");
		}
		end();
	}

}
