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
package com.hs.mail.imap.processor.ext.thread;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.jboss.netty.channel.Channel;

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.mailbox.SelectedMailbox;
import com.hs.mail.imap.mailbox.UidToMsnMapper;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.ThreadRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.responder.ext.ThreadResponder;
import com.hs.mail.imap.message.thread.Threadable;
import com.hs.mail.imap.processor.AbstractImapProcessor;

/**
 * 
 * @author Won Chul Doh
 * @since Oct 1, 2018
 *
 */
public class ThreadProcessor extends AbstractImapProcessor {

	@Override
	protected void doProcess(ImapSession session, ImapRequest message,
			Responder responder) throws Exception {
		doProcess(session, (ThreadRequest) message,
				(ThreadResponder) responder);
	}
		
	@Override
	protected Responder createResponder(Channel channel, ImapRequest request) {
		return new ThreadResponder(channel, request);
	}

	protected void doProcess(ImapSession session, ThreadRequest request,
			ThreadResponder responder) throws Exception {
		SelectedMailbox selected = session.getSelectedMailbox();
		MailboxManager manager = getMailboxManager();
		UidToMsnMapper map = new UidToMsnMapper(selected, request.isUseUID());
		String algorithm = request.getAlgorithm();
		List<Threadable> results = manager.searchThread(
				!"ORDEREDSUBJECT".equalsIgnoreCase(algorithm), map,
				selected.getMailboxID(), request.getSearchKey());
		if (CollectionUtils.isNotEmpty(results)) {
			if (!request.isUseUID()) {
				getMessageNumbers(map, results);
			}
			Threadable thread = buildThread(algorithm, results);
			responder.response(thread);
		}
		responder.okCompleted(request);
	}

	private void getMessageNumbers(UidToMsnMapper map,
			List<Threadable> threadables) {
		Iterator<Threadable> it = threadables.iterator();
		while (it.hasNext()) {
			Threadable t = it.next();
			long msgnum = map.getMessageNumber(t.getUID());
			if (msgnum == -1) {
				it.remove();
			} else {
				t.setUID(msgnum);
			}
		}
	}
	
	private Threadable buildThread(String algorithm,
			List<Threadable> threadables) {
		Threader t = ThreaderFactory.getThreader(algorithm);
		return t.thread(threadables);
	}

}
