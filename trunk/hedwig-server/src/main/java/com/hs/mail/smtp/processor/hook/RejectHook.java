/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.hs.mail.smtp.processor.hook;

import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.smtp.SmtpSession;
import com.hs.mail.smtp.message.Recipient;
import com.hs.mail.smtp.message.SmtpMessage;

public class RejectHook implements ConnectHook, RcptHook {
	
	private boolean relay = false;
	
	public RejectHook(boolean relay) {
		this.relay = relay;
	}

	@Override
	public HookResult onConnect(SmtpSession session, TcpTransport trans) {
		StringBuilder response = new StringBuilder();
		response.append("554 5.7.1 Service unavailable; Client host[")
				.append(session.getRemoteIP()).append("] blocked by system");
		return HookResult.reject(response.toString());
	}

	@Override
	public HookResult doRcpt(SmtpSession session, SmtpMessage message,
			Recipient rcpt) {
		StringBuilder response = new StringBuilder();
		response.append("554 5.7.1 <").append(rcpt.getMailbox())
				.append(">: Recipient address rejected");
		if (relay)
			response.append("; Relay access denied");
		return HookResult.reject(response.toString());
	}

}
