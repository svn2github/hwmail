package com.hs.mail.imap.message.responder.ext;

import org.apache.commons.lang3.ArrayUtils;
import org.jboss.netty.channel.Channel;

import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.responder.AbstractImapResponder;

/**
 * 
 * @author Wonchul, Doh
 * @since Dec 17, 2016
 *
 */
public class NamespaceResponder extends AbstractImapResponder {

	public NamespaceResponder(Channel channel, ImapRequest request) {
		super(channel, request);
	}
	
	public void respond(String[] personal, String[] others, String[] shared) {
		untagged(request.getCommand());
		composeNamespaces(personal);
		composeNamespaces(others);
		composeNamespaces(shared);
		end();
	}
	
	void composeNamespaces(String[] namespaces) {
		if (ArrayUtils.isEmpty(namespaces)) {
			nil();
		} else {
			openParen("(");
			for (String namespace : namespaces) {
				composeNamespace(namespace);
			}
			closeParen(")");
		}
	}
	
	void composeNamespace(String namespace) {
		openParen("(");
		quote(namespace);
		quote(Mailbox.folderSeparator);
		closeParen(")");
	}

}
