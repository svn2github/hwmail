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

import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.collections.CollectionUtils;

import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.imap.message.MessageMetaData;
import com.hs.mail.pop3.POP3Exception;
import com.hs.mail.pop3.POP3Session;

/**
 * Handler for STAT command.
 * 
 * Returns the number of messages in the mailbox and its aggregate size.
 * 
 * @author Won Chul Doh
 * @since April 12, 2018
 * 
 */
public class StatProcessor extends AbstractPOP3Processor {

	@Override
	protected void doProcess(POP3Session session, TcpTransport trans,
			StringTokenizer st) {
		if (session.getState() == POP3Session.State.TRANSACTION) {
			List<MessageMetaData> uidList = session.getUidList();
			List<Long> deletedUidList = session.getDeletedUidList();
            long size = 0;
            int count = 0;
			if (CollectionUtils.isNotEmpty(uidList)) {
				for (MessageMetaData data : uidList) {
					if (!deletedUidList.contains(data.getUid())) {
						size += data.getSize();
						count++;
					}
				}
			}
			StringBuilder responseBuffer = new StringBuilder(32)
					.append(OK_RESPONSE)
					.append(" ")
					.append(count)
					.append(" ")
					.append(size);
			session.writeResponse(responseBuffer.toString());
		} else {
			throw new POP3Exception();
		}
	}

}
