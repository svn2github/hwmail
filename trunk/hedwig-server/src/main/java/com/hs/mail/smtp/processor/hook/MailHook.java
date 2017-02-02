package com.hs.mail.smtp.processor.hook;

import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.message.MailAddress;

public interface MailHook {

	void doMail(SmtpSession session, MailAddress sender);
	
}
