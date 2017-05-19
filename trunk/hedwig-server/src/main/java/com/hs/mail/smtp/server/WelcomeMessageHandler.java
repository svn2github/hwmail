package com.hs.mail.smtp.server;

import com.hs.mail.container.config.Config;
import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.smtp.SmtpSession;

public class WelcomeMessageHandler implements ConnectHandler {

	public void onConnect(SmtpSession session, TcpTransport trans) {
		String greetings = new StringBuilder()
				.append("220 ")
				.append(Config.getHelloName())
				.append(" Service ready")
				.toString();
		session.writeResponse(greetings);
	}

}
