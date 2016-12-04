package com.hs.mail.imap.message.response.ext;

import com.hs.mail.imap.message.response.AbstractImapResponse;

/**
 * 
 * @author Wonchul Doh
 * @since December 4, 2016
 *
 */
public class MyRightsResponse extends AbstractImapResponse {

	private String mailbox;
	
	private String rights;

	public MyRightsResponse(String mailbox, String rights) {
		this.mailbox = mailbox;
		this.rights = rights;
	}

	public String getMailbox() {
		return mailbox;
	}

	public String getRights() {
		return rights;
	}

}
