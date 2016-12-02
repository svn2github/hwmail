package com.hs.mail.imap.message.responder.ext;

import org.jboss.netty.channel.Channel;

import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.mailbox.MailboxACL.MailboxACLEntry;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.responder.DefaultImapResponder;
import com.hs.mail.imap.message.response.ext.ACLResponse;

public class ACLResponder extends DefaultImapResponder {

	public ACLResponder(Channel channel, ImapRequest request) {
		super(channel, request);
	}
	
	public void responde(ACLResponse response) {
		MailboxACL acl = response.getACL();
		untagged("ACL");
		message(acl.getMailbox());
		for (MailboxACLEntry entry : acl.getEntries()) {
			message(entry.getIdentifier());
			message(entry.getRights());
		}
	}

}
