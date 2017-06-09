package com.hs.mail.smtp.processor.hook;

import com.hs.mail.container.config.Config;
import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.smtp.SmtpSession;

public class RemoteAddrInNetwork implements ConnectHook {

	@Override
	public HookResult onConnect(SmtpSession session, TcpTransport trans) {
		return Config.getAuthorizedNetworks().matches(
				session.getClientAddress()) ? HookResult.OK : HookResult.DUNNO;
	}

}
