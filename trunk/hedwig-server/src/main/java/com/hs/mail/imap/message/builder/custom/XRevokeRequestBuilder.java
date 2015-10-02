package com.hs.mail.imap.message.builder.custom;

import java.util.LinkedList;

import com.hs.mail.imap.message.SequenceRange;
import com.hs.mail.imap.message.builder.AbstractUidRequestBuilder;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.custom.XRevokeRequest;
import com.hs.mail.imap.parser.Token;
import com.hs.mail.imap.server.codec.ImapMessage;

/**
 * 
 * @author Won Chul Doh
 * @since Aug 15, 2011
 *
 */
public class XRevokeRequestBuilder extends AbstractUidRequestBuilder {

	@Override
	public ImapRequest createRequest(String tag, String command,
			ImapMessage message, boolean useUID) {
		LinkedList<Token> tokens = message.getTokens();
		SequenceRange[] sequenceSet = parseSequenceSet(tokens);
		return new XRevokeRequest(tag, command, sequenceSet, useUID);
	}

}
