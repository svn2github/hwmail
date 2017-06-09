package com.hs.mail.smtp.processor.hook;

import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.smtp.SmtpSession;

public class RejectHook implements ConnectHook {

	@Override
	public HookResult onConnect(SmtpSession session, TcpTransport trans) {
		StringBuilder response = new StringBuilder();
		response.append("550 5.7.1 ")
				.append(session.getRemoteIP());
		return HookResult.reject(response.toString());
	}

}
