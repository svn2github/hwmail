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
import com.hs.mail.imap.message.responder.ext.thread.ThreadResponder;
import com.hs.mail.imap.message.thread.Threadable;
import com.hs.mail.imap.processor.AbstractImapProcessor;

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
		List<Threadable> results = manager.searchThread(map,
				selected.getMailboxID(), request.getSearchKey());
		if (CollectionUtils.isNotEmpty(results)) {
			if (!request.isUseUID()) {
				getMessageNumbers(map, results);
			}
			Threadable thread = buildThread(results);
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
	
	private Threadable buildThread(List<Threadable> threadables) {
		Threadable first = null, last = null;
		for (Threadable t : threadables) {
			if (first == null) {
				first = t;
			} else {
				last.setNext(t);
			}
			last = t;
		}
		last = null;
		return new Threader().thread(first);
	}

}
