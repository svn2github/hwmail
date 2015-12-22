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

import java.util.LinkedList;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.hs.mail.imap.parser.Token;

/**
 * 
 * @author Won Chul Doh
 * @since Jan 22, 2010
 *
 */
public class DefaultImapMessage implements ImapMessage {

    private ChannelBuffer literal = ChannelBuffers.EMPTY_BUFFER;
	private long literalLength = -1;
	private boolean needContinuationRequest = false;
	protected LinkedList<Token> tokens = null;
	
	public DefaultImapMessage(LinkedList<Token> tokens) {
		this.tokens = tokens;
	}

	public String getCommand() {
		return (tokens.size() > 0) ? tokens.get(1).value : null;
	}
    
    public LinkedList<Token> getTokens() {
		return tokens;
	}

	public void setTokens(LinkedList<Token> tokens) {
		this.tokens = tokens;
		this.literalLength = -1;
		Token last = tokens.getLast();
		if (last.isLiteral()) {
			setLiteralLength(Integer.parseInt(last.value));
			setNeedContinuationRequest(last.type == Token.Type.LITERAL);
		}
	}

	public ChannelBuffer getLiteral() {
		return literal;
	}

	public void setLiteral(ChannelBuffer literal) {
		this.literal = (literal == null) 
				? ChannelBuffers.EMPTY_BUFFER
				: literal;
	}

	public long getLiteralLength() {
		return literalLength;
	}

	public void setLiteralLength(long literalLength) {
		this.literalLength = literalLength;
	}

	public boolean isNeedContinuationRequest() {
		return needContinuationRequest;
	}

	public void setNeedContinuationRequest(boolean needContinuationRequest) {
		this.needContinuationRequest = needContinuationRequest;
	}

	public boolean isNeedParsing() {
		return (tokens == null);
	}
	
}
