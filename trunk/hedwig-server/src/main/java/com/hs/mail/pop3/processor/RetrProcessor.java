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
package com.hs.mail.pop3.processor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;

import com.hs.mail.container.config.Config;
import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.message.FetchData;
import com.hs.mail.imap.message.MessageMetaData;
import com.hs.mail.imap.message.response.FetchResponse;
import com.hs.mail.io.ExtraDotOutputStream;
import com.hs.mail.pop3.POP3Exception;
import com.hs.mail.pop3.POP3Session;
import com.hs.mail.util.FileUtils;

/**
 * Handler for RETR command.
 * 
 * Retrieves a particular mail message from the mailbox.
 * 
 * @author Won Chul Doh
 * @since April 12, 2018
 * 
 */
public class RetrProcessor extends AbstractPOP3Processor {

	@Override
	void doProcess(POP3Session session, TcpTransport trans, StringTokenizer st) {
		if (session.getState() == POP3Session.State.TRANSACTION) {
			if (st.countTokens() == 1) {
				int num = POP3Utils.getNumberParameter(nextToken(st));
				doRETR(session, num);
			} else {
				throw POP3Exception.INVALID_ARGS;
			}
		} else {
			throw POP3Exception.INVALID_STATE;
		}
	}

	private void doRETR(POP3Session session, int num) {
		MessageMetaData data = POP3Utils.getMetaData(session, num);
		try {
			InputStream content = getMessage(data.getUid());
			if (content == null) {
				StringBuilder responseBuffer = new StringBuilder(64)
						.append("Message ")
						.append(num)
						.append(" does not exist.");
				throw new POP3Exception(responseBuffer.toString());
			}
			
			StringBuilder responseBuffer = new StringBuilder(64)
					.append(OK_RESPONSE)
					.append(" ")
					.append(data.getSize())
					.append(" octets");
			session.writeResponse(responseBuffer.toString());

			writeMessageContentTo(session, content);

		} catch (IOException ioe) {
			StringBuilder responseBuffer = new StringBuilder(64)
					.append("Error while retrieving message.");
			throw new POP3Exception(responseBuffer.toString(), ioe);
		}
	}

	/**
	 * {@link FetchResponse#getInputStream} 
	 */
	protected InputStream getMessage(long uid) throws IOException {
		MailboxManager manager = getMailboxManager();
		FetchData fd = manager.getMessageFetchData(uid);
		if (fd != null) {
			File file = Config.getDataFile(fd.getInternalDate(), fd.getPhysMessageID());
			if (FileUtils.isCompressed(file, false)) {
				return new GZIPInputStream(new FileInputStream(file));
			} else if (file.exists()) {
				return new BufferedInputStream(new FileInputStream(file));
			}
		}
		return null;
	}

	protected void writeMessageContentTo(POP3Session session,
			InputStream content) throws IOException {
		OutputStream out = session.getTransport().getOutputStream();
		try {
			ExtraDotOutputStream edouts = new ExtraDotOutputStream(out);
			IOUtils.copy(content, edouts);
			edouts.checkCRLFTerminator();
			edouts.flush();
		} finally {
			IOUtils.closeQuietly(content);
			out.write(".\r\n".getBytes());
			out.flush();
		}
	}
	
}
