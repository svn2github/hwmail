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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * Encodes an {@link ImapMessage} into a <code>ChannelBuffer</code>.
 * 
 * @author Won Chul Doh
 * @since Jan 22, 2010
 * 
 */
@ChannelPipelineCoverage("all")
public class ImapMessageEncoder extends OneToOneEncoder {

    private final String charsetName;

    public ImapMessageEncoder() {
		this(Charset.defaultCharset());
	}

	public ImapMessageEncoder(String charsetName) {
		this(Charset.forName(charsetName));
	}

	public ImapMessageEncoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        charsetName = charset.name();
	}

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if (msg instanceof String) {
			return ChannelBuffers.copiedBuffer((String) msg, charsetName);
		}
		if (msg instanceof ByteBuffer) {
			return ChannelBuffers.copiedBuffer((ByteBuffer) msg);
		}
		// Unknown message type
		return msg;
	}

}