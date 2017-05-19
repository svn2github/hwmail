package com.hs.mail.smtp.processor.hook;

import com.hs.mail.smtp.SmtpException;
import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.message.Recipient;
import com.hs.mail.smtp.message.SmtpMessage;

public class MaxRcptHook implements RcptHook {

	private int maxRcpt = 0;

	public MaxRcptHook(int maxRcpt) {
		this.maxRcpt = maxRcpt;
	}

	public void doRcpt(SmtpSession session, SmtpMessage message, Recipient rcpt) {
		int rcptCount = message.getRecipientsSize();
		if (rcptCount >= maxRcpt) {
			throw new SmtpException(SmtpException.RECIPIENTS_COUNT_LIMIT);
		}
	}

}
