package com.hs.mail.imap.message.builder.ext;

import com.hs.mail.imap.message.builder.ImapRequestBuilder;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.MyRightsRequest;
import com.hs.mail.imap.server.codec.ImapMessage;

/**
 * 
 * @author Wonchul Doh
 * @since December 4, 2016
 *
 */
public class MyRightsRequestBuilder extends ImapRequestBuilder {

	@Override
	public ImapRequest createRequest(String tag, String command, ImapMessage message) {
		String mailbox = message.getTokens().remove().value;
		return new MyRightsRequest(tag, command, mailbox);
	}

}
