package com.hs.mail.smtp.processor.hook;

import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.message.Recipient;
import com.hs.mail.smtp.message.SmtpMessage;

public abstract class RcptHook extends AbstractHook {

	abstract public void doRcpt(SmtpSession session, SmtpMessage message, Recipient rcpt);
	
}
