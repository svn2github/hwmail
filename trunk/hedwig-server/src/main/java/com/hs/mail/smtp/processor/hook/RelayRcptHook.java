package com.hs.mail.smtp.processor.hook;

import org.apache.commons.lang3.ArrayUtils;

import com.hs.mail.container.config.Config;
import com.hs.mail.smtp.SmtpException;
import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.message.Recipient;
import com.hs.mail.smtp.message.SmtpMessage;

public class RelayRcptHook implements RcptHook {
	
	private boolean permit_mynetworks = true;

	private boolean permit_sasl_auth  = true;
	
	public RelayRcptHook(String[] restrictions) {
		permit_mynetworks = ArrayUtils.contains(restrictions, "permit_mynetworks");
		permit_sasl_auth  = ArrayUtils.contains(restrictions, "permit_sasl_authenticated");
	}

	public void doRcpt(SmtpSession session, SmtpMessage message, Recipient rcpt) {
		String toDomain = rcpt.getHost();
		if (!Config.isLocal(toDomain)) {
			if (!isRelayingAllowed(session)) {
				throw new SmtpException(SmtpException.RELAY_DENIED);
			}
		}
	}

	private boolean isRelayingAllowed(SmtpSession session) {
		return (permit_mynetworks && Config.getAuthorizedNetworks().matches(
				session.getClientAddress()))
				|| (permit_sasl_auth && (session.getAuthID() > 0));
	}

}
