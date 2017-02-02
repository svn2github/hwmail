package com.hs.mail.smtp.processor.hook;

import java.io.File;
import java.io.IOException;

import com.hs.mail.exception.ConfigException;
import com.hs.mail.smtp.SmtpException;
import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.message.MailAddress;

public class MailAccessHook implements MailHook {

	private AccessTable access;

	public MailAccessHook(String path) throws ConfigException {
		File config = new File(path);
		if (!config.exists()) {
			throw new ConfigException("Sender access file '" + path
					+ "' does not exist");
		}
		try {
			this.access = new AccessTable(config);
		} catch (IOException e) {
			throw new ConfigException(e);
		}
	}

	@Override
	public void doMail(SmtpSession session, MailAddress sender) {
		if (access.isRestricted(sender)) {
			throw new SmtpException(SmtpException.SENDER_REJECTED);
		}
	}

}
