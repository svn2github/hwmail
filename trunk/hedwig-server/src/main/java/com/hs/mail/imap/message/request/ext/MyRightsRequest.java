package com.hs.mail.imap.message.request.ext;

import com.hs.mail.imap.message.request.AbstractMailboxRequest;

/**
 * 
 * @author Wonchul Doh
 * @since December 4, 2016
 *
 */
public class MyRightsRequest extends AbstractMailboxRequest {

	public MyRightsRequest(String tag, String command, String mailbox) {
		super(tag, command, mailbox);
	}

}
