package com.hs.mail.util;

import com.hs.mail.imap.ImapConstants;
import com.sun.mail.imap.protocol.BASE64MailboxDecoder;
import com.sun.mail.imap.protocol.BASE64MailboxEncoder;

public class MailUtils {
	
	// decode the name (using RFC2060's modified UTF7)
	public static String decodeMailbox(String name) {
		return BASE64MailboxDecoder.decode(name);
	}
	
	public static String encodeMailbox(String name) {
		if (ImapConstants.INBOX_NAME.equalsIgnoreCase(name))
			return name.toUpperCase();
		else
			return BASE64MailboxEncoder.encode(name);
	}

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < args.length; i++) {
			System.out.println(decodeMailbox(args[i]));
		}
	}
	
}
