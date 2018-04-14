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

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.imap.message.MessageMetaData;
import com.hs.mail.pop3.POP3Exception;
import com.hs.mail.pop3.POP3Session;

/**
 * Handler for TOP command.
 * 
 * Retrieves the top N lines of a specified message in the mailbox.
 * 
 * The expected command format is
 *  TOP [mail message number] [number of lines to return]
 * 
 * @author Won Chul Doh
 * @since April 13, 2018
 * 
 */
public class TopProcessor extends RetrProcessor {

	@Override
	void doProcess(POP3Session session, TcpTransport trans, StringTokenizer st) {
		if (session.getState() == POP3Session.State.TRANSACTION) {
			if (st.countTokens() == 2) {
				int msg = POP3Utils.getNumberParameter(nextToken(st));
				int num = POP3Utils.getNumberParameter(nextToken(st));
				doTOP(session, msg, num);
			} else {
				throw POP3Exception.INVALID_ARGS;
			}
		} else {
			throw POP3Exception.INVALID_STATE;
		}
	}

	private void doTOP(POP3Session session, int msg, int num) {
		MessageMetaData data = POP3Utils.getMetaData(session, msg);
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
					.append("Top of message follows");
			session.writeResponse(responseBuffer.toString());
			
			writeMessageContentTo(session,
					new CountingBodyInputStream(content, num));

		} catch (IOException ioe) {
			StringBuilder responseBuffer = new StringBuilder(64)
					.append("Error while retrieving message.");
			throw new POP3Exception(responseBuffer.toString(), ioe);
		}
	}

    /**
     * This {@link InputStream} implementation can be used to return all message headers 
     * and limit the body lines which will be read from the wrapped {@link InputStream}.
     */   
    private final class CountingBodyInputStream extends InputStream {

        private int count = 0;
        private int limit = -1;
        private int lastChar;
        private final InputStream in;
        private boolean isBody = false; // starting from header
        private boolean isEmptyLine = false;

        /**
         * 
         * @param in
         *            InputStream to read from
         * @param limit
         *            the lines to read. -1 is used for no limits
         */
        public CountingBodyInputStream(InputStream in, int limit) {
            this.in = in;
            this.limit = limit;
        }

        @Override
        public int read() throws IOException {
            if (limit != -1) {
                if (count <= limit) {
                    int a = in.read();

                    // check for empty line
                    if (!isBody && isEmptyLine && lastChar == '\r' && a == '\n') {
                    	// reached body
                    	isBody = true;
                    }

                    if (lastChar == '\r' && a == '\n') {
                    	// reset empty line flag
                    	isEmptyLine = true;

                    	if (isBody) {
                    		count++;
                    	}
                    } else if (lastChar == '\n' && a != '\r') {
                    	isEmptyLine = false;
                    }

                    lastChar = a;

                    return a;
                } else {
                    return -1;
                }
            } else {
                return in.read();
            }

        }

        @Override
        public long skip(long n) throws IOException {
            return in.skip(n);
        }

        @Override
        public int available() throws IOException {
            return in.available();
        }

        @Override
        public void close() throws IOException {
            in.close();
        }

        @Override
        public void mark(int readlimit) {
            // not supported
        }

        @Override
        public void reset() throws IOException {
            // do nothing as mark is not supported
        }

        @Override
        public boolean markSupported() {
            return false;
        }

    }

}
