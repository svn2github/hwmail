package com.hs.mail.imap.processor.ext;

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.SortRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.processor.AbstractImapProcessor;

public class SortProcessor extends AbstractImapProcessor {

	@Override
	protected void doProcess(ImapSession session, ImapRequest message,
			Responder responder) throws Exception {
		SortRequest request = (SortRequest) message;
		// TODO Auto-generated method stub
		throw new Exception("BAD Unknown command");
	}

}
