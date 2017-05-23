package com.hs.mail.smtp.processor.hook;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.container.config.Config;
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

	public void onConnect(SmtpSession session, TcpTransport trans) {
		if (Config.getAuthorizedNetworks().matches(session.getClientAddress())) {
			return;
		}

		if (blacklist != null) {
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
					response.append("550 5.7.1 ")
							.append(ipAddress)
							.append(" is blocked by RBL at ")
							.append(rbl);
					
					session.writeResponse(response.toString()); 
					trans.endSession();
					return;
				}
			}
		}
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
