package com.hs.mail.imap.message.responder.ext.thread;

import org.jboss.netty.channel.Channel;

import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.responder.DefaultImapResponder;
import com.hs.mail.imap.message.thread.Threadable;

public class ThreadResponder extends DefaultImapResponder {

	public ThreadResponder(Channel channel, ImapRequest request) {
		super(channel, request);
	}

	public void response(Threadable thread) {
		untagged(request.getCommand());
		
	}

	void composeThread(Threadable thread) {
		openParen("(");
		closeParen(")");
	}
	
}
