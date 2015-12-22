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
package com.hs.mail.imap.server.codec;

import java.io.StringReader;
import java.util.LinkedList;

import org.apache.commons.lang3.ArrayUtils;
import org.jboss.netty.util.CharsetUtil;

import com.hs.mail.imap.parser.CommandParser;
import com.hs.mail.imap.parser.LiteralException;
import com.hs.mail.imap.parser.Token;



/**
 * 
 * @author Won Chul Doh
 * @since Jan 22, 2010
 *
 */
public class ImapRequestDecoder extends ImapMessageDecoder {

	public ImapRequestDecoder() {
		super();
	}

	public ImapRequestDecoder(int maxLineLength) {
		super(maxLineLength);
	}

	@Override
	protected ImapMessage createMessage() {
		ImapMessage message = new DefaultImapMessage(null);
		parseMessage(message);
		return message;
	}
	
	@Override
	protected void parseMessage(ImapMessage message) {
		try {
			LinkedList<Token> tokens = parse(request);
			message.setTokens(tokens);
		} catch (LiteralException ex) {
			message.setLiteralLength(ex.getLength());
			message.setNeedContinuationRequest(!ex.isSynchronous());
		}
	}
	
	private LinkedList<Token> parse(String line) {
		if (content != null) {
			astring = ArrayUtils.add(astring, content.toString(CharsetUtil.UTF_8));
		}
		CommandParser parser = new CommandParser(new StringReader(request), astring);
		return parser.command();
	}
	
}
