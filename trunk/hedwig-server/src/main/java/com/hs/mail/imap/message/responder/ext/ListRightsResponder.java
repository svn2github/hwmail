package com.hs.mail.imap.message.responder.ext;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.channel.Channel;

import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.responder.DefaultImapResponder;
import com.hs.mail.imap.message.response.ext.ListRightsResponse;

/**
 * 
 * @author Wonchul Doh
 * @since December 3, 2016
 *
 */
public class ListRightsResponder extends DefaultImapResponder {

	public ListRightsResponder(Channel channel, ImapRequest request) {
		super(channel, request);
	}
	
	public void respond(ListRightsResponse response) {
		untagged(request.getCommand());
		message(response.getMailbox());
		message(response.getIdentifier());
		message(StringUtils.isNotEmpty(response.getRights()) ? response.getRights() : "\"\"");
		end();
	}

}
