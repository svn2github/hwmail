package com.hs.mail.imap.processor.ext;

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.UnsupportedRightException;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.SetACLRequest;
import com.hs.mail.imap.message.responder.Responder;

/**
 * The SETACL command changes the access control list on the specified mailbox
 * so that the specified identifier is granted permissions as specified in the
 * third argument.
 * 
 * @author Wonchul Doh
 * @since December 2, 2016
 *
 */
public class SetACLProcessor extends AbstractACLProcessor {

	@Override
	protected void doProcess(ImapSession session, ImapRequest message,
			Responder responder) throws Exception {
		SetACLRequest request = (SetACLRequest) message;

		String rights = request.getRights();
		MailboxACL.EditMode editMode = MailboxACL.EditMode.REPLACE;
		switch (rights.charAt(0)) {
		case '+':
			editMode = MailboxACL.EditMode.ADD;
			rights = rights.substring(1);
			break;
		case '-':
			editMode = MailboxACL.EditMode.DELETE;
			rights = rights.substring(1);
			break;
		}

		try {
			Mailbox mailbox = getAuthorizedMailbox(session, request);
			long userid = getUserID(request.getIdentifier());
			getMailboxManager().setACL(userid, mailbox.getMailboxID(),
					editMode, rights);
			responder.okCompleted(request);
		} catch (UnsupportedRightException e) {
			responder.taggedBad(request, e.getMessage());
		}
	}

}
