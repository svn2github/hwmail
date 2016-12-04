package com.hs.mail.imap.message.responder.ext;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.channel.Channel;

import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.responder.DefaultImapResponder;
import com.hs.mail.imap.message.response.ext.MyRightsResponse;

/**
 * 
 * @author Wonchul Doh
 * @since December 4, 2016
 *
 */
public class MyRightsResponder extends DefaultImapResponder {

	public MyRightsResponder(Channel channel, ImapRequest request) {
		super(channel, request);
	}
	
	public void reponde(MyRightsResponse response) {
		untagged("MYRIGHTS");
		message(response.getMailbox());
		message(StringUtils.isNotEmpty(response.getRights()) ? response.getRights() : "\"\"");
		end();
	}

}
