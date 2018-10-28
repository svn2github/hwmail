/*
 * Copyright 2010 the original author or authors.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.hs.mail.imap.message.responder.ext;

import org.jboss.netty.channel.Channel;

import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.responder.DefaultImapResponder;
import com.hs.mail.imap.message.thread.Threadable;

/**
 * 
 * @author Won Chul Doh
 * @since Oct 1, 2018
 *
 */
public class ThreadResponder extends DefaultImapResponder {

	public ThreadResponder(Channel channel, ImapRequest request) {
		super(channel, request);
	}

	public void respond(Threadable thread) {
		untagged(request.getCommand());
		composeThread(thread, true);
		end();
	}

	void composeThread(Threadable first, boolean split) {
		for (Threadable thread = first; thread != null; 
				thread = thread.getNext()) {
			// split into sub-threads
			if (split) {
				openParen("(");
			}
			if (!thread.isDummy()) {
				message(thread.getUID());
			}
			Threadable child = thread.getChild();
			if (child != null) {
				composeThread(child, (child.getNext() != null));
			}
			if (split) {
				closeParen(")");
				skipNextSpace();
			}
		}
	}

}
