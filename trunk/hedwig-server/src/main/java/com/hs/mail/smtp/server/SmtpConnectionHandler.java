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
import java.io.PrintStream;
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
import com.hs.mail.util.RollingPrintStream;

/**
 * 
 * @author Won Chul Doh
 * @since May 3, 2010
 * 
 */
public class SmtpConnectionHandler implements ConnectionHandler {

	private List<ConnectHandler> connectHandlers; 
	
	private boolean debug = false;

	private PrintStream out;	// debug output stream

	public SmtpConnectionHandler() {
		connectHandlers = new ArrayList<ConnectHandler>(2);
		connectHandlers.add(new WelcomeMessageHandler());
	}
	
	public void configure() {
		if (Config.getBooleanProperty("smtp_trace_protocol", false)) {
			this.debug = true;
			String path = Config.getProperty("smtp_protocol_log", null);
			if (path != null) {
				try {
					this.out = new RollingPrintStream(path);
				} catch (IOException e) {
					// Ignore this exception
				}
			}
		}

		String restrictions = Config.getProperty("smtps_client_restrictions", null);
		if (StringUtils.isNotBlank(restrictions)) {
			String[] array = StringUtils.stripAll(StringUtils.split(restrictions, ","));
			List<String> blacklist = new ArrayList<String>();
			boolean permit_mynetworks = false;
			for (String restriction : array) {
				String[] tokens = StringUtils.split(restriction);
				if (ArrayUtils.isNotEmpty(tokens)) {
					if ("permit_mynetworks".equals(tokens[0])) {
						permit_mynetworks = true;
					}
					if ("reject_rbl_client".equals(tokens[0])) {
						if (tokens.length > 1) {
							blacklist.add(tokens[1]);
						}
					}
				}
			}
			if (!blacklist.isEmpty()) {
				DNSRBLHandler cHandler = new DNSRBLHandler();
				cHandler.setBlacklist(blacklist.toArray(new String[blacklist.size()]));
				connectHandlers.add(0, cHandler);
			}
		}
	}
	
	public void handleConnection(Socket soc) throws IOException {
		TcpTransport trans = new TcpTransport();
		trans.setChannel(new TcpSocketChannel(soc));
		SmtpSession session = new SmtpSession(trans);
		session.setDebug(debug, out);

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
		for (ConnectHandler cHandler : connectHandlers) {
			cHandler.onConnect(session, trans);
			if (trans.isSessionEnded()) {
				return;
			}
		}
	}
	
}
