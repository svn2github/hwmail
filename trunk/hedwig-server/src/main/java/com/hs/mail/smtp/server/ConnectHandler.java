package com.hs.mail.smtp.server;

import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.smtp.SmtpSession;

public interface ConnectHandler {

	void onConnect(SmtpSession session, TcpTransport trans);

}
