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

import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Iterator;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.dom.Header;
import org.apache.james.mime4j.field.LenientFieldParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.message.MailMessage;
import com.hs.mail.imap.processor.fetch.BodyStructureBuilder;
import com.hs.mail.imap.processor.fetch.EnvelopeBuilder;
import com.hs.mail.sieve.Sieve;
import com.hs.mail.smtp.message.Recipient;
import com.hs.mail.smtp.message.SmtpMessage;

/**
 * Mailet that actually stores the message
 * 
 * @author Won Chul Doh
 * @since 29 Jun, 2010
 * 
 */
public class ToRepository extends AbstractMailet {

	static Logger logger = LoggerFactory.getLogger(ToRepository.class);
	
	private BodyStructureBuilder builder = null;
	
	public ToRepository() {
		super();
		this.builder = new BodyStructureBuilder(new EnvelopeBuilder());
	}

	public boolean accept(Set<Recipient> recipients, SmtpMessage message) {
		return CollectionUtils.isNotEmpty(recipients);
	}
	
	public void service(Set<Recipient> recipients, SmtpMessage message)
			throws MessagingException {
		try {
			deliver(recipients, message);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void deliver(Set<Recipient> recipients, SmtpMessage message)
			throws IOException {
		String returnPath = (message.getNode() != SmtpMessage.LOCAL)
				? "Return-Path: <" + message.getFrom().getMailbox() + ">\r\n"
				: null;
		MailMessage msg = message.getMailMessage();
		try {
			if (returnPath != null) {
				Header header = msg.getHeader().getHeader();
				header.setField(LenientFieldParser.parse(returnPath));
				msg.setSize(msg.getSize() + returnPath.getBytes().length);
			}
			for (Iterator<Recipient> it = recipients.iterator(); it.hasNext();) {
				Recipient rcpt = it.next();
				it.remove();
				try {
					if (rcpt.getID() != -1) {
						String destination = rcpt.getDestination();
						if (destination != null
								|| !Sieve.runSieve(context, rcpt, message)) {
							context.storeMail(rcpt.getID(), 
									StringUtils.defaultString(destination, 
											ImapConstants.INBOX_NAME), 
									message);
						}
					}
				} catch (Exception e) {
					logger.error("{} exception delivering mail ({}): {}", 
							"Permanent",
							message.getName(), 
							e.getMessage().trim());
		
					if (!message.isNotificationMessage()) {
						StringBuilder errorBuffer = new StringBuilder(256)
								.append(rcpt.getMailbox())
								.append("\r\n")
								.append("Error while storing message.")
								.append("\r\n");
						message.appendErrorMessage(errorBuffer.toString());
					}
				}
			}
		} catch (MimeException e) {
			// impossible really
		} finally {
			saveMessage(returnPath, msg);
		}
	}

	private void saveMessage(String returnPath, MailMessage msg)
			throws IOException {
		if (msg != null && msg.getPhysMessageID() != 0) {
			try {
				if (returnPath != null) {
					PushbackInputStream is = new PushbackInputStream(
							msg.getInputStream(), returnPath.length());
					is.unread(returnPath.getBytes("ASCII"));
					msg.save(is);
				} else {
					msg.save(false);
				}
				builder.build(msg.getInternalDate(), msg.getPhysMessageID());
			} catch (MimeException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
}
