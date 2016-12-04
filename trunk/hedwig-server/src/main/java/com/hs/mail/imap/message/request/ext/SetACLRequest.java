package com.hs.mail.imap.message.request.ext;

import com.hs.mail.imap.message.request.AbstractMailboxRequest;

/**
 * 
 * @author Wonchul Doh
 * @since December 2, 2016
 *
 */
public class SetACLRequest extends AbstractMailboxRequest {
	
	private final String identifier;
	private final String rights;

	public SetACLRequest(String tag, String command, String mailbox,
			String identifier, String rights) {
		super(tag, command, mailbox);
		this.identifier = identifier;
		this.rights = rights;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getRights() {
		return rights;
	}

}
