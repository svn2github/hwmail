package com.hs.mail.imap.processor.ext;

import javax.security.auth.login.AccountNotFoundException;

import com.hs.mail.imap.mailbox.MailboxACL;
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
		if (MailboxACL.ANYONE.equals(identifier)) {
			return MailboxACL.ANYONE_ID;
		}

		UserManager manager = getUserManager();
		String address = manager.toAddress(identifier);
		long userid = manager.getUserID(address);
		if (userid == 0) {
			throw new AccountNotFoundException("Account for " + identifier + " not found");
		}
		return userid;
	}

}
