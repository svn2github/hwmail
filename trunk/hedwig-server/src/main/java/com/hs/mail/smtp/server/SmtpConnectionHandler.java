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
package com.hs.mail.smtp.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.hs.mail.container.config.Config;
import com.hs.mail.container.server.ConnectionHandler;
import com.hs.mail.container.server.socket.TcpSocketChannel;
import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.exception.LookupException;
import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.processor.SmtpProcessor;
import com.hs.mail.smtp.processor.SmtpProcessorFactory;
import com.hs.mail.smtp.processor.hook.ConnectHook;
import com.hs.mail.smtp.processor.hook.DNSRBLHandler;

/**
 * 
 * @author Won Chul Doh
 * @since May 3, 2010
 * 
 */
public class SmtpConnectionHandler implements ConnectionHandler {

	private List<ConnectHook> connectHandlers; 
	
	public SmtpConnectionHandler() {
		connectHandlers = new ArrayList<ConnectHook>(2);
		connectHandlers.add(new WelcomeMessageHandler());
	}
	
	public void configure() {
		if (Config.getBooleanProperty("smtp_trace_protocol", false)) {
			SmtpSession.setLogger(Config.getProperty("smtp_protocol_log", null));
		}

		String rblservers = Config.getProperty("maps_rbl_domains", null);
		if (StringUtils.isNotBlank(rblservers)) {
			String[] blacklist = StringUtils.stripAll(StringUtils.split(rblservers, ","));
			if (ArrayUtils.isNotEmpty(blacklist)) {
				DNSRBLHandler cHandler = new DNSRBLHandler(blacklist);
				connectHandlers.add(0, cHandler);
			}
		}
	}
	
	public void handleConnection(Socket soc) throws IOException {
		TcpTransport trans = new TcpTransport();
		trans.setChannel(new TcpSocketChannel(soc));
		SmtpSession session = new SmtpSession(trans);

		onConnect(session, trans);

		while (!trans.isSessionEnded()) {
			String line = trans.readLine();
			if (line != null) {
				session.debug(line);
				StringTokenizer st = new StringTokenizer(line);
				if (!st.hasMoreTokens()) {
					break;
				}
				String command = st.nextToken().trim();
				try {
					SmtpProcessor processor = SmtpProcessorFactory
							.createSmtpProcessor(command);
					processor.process(session, trans, st);
				} catch (LookupException e) {
					session.writeResponse("500 5.5.1 Unknown command \"" + command
							+ "\"");
				}
			}
		}
	}

	protected void onConnect(SmtpSession session, TcpTransport trans) {
		for (ConnectHook cHandler : connectHandlers) {
			cHandler.onConnect(session, trans);
			if (trans.isSessionEnded()) {
				return;
			}
		}
	}
	
	class WelcomeMessageHandler implements ConnectHook {

		public void onConnect(SmtpSession session, TcpTransport trans) {
			String greetings = new StringBuilder()
					.append("220 ")
					.append(Config.getHelloName())
					.append(" Service ready")
					.toString();
			session.writeResponse(greetings);
		}

	}

}
