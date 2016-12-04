package com.hs.mail.imap.message.builder.ext;

import java.util.LinkedList;

import com.hs.mail.imap.message.builder.ImapRequestBuilder;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.SetACLRequest;
import com.hs.mail.imap.parser.Token;
import com.hs.mail.imap.server.codec.ImapMessage;

/**
 * 
 * @author Wonchul Doh
 * @since December 2, 2016
 *
 */
public class SetACLRequestBuilder extends ImapRequestBuilder {

	@Override
	public ImapRequest createRequest(String tag, String command,
			ImapMessage message) {
		LinkedList<Token> tokens = message.getTokens();
		String mailbox = tokens.remove().value;
		String identifier = tokens.remove().value;
		String rights = tokens.remove().value;
		return new SetACLRequest(tag, command, mailbox, identifier, rights);
	}

}
