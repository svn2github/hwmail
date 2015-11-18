package com.hs.mail.imap.processor.custom;

import java.util.Map;
import java.util.Map.Entry;

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.mailbox.SelectedMailbox;
import com.hs.mail.imap.mailbox.UidToMsnMapper;
import com.hs.mail.imap.message.FetchData;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.custom.XRevokeRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.response.HumanReadableText;
import com.hs.mail.imap.message.response.custom.XRevokeResponseBuilder;
import com.hs.mail.imap.processor.AbstractImapProcessor;
import com.hs.mail.imap.user.UserManager;

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
		UserManager usermgr = getUserManager();
		long sequenceNumber = request.getSequenceNumber();
		long uid = map.getUID((int) map.getMessageNumber(sequenceNumber));
		if (uid != -1) {
			FetchData fd = manager.getMessageFetchData(uid);
			if (fd != null) {
				XRevokeResponseBuilder builder = new XRevokeResponseBuilder(
						manager, usermgr, fd.getPhysMessageID());
				if (session.getUserID() != builder.getSenderID()) {
					responder.taggedNo(request, HumanReadableText.PERMISSION_DENIED);
				} else {
					Map<String, String> responses = 
							builder.build(uid, request.getFlag(), request.getRecipients());
					for (Entry<String, String> entry : responses.entrySet()) {
						response(responder, request.getCommand(),
								entry.getValue(), entry.getKey());
					}
					responder.okCompleted(request);
				}
				return;
			}
		}
		responder.taggedNo(request, HumanReadableText.MESSAGE_NOT_FOUND);
	}
	
	private void response(Responder responder, String command,
			String responseCode, String recipient) {
		responder.untagged(new StringBuilder(command)
				.append(' ')
				.append(responseCode)
				.append(' ')
				.append(recipient)
				.append("\r\n")
				.toString());
	}

}
