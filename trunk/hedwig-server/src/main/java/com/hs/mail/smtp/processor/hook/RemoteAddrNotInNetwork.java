package com.hs.mail.smtp.processor.hook;

import com.hs.mail.container.config.Config;
import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.smtp.SmtpSession;

public class RemoteAddrNotInNetwork implements ConnectHook {

	@Override
	public void onConnect(SmtpSession session, TcpTransport trans) {
		if (!Config.getAuthorizedNetworks().matches(session.getClientAddress())) {
			String ipAddress = session.getRemoteIP();
			StringBuilder response = new StringBuilder();
			response.append("550 5.7.1 ")
					.append(ipAddress)
					.append(" is blocked by system");
			
			session.writeResponse(response.toString()); 
			trans.endSession();
			return;
		}
	}

}
