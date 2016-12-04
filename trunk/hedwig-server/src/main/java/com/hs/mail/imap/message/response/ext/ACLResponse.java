package com.hs.mail.imap.message.response.ext;

import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.message.response.AbstractImapResponse;

/**
 * 
 * @author Wonchul Doh
 * @since December 2, 2016
 *
 */
public class ACLResponse extends AbstractImapResponse {

	private MailboxACL acl;

	public ACLResponse(MailboxACL acl) {
		this.acl = acl;
	}

	public MailboxACL getACL() {
		return acl;
	}
	
}
