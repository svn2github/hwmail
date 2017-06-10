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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.smtp.SmtpSession;

public class DNSRBLHandler implements ConnectHook {

	private static Logger logger = LoggerFactory.getLogger(DNSRBLHandler.class);

	/**
	 * The list of RBL servers to be checked to limit spam
	 */
	private String[] blacklist;

	public DNSRBLHandler(String[] blacklist) {
		this.blacklist = blacklist;
	}
	
	public HookResult onConnect(SmtpSession session, TcpTransport trans) {
		if (ArrayUtils.isNotEmpty(blacklist)) {
			String ipAddress = session.getRemoteIP();
            StringBuffer sb = new StringBuffer();
            StringTokenizer st = new StringTokenizer(ipAddress, " .", false);
            while (st.hasMoreTokens()) {
                sb.insert(0, st.nextToken() + ".");
            }
            String reversedOctets = sb.toString();
			
			for (String rbl : blacklist) {
				if (resolve(reversedOctets + rbl)) {
					// was found in the RBL
					logger.info("Connection from {} is restricted by {}", ipAddress, rbl);
					
					StringBuilder response = new StringBuilder();
					response.append("554 5.7.1 Service unavailable; Client host[")
							.append(ipAddress)
							.append("] blocked using ")
							.append(rbl);
					
					return HookResult.reject(response.toString());
				}
			}
		}
		return HookResult.DUNNO;
	}

	protected boolean resolve(String ip) {
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
	}
	
}
