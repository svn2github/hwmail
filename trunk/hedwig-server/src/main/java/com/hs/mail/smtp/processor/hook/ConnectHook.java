package com.hs.mail.smtp.processor.hook;

import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.smtp.SmtpSession;

public interface ConnectHook {

	HookResult onConnect(SmtpSession session, TcpTransport trans);

}
