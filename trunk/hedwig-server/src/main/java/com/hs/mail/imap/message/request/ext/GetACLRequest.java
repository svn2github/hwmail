package com.hs.mail.imap.message.request.ext;

import com.hs.mail.imap.message.request.AbstractMailboxRequest;

public class GetACLRequest extends AbstractMailboxRequest {

	public GetACLRequest(String tag, String command, String mailbox) {
		super(tag, command, mailbox);
	}

}
