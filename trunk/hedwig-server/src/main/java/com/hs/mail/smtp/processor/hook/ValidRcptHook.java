package com.hs.mail.smtp.processor.hook;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.hs.mail.container.config.ComponentManager;
import com.hs.mail.container.config.Config;
import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.user.Alias;
import com.hs.mail.imap.user.User;
import com.hs.mail.imap.user.UserManager;
import com.hs.mail.smtp.SmtpException;
import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.message.Recipient;
import com.hs.mail.smtp.message.SmtpMessage;

public class ValidRcptHook implements RcptHook {

	private boolean isValidRecipient(SmtpSession session, Recipient rcpt) {
		User user = getUserManager().getUserByAddress(rcpt.getMailbox());
		if (user != null) {
			if (StringUtils.isEmpty(user.getForwardTo())) {
				rcpt.setID(user.getID());
			}
		} else {
			List<Alias> expanded = getUserManager().expandAlias(rcpt.getMailbox());
			if (CollectionUtils.isNotEmpty(expanded)) {
				for (Alias alias : expanded) {
					if (alias.getDeliverTo().startsWith(ImapConstants.NAMESPACE_PREFIX)) {
						// Aliased mailbox is a public folder.
						if (session.getAuthID() < 0
								|| !getMailboxManager().hasRight(
										session.getAuthID(),
										alias.getDeliverTo(),
										MailboxACL.p_Post_RIGHT)) {
							// User does not have right to post to the folder.
							throw new SmtpException(SmtpException.RECIPIENT_REJECTED);
						}
					}
				}
			} else {
				// User and alias does not exist
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void doRcpt(SmtpSession session, SmtpMessage message, Recipient rcpt) {
		if (Config.isLocal(rcpt.getHost())) {
			if (!isValidRecipient(session, rcpt)) {
				throw new SmtpException(SmtpException.NO_SUCH_USER);
			}
		}
	}

	private UserManager getUserManager() {
		return (UserManager) ComponentManager.getBean("userManager");
	}

	private MailboxManager getMailboxManager() {
		return (MailboxManager) ComponentManager.getBean("mailboxManager");
	}
	
}
