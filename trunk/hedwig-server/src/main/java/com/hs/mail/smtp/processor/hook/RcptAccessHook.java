package com.hs.mail.smtp.processor.hook;

import java.io.File;
import java.io.IOException;

import com.hs.mail.exception.ConfigException;
import com.hs.mail.smtp.SmtpException;
import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.message.Recipient;
import com.hs.mail.smtp.message.SmtpMessage;

public class RcptAccessHook implements RcptHook {
	
	private AccessTable access;
	
	public RcptAccessHook(String path) throws ConfigException {
		File config = new File(path);
		if (!config.exists()) {
			throw new ConfigException("Recipient access file '" + path
					+ "' does not exist");
		}
		try {
			this.access = new AccessTable(config);
		} catch (IOException e) {
			throw new ConfigException(e);
		}
	}
	
	@Override
	public void doRcpt(SmtpSession session, SmtpMessage message, Recipient rcpt) {
		if (access.findAction(rcpt) == AccessTable.Action.REJECT) {
			throw new SmtpException(SmtpException.RECIPIENT_REJECTED);
		}
	}

}
