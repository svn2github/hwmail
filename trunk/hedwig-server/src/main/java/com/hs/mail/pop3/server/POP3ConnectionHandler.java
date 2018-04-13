/*
 * Copyright 2018 the original author or authors.
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
package com.hs.mail.pop3.server;

import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

import com.hs.mail.container.server.ConnectionHandler;
import com.hs.mail.container.server.socket.TcpSocketChannel;
import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.exception.LookupException;
import com.hs.mail.io.LineReader;
import com.hs.mail.pop3.POP3Session;
import com.hs.mail.pop3.processor.POP3Processor;
import com.hs.mail.pop3.processor.POP3ProcessorFactory;

/**
 * 
 * @author Won Chul Doh
 * @since April 11, 2018
 * 
 */
public class POP3ConnectionHandler implements ConnectionHandler {

	@Override
	public void configure() throws Exception {
	}

	@Override
	public void handleConnection(Socket soc) throws IOException {
		POP3Session session = null;

		try {
			TcpTransport trans = new TcpTransport();
			trans.setChannel(new TcpSocketChannel(soc));
			session = new POP3Session(trans);
		
			onConnect(session, trans);
		
			while (!trans.isSessionEnded()) {
				String line = trans.readLine();
				if (line == null) {
					break;
				}
				StringTokenizer st = new StringTokenizer(line);
				if (!st.hasMoreTokens()) {
					break;
				}
				String command = st.nextToken().trim();
				try {
					POP3Processor processor = POP3ProcessorFactory
							.createPOP3Processor(command);
					processor.process(session, trans, st);
				} catch (LookupException e) {
					StringBuilder responseBuffer = new StringBuilder(64)
							.append(POP3Processor.ERR_RESPONSE)
							.append(" Unknown command \"")
							.append(command)
							.append("\"");
					session.writeResponse(responseBuffer.toString());
				}
			}
		} catch (LineReader.TerminationException te) {
			StringBuilder responseBuffer = new StringBuilder(64)
					.append(POP3Processor.ERR_RESPONSE)
					.append(" Syntax error.")
					.append(" CR and LF must be CRLF paired.")
					.append(" See RFC 1939 #3.");
			session.writeResponse(responseBuffer.toString());
		} finally {
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

	protected void onConnect(POP3Session session, TcpTransport trans) {
		StringBuilder responseBuffer = new StringBuilder(64)
				.append(POP3Processor.OK_RESPONSE)
				.append(" POP3 server ready");
		session.writeResponse(responseBuffer.toString());
	}
	
}
