package com.hs.mail.imap.processor.ext;

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.UnsupportedRightException;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxACL.EditMode;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.DeleteACLRequest;
import com.hs.mail.imap.message.responder.Responder;

/**
 * The DELETEACL command removes any <identifier,rights> pair for the specified
 * identifier from the access control list for the specified mailbox.
 * 
 * @author Wonchul Doh
 * @since December 2, 2016
 *
 */
public class DeleteACLProcessor extends AbstractACLProcessor {

	@Override
	protected void doProcess(ImapSession session, ImapRequest message,
			Responder responder) throws Exception {
		DeleteACLRequest request = (DeleteACLRequest) message;
		try {
			Mailbox mailbox = getAuthorizedMailbox(session, request);
			long userid = getUserID(request.getIdentifier());
			getMailboxManager().setACL(userid, mailbox.getMailboxID(),
					EditMode.REPLACE, null);
			responder.okCompleted(request);
		} catch (UnsupportedRightException e) {
			responder.taggedBad(request, e.getMessage());
		}
	}

}
