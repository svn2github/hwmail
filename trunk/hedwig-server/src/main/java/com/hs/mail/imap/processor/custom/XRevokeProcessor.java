package com.hs.mail.imap.processor.custom;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.mailbox.SelectedMailbox;
import com.hs.mail.imap.mailbox.UidToMsnMapper;
import com.hs.mail.imap.message.SequenceRange;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.custom.XRevokeRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.processor.AbstractImapProcessor;

/**
 * 
 * @author Won Chul Doh
 * @since Aug 15, 2011
 *
 */
public class XRevokeProcessor extends AbstractImapProcessor {

	@Override
	protected void doProcess(ImapSession session, ImapRequest message,
			Responder responder) throws Exception {
		XRevokeRequest request = (XRevokeRequest) message;
		SelectedMailbox selected = session.getSelectedMailbox();
		UidToMsnMapper map = new UidToMsnMapper(selected, request.isUseUID());
		MailboxManager manager = getMailboxManager();
		SequenceRange[] sequenceSet = request.getSequenceSet();
		for (int i = 0; i < sequenceSet.length; i++) {
			long min = map.getMinMessageNumber(sequenceSet[i].getMin());
			long max = map.getMaxMessageNumber(sequenceSet[i].getMax());
			for (long j = min; j <= max && j >= 0; j++) {
				long uid = map.getUID((int) j);
				if (uid != -1) {
					List<Long> msgids = manager.getSiblingMessageIDList(uid);
					if (CollectionUtils.isNotEmpty(msgids)) {
						for (Long msgid : msgids) {
							if (uid != msgid) {
								manager.deleteMessage(msgid);
							}
						}
					}
				} else {
					break; // Out of index
				}
			}
		}
		responder.okCompleted(request);
	}

}
