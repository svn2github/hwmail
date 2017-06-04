/*
 * Copyright 2010 the original author or authors.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.hs.mail.smtp;

import java.net.InetAddress;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.hs.mail.container.config.Config;
import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.smtp.message.MailAddress;
import com.hs.mail.smtp.message.SmtpMessage;
import com.hs.mail.util.Log4jUtils;

/**
 * 
 * @author Won Chul Doh
 * @since May 30, 2010
 *
 */
public class SmtpSession {
	
	private static Logger logger = Logger.getLogger(SmtpSession.class);
	
    /**
     * Static Random instance used to generate SMTP ids
     */
    private final static Random random = new Random();

    private TcpTransport transport;
	private InetAddress clientAddress;
	private String clientDomain;
	private String protocol;
	private String smtpID;
	private long authID = -1;
	private int errorCount = 0;
	private SmtpMessage message;

	public SmtpSession(TcpTransport trans) {
		this.transport = trans;
		this.clientAddress = trans.getRemoteAddress();
		this.smtpID = random.nextInt(1024) + "";
	}
	
	public static void setLogger(String filename) {
		logger.setLevel(Level.DEBUG);
    	logger.setAdditivity(false);
		Log4jUtils.addAppender(logger, filename, "%m%n");
	}
	
	public void debug(String line) {
		if (logger.isDebugEnabled()) {
			logger.debug(line);
		}
	}

    public TcpTransport getTransport() {
		return transport;
	}
	
	public InetAddress getClientAddress() {
		return clientAddress;
	}

	public String getRemoteIP() {
		return clientAddress.getHostAddress();
	}

	public String getRemoteHost() {
		return clientAddress.getHostName();
	}

	public String getClientDomain() {
		return clientDomain;
	}

	public void setClientDomain(String domain) {
		this.clientDomain = domain;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public String getSessionID() {
		return smtpID;
	}
	
	public long getAuthID() {
		return authID;
	}

	public void setAuthID(long authID) {
		this.authID = authID;
	}

	public int incErrorCount() {
		errorCount++;
		return errorCount;
	}

	public SmtpMessage getMessage() {
		return message;
	}

	public void setMessage(SmtpMessage message) {
		this.message = message;
	}

	public void createSmtpMessage(MailAddress from) {
		this.message = new SmtpMessage(from);
	}
	
	public void writeResponse(String response) {
		if (errorCount >= Config.getSmtpdSoftErrorLimit()) {
			if (Config.getSmtpdErrorSleepTime() > 0) {
				// Slow down clients that make errors. Sleep-on-anything slows
				// down clients that make an excessive number of errors within a
				// session.
				try {
					Thread.sleep(Config.getSmtpdErrorSleepTime() * 1000);
				} catch (InterruptedException e) {
					// IGNORE
				}
			}
		}

		transport.println(response);
		debug(response);
	}
	
}
