package com.hs.mail.imap.message.response.ext;

import com.hs.mail.imap.message.response.AbstractImapResponse;

/**
 * 
 * @author Wonchul Doh
 * @since December 3, 2016
 *
 */
public class ListRightsResponse extends AbstractImapResponse {

	private String mailbox;
	
	private String identifier;
	
	private String rights;

	public ListRightsResponse(String mailbox, String identifier, String rights) {
		this.mailbox = mailbox;
		this.identifier = identifier;
		this.rights = rights;
	}

	public String getMailbox() {
		return mailbox;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getRights() {
		return rights;
	}	
	
}
