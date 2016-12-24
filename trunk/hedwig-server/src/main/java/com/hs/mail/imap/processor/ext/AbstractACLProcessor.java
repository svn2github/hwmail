package com.hs.mail.imap.processor.ext;

import javax.security.auth.login.AccountNotFoundException;

import com.hs.mail.exception.MailboxException;
import com.hs.mail.exception.MailboxNotFoundException;
import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.message.request.AbstractMailboxRequest;
import com.hs.mail.imap.message.response.HumanReadableText;
import com.hs.mail.imap.processor.AbstractImapProcessor;
import com.hs.mail.imap.user.UserManager;

/**
 * 
 * @author Wonchul Doh
 * @since December 4, 2016
 *
 */
public abstract class AbstractACLProcessor extends AbstractImapProcessor {
	
	protected static final String ACL_ANYONE_RIGHTS = "acl_anyone_rights";

	protected static final String ACL_OWNER_RIGHTS  = "acl_owner_rights";

	protected static final String ACL_OTHER_RIGHTS  = "acl_other_rights";

	protected long getUserID(String identifier) throws AccountNotFoundException {
		if (ImapConstants.ANYONE.equals(identifier)) {
			return ImapConstants.ANYONE_ID;
		}

		UserManager manager = getUserManager();
		String address = manager.toAddress(identifier);
		long userid = manager.getUserID(address);
		if (userid == 0) {
			throw new AccountNotFoundException("Account for " + identifier + " not found");
		}
		return userid;
	}
	
	protected Mailbox getAuthorizedMailbox(ImapSession session,
			AbstractMailboxRequest request) throws MailboxException {
		MailboxManager mailboxManager = getMailboxManager();
		Mailbox mailbox = mailboxManager.getMailbox(session.getUserID(),
				request.getMailbox());
		if (mailbox == null) {
			throw new MailboxNotFoundException(
					HumanReadableText.MAILBOX_NOT_FOUND);
		}

		String rights = mailboxManager.getRights(session.getUserID(),
				mailbox.getMailboxID());
		/*
		 * RFC 4314 section 6.
		 * An implementation MUST make sure the ACL commands themselves do
		 * not give information about mailboxes with appropriately
		 * restricted ACLs.
		 */
		if (rights.indexOf('l') == -1) {
			throw new MailboxNotFoundException(
					HumanReadableText.MAILBOX_NOT_FOUND);
		} 
		/*
		 * RFC 4314 section 4.
		 * Rights required to perform SETACL/DEETEACL/GETACL/LISTRIGHTS 
		 * commands.
		 */
		else if (rights.indexOf('a') == -1) {
			throw new MailboxException(HumanReadableText.INSUFFICIENT_RIGHTS);
		}
		return mailbox;
	}

}
