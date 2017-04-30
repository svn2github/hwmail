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
package com.hs.mail.mailet;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jsieve.mail.ActionReject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.container.config.Config;
import com.hs.mail.sieve.SieveMailAdapter;
import com.hs.mail.smtp.message.Recipient;
import com.hs.mail.smtp.message.SmtpMessage;
import com.hs.mail.util.FileUtils;

/**
 * Sends the message through daemonized SpamAssassin (spamd), visit
 * <a href="http://spamassassin.apache.org/">Apache SpamAssassin</a> for info on
 * configuration. The header X-Spam-Flag is added to every spam message with the
 * value of YES. The header X-Spam-Status is also added, this header contains
 * the score and the threshold score for spam (usually 5.0).
 * 
 * 
 * @author Wonchul Doh
 * @since 26 April, 2017
 *
 */
public class SpamAssassin extends AbstractMailet {

	static Logger logger = LoggerFactory.getLogger(SpamAssassin.class);
	
	private String spamdHost;	// The host for spamd

	private int spamdPort;	// The port number of spamd
	
	private String action;	// Action for SPAM message
	
	// Argument for SPAM action, message for reject action or destination folder
	// for fileinto action
	private String argument; 
	
	public void init(MailetContext context) {
		super.init(context);
		this.spamdHost = Config.getProperty("spamd_host", "127.0.0.1");
		this.spamdPort = (int) Config.getNumberProperty("spamd_port", 783);
		
		String[] tokens = StringUtils.split(Config.getProperty("spamd_action", null), null, 2);
		if (ArrayUtils.isNotEmpty(tokens)) {
			this.action = tokens[0];
			if (tokens.length > 1) {
				this.argument = StringUtils.strip(tokens[1], "\"");
			}
		}
	}

	public boolean accept(Set<Recipient> recipients, SmtpMessage message) {
		return (CollectionUtils.isNotEmpty(recipients)
				&& !message.isNotificationMessage());
	}

	public void service(Set<Recipient> recipients, SmtpMessage message)
			throws MessagingException {
		try {
			// Invoke spamassian connection and scan the message
			SpamAssassinInvoker sa = new SpamAssassinInvoker(spamdHost, spamdPort);

			if (!sa.scanMail(message)) {
				// Message is not a SPAM
				return;
			}
			
			if (Action.discard.name().equals(action)) {
				recipients.clear();
				return;
			}
			
			if (Action.reject.name().equals(action)) {
				recipients.clear();
				SieveMailAdapter adapter = new SieveMailAdapter(context, null, -1L);
				adapter.setMessage(message);
				adapter.addAction(new ActionReject(StringUtils.defaultString(
						argument, "Email rejected. Looks like spam.")));
				adapter.executeActions();
				return;
			}
			
			if (Action.fileinto.name().equals(action)) {
				String destination = StringUtils.defaultString(argument, "Junk");
				for (Iterator<Recipient> it = recipients.iterator(); it.hasNext();) {
					Recipient rcpt = it.next();
					rcpt.setDestination(destination);
				}
			}
			
			Iterator<String> headers = sa.getHeadersAsAttribute().keySet().iterator();
			StringBuilder buff = new StringBuilder();
			
			// Add headers to message
			while (headers.hasNext()) {
				String key = headers.next();
				buff.append(key)
						.append(": ")
						.append((String) sa.getHeadersAsAttribute().get(key))
						.append("\r\n");
			}

			File file = message.getDataFile(); 
			FileUtils.prependToFile(buff.toString(), file);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	enum Action {
		discard, reject, fileinto
	}
	
}
