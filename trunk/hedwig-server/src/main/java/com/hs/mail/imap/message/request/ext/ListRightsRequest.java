package com.hs.mail.imap.message.request.ext;

import com.hs.mail.imap.message.request.AbstractMailboxRequest;

public class ListRightsRequest extends AbstractMailboxRequest {

	private final String identifier;

	public ListRightsRequest(String tag, String command, String mailbox,
			String identifier) {
		super(tag, command, mailbox);
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

}
