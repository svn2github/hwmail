package com.hs.mail.imap.message.builder.custom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.hs.mail.imap.message.builder.AbstractUidRequestBuilder;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.custom.XRevokeRequest;
import com.hs.mail.imap.parser.Token;
import com.hs.mail.imap.server.codec.DecoderUtils;
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
		long sequenceNumber = DecoderUtils
				.parseSeqNumber(tokens.remove().value);
		XRevokeRequest request = new XRevokeRequest(tag, command,
				sequenceNumber, useUID);
		if (!tokens.isEmpty()) {
			request.setFlag(tokens.remove().value);
			if (!tokens.isEmpty()) {
				List<String> recipients = new ArrayList<String>();
				while (!tokens.isEmpty()) {
					recipients.add(tokens.remove().value);
				}
				request.setRecipients(recipients);
			}
		}
		return request;
	}

}
