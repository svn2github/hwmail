package com.hs.mail.smtp.processor.hook;

import java.io.File;
import java.io.IOException;

import com.hs.mail.exception.ConfigException;
import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.message.Recipient;
import com.hs.mail.smtp.message.SmtpMessage;
import com.hs.mail.util.FileUtils;

public class DNSRBLHook extends RcptHook {
	
	/**
	 * The list of rbl servers to be checked to limit spam 
	 */
	private String[] blacklist;
	
	public static DNSRBLHook create(String path) throws ConfigException {
		File config = new File(path);
		if (!config.exists()) {
			throw new ConfigException(
					"Recipient access file '" + path + "' does not exist");
		}
		try {
			DNSRBLHook hook = new DNSRBLHook();
			hook.blacklist = FileUtils.readLines(config);
			return hook;
		} catch (IOException e) {
			throw new ConfigException(e);
		}
	}

	@Override
	public void doRcpt(SmtpSession session, SmtpMessage message,
			Recipient rcpt) {
		// TODO Auto-generated method stub

	}

}
