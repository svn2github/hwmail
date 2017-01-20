package com.hs.mail.smtp.processor.hook;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.hs.mail.imap.user.Alias;
import com.hs.mail.imap.user.User;
import com.hs.mail.smtp.SmtpException;
import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.message.Recipient;
import com.hs.mail.smtp.message.SmtpMessage;

public class ValidRcptHook extends RcptHook {

	private boolean isValidRecipient(Recipient rcpt) {
		User user = getUserManager().getUserByAddress(rcpt.getMailbox());
		if (user != null) {
			if (StringUtils.isEmpty(user.getForwardTo())) {
				rcpt.setID(user.getID());
			}
		} else {
			List<Alias> expanded = getUserManager().expandAlias(rcpt.getMailbox());
			if (CollectionUtils.isEmpty(expanded)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void doRcpt(SmtpSession session, SmtpMessage message, Recipient rcpt) {
		if (!isValidRecipient(rcpt)) {
			throw new SmtpException(SmtpException.NO_SUCH_USER);
		}
	}
	
}
