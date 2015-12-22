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

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

/**
 * Decodes <code>ChannelBuffer</code> into {@link ImapMessage}.
 * 
 * @author Won Chul Doh
 * @since Jan 22, 2010
 * 
 */
public abstract class ImapMessageDecoder extends
		ReplayingDecoder<ImapMessageDecoder.State> {

	/**
	 * Carriage return
	 */
	private static final byte CR = 13;

	/**
	 * Line feed character
	 */
	private static final byte LF = 10;
	
	private final int maxLineLength;
	protected String request;
	protected String[] astring;
	protected ImapMessage message;
	protected ChannelBuffer content;

	/**
	 * The internal state of <code>ImapMessageDecoder</code>.
	 * <em>Internal use only</em>.
	 */
	protected enum State {
		READ_COMMAND, 
		READ_LITERAL,
		READ_REMAINDER
	}

	/**
	 * Creates a new instance with the default.
	 * {@code maxLineLength (8192)}
	 */
	protected ImapMessageDecoder() {
		this(8192);
	}

	/**
	 * Creates a new instance with the specific parameter.
	 */
	protected ImapMessageDecoder(int maxLineLength) {
		super(State.READ_COMMAND, true);

		if (maxLineLength <= 0) {
			throw new IllegalArgumentException(
					"maxLineLength must be a positive integer: "
							+ maxLineLength);
		}
		this.maxLineLength = maxLineLength;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer, State state) throws Exception {
		switch (state) {
		case READ_COMMAND: {
			request = readLine(buffer, maxLineLength);
			message = createMessage();
			if (message.getLiteralLength() != -1) {
				checkpoint(State.READ_LITERAL);
				resetLiteral(channel);
			} else {
				return message;
			}
		}
		case READ_LITERAL: {
			// we have a content-length so we just read the correct number of
			// bytes
			readFixedLengthContent(buffer);
			checkpoint(State.READ_REMAINDER);
		}
		case READ_REMAINDER: {
			// FIXME - RFC 3501 7.5 - Read remainder of the command
			String remainder = readLine(buffer, maxLineLength);
			if (message.isNeedParsing()) {
				request = StringUtils.chomp(request) + remainder;
				parseMessage(message);
				if (message.getLiteralLength() != -1) {
					checkpoint(State.READ_LITERAL);
					resetLiteral(channel);
					return null;
				}
			}
			return reset();
		}
		default:
			throw new Error("Shouldn't reach here.");
		}
	}
	
	private Object reset() throws Exception {
		ImapMessage message = this.message;
		ChannelBuffer content = this.content;

		if (content != null) {
			message.setLiteral(content);
			this.content = null;
		}
		this.message = null;
		this.request = null;
		this.astring = null;

		checkpoint(State.READ_COMMAND);
		return message;
	}
	
	private void resetLiteral(Channel channel) {
		this.content = null;
		if (message.isNeedContinuationRequest()) {
			// Need continuation request
			channel.write("+ OK\r\n");
		}
	}

	private void readFixedLengthContent(ChannelBuffer buffer) {
		long length = message.getLiteralLength();
		if (content == null) {
			content = buffer.readBytes((int) length);
		} else {
			content.writeBytes(buffer.readBytes((int) length));
		}
	}
	
	private String readLine(ChannelBuffer buffer, int maxLineLength)
			throws TooLongFrameException {
		StringBuilder sb = new StringBuilder(128);
		int lineLength = 0;
		while (true) {
			byte nextByte = buffer.readByte();
			if (nextByte == CR) {
				nextByte = buffer.readByte();
				if (nextByte == LF) {
					sb.append('\n');
					return sb.toString();
				}
			} else if (nextByte == LF) {
				sb.append('\n');
				return sb.toString();
			} else {
				if (lineLength >= maxLineLength) {
					throw new TooLongFrameException(
							"An IMAP command is larger than " + maxLineLength
									+ " bytes.");
				}
				lineLength++;
				sb.append((char) nextByte);
			}
		}
	}
	
	protected abstract ImapMessage createMessage();
	protected abstract void parseMessage(ImapMessage message);

}
