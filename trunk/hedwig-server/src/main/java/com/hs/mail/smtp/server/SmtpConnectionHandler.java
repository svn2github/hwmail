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
import java.io.InterruptedIOException;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;

import com.hs.mail.container.config.Config;
import com.hs.mail.container.server.ConnectionHandler;
import com.hs.mail.container.server.socket.TcpSocketChannel;
import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.exception.LookupException;
import com.hs.mail.io.LineReader;
import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.processor.SmtpProcessor;
import com.hs.mail.smtp.processor.SmtpProcessorFactory;
import com.hs.mail.smtp.processor.hook.ConnectHook;
import com.hs.mail.smtp.processor.hook.HookFactory;
import com.hs.mail.smtp.processor.hook.HookResult;
import com.hs.mail.smtp.processor.hook.HookReturnCode;
import com.hs.mail.smtp.processor.hook.MailLog;

/**
 * 
 * @author Won Chul Doh
 * @since May 3, 2010
 * 
 */
public class SmtpConnectionHandler implements ConnectionHandler {

	private List<ConnectHook> connectHandlers = null; 
	
	public void configure() throws Exception {
		if (Config.getBooleanProperty("smtp_trace_protocol", false)) {
			SmtpSession.setLogger(Config.getProperty("smtp_protocol_log", null));
		}
		connectHandlers = HookFactory.getHooks(ConnectHook.class,
				"smtpd_client_restrictions", null);
	}
	
	public void handleConnection(Socket soc) throws IOException {
		SmtpSession session = null;		

		try {
			TcpTransport trans = new TcpTransport();
			trans.setChannel(new TcpSocketChannel(soc));
			session = new SmtpSession(trans);

			MailLog.connect(session);
			
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
					session.writeResponse("500 5.5.1 Unknown command \""
							+ command + "\"");
				}
			}
		} catch (LineReader.TerminationException te) {
			session.writeResponse("501 Syntax error."
					+ " CR and LF must be CRLF paired."
					+ "  See RFC 2821 #2.7.1.");
		} catch (LineReader.LineLengthExceededException llee) {
			session.writeResponse("500 Line length exceeded."
					+ " See RFC 2821 #4.5.3.1.");
		} catch (InterruptedIOException ie) {
			// The socket is still connected event though TIMEOUT expires
			session.writeResponse("421 Session timeout,"
					+ "closing transmission channel.");
		} finally {
			MailLog.disconnect(session);
			if (soc != null) {
				try {
					soc.close();
				} catch (IOException e) {
					// IGNORE
				} finally {
					soc = null;
				}
			}
		}
	}

	protected void onConnect(SmtpSession session, TcpTransport trans) {
		if (connectHandlers != null) {
			for (ConnectHook cHandler : connectHandlers) {
				HookResult hr = cHandler.onConnect(session, trans);
				if (hr.getResult() == HookReturnCode.REJECT) {
					MailLog.reject(session, hr.getMessage());
					session.writeResponse(hr.getMessage());
					trans.endSession();
					return;
				}
				if (hr.getResult() == HookReturnCode.OK) {
					break;
				}
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
