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
package com.hs.mail.imap.server;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;

import com.hs.mail.util.Log4jUtils;

/**
 * A ChannelHandler that logs all protocol streams.
 * 
 * @author Won Chul Doh
 * @since Jul 7, 2010
 */

public class DebuggingHandler implements ChannelUpstreamHandler,
		ChannelDownstreamHandler {
	
	private static Logger logger = Logger.getLogger(DebuggingHandler.class);

	public DebuggingHandler() {
	}

    public void setLogger(String filename) {
    	logger.setLevel(Level.DEBUG);
    	logger.setAdditivity(false);
    	Log4jUtils.addAppender(logger, filename, "%m");
    }
    
	private String toString(ChannelBuffer buffer) {
		byte[] dst = new byte[buffer.readableBytes()];
		buffer.getBytes(buffer.readerIndex(), dst);
		return new String(dst);
	}

	/**
	 * Logs the specified event to the {@link out} returned by
	 * {@link #getDebugOut()}.
	 */
	public void debug(ChannelEvent e) {
		if (logger.isDebugEnabled()) {
			if (e instanceof MessageEvent) {
				Object msg = ((MessageEvent) e).getMessage();
		        if (msg instanceof ChannelBuffer) {
		        	logger.debug(toString((ChannelBuffer) msg));
		        } else {
		        	logger.debug(msg);
		        }
			}
		}
	}
	
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e)
			throws Exception {
		if (e != null) {
			debug(e);
			ctx.sendUpstream(e);
		}
	}

	public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e)
			throws Exception {
		if (e != null) {
			debug(e);
			ctx.sendDownstream(e);
		}
	}

}
