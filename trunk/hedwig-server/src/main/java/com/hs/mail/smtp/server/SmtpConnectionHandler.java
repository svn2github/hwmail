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
import com.hs.mail.smtp.processor.hook.HookResult;
import com.hs.mail.smtp.processor.hook.HookReturnCode;
import com.hs.mail.smtp.processor.hook.RejectHook;
import com.hs.mail.smtp.processor.hook.RemoteAddrInNetwork;

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
	}
	
	public void configure() {
		if (Config.getBooleanProperty("smtp_trace_protocol", false)) {
			SmtpSession.setLogger(Config.getProperty("smtp_protocol_log", null));
		}

		String restrictions = Config.getProperty("smtpd_client_restrictions", null);
		if (StringUtils.isNotBlank(restrictions)) {
			String[] array = StringUtils.stripAll(StringUtils.split(restrictions, ","));
			DNSRBLHandler rblHandler = null;
			for (String restriction : array) {
				String[] tokens = StringUtils.split(restriction);
				if (ArrayUtils.isNotEmpty(tokens)) {
					if ("permit_mynetworks".equals(tokens[0])) {
						connectHandlers.add(new RemoteAddrInNetwork());
					} else if ("reject_rbl_client".equals(tokens[0])) {
						if (tokens.length == 2) {
							if (rblHandler == null) {
								rblHandler = new DNSRBLHandler();
								connectHandlers.add(rblHandler);
							}
							rblHandler.add(tokens[1]);
						}
					} else if ("reject".equals(tokens[0])) {
						connectHandlers.add(new RejectHook());
					}
				}
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
			if (line == null) {
				break;
			}
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

	protected void onConnect(SmtpSession session, TcpTransport trans) {
		for (ConnectHook cHandler : connectHandlers) {
			HookResult hr = cHandler.onConnect(session, trans);
			if (hr.getResult() == HookReturnCode.REJECT) {
				session.writeResponse(hr.getMessage());
				trans.endSession();
				return;
			}
			if (hr.getResult() == HookReturnCode.OK) {
				break;
			}
		}
		// Send welcome message to client
		welcome(session);
	}

	private void welcome(SmtpSession session) {
		String greetings = new StringBuilder()
				.append("220 ")
				.append(Config.getHelloName())
				.append(" Service ready")
				.toString();
		session.writeResponse(greetings);
	}

}
