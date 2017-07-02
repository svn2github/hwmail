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
package com.hs.mail.imap.processor.fetch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.james.mime4j.codec.EncoderUtil;
import org.apache.james.mime4j.dom.address.AddressList;
import org.apache.james.mime4j.dom.address.DomainList;
import org.apache.james.mime4j.dom.address.Group;
import org.apache.james.mime4j.dom.address.Mailbox;
import org.apache.james.mime4j.dom.address.MailboxList;
import org.apache.james.mime4j.field.address.LenientAddressParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.imap.ImapConstants;

/**
 * 
 * @author Won Chul Doh
 * @since Mar 8, 2010
 *
 */
public class EnvelopeBuilder {
	
	private static Logger logger = LoggerFactory.getLogger(EnvelopeBuilder.class);

	public static final String[] WANTED_FIELDS = new String[] {
			ImapConstants.RFC822_DATE, ImapConstants.RFC822_SUBJECT,
			ImapConstants.RFC822_FROM, ImapConstants.RFC822_SENDER,
			ImapConstants.RFC822_REPLY_TO, ImapConstants.RFC822_TO,
			ImapConstants.RFC822_CC, ImapConstants.RFC822_BCC,
			ImapConstants.RFC822_IN_REPLY_TO, ImapConstants.RFC822_MESSAGE_ID };
	
	public Envelope build(Map<String, String> header) {
		final String date = header.get(ImapConstants.RFC822_DATE);
		final String subject = header.get(ImapConstants.RFC822_SUBJECT);
		final Address[] fromAddresses = buildAddresses(header.get(ImapConstants.RFC822_FROM));
		final Address[] senderAddresses = buildAddresses(header.get(ImapConstants.RFC822_SENDER), fromAddresses);
		final Address[] replyToAddresses = buildAddresses(header.get(ImapConstants.RFC822_REPLY_TO), fromAddresses);
		final Address[] toAddresses = buildAddresses(header.get(ImapConstants.RFC822_TO));
		final Address[] ccAddresses = buildAddresses(header.get(ImapConstants.RFC822_CC));
		final Address[] bccAddresses = buildAddresses(header.get(ImapConstants.RFC822_BCC)); 
		final String inReplyTo = header.get(ImapConstants.RFC822_IN_REPLY_TO);
		final String messageId = header.get(ImapConstants.RFC822_MESSAGE_ID);
		Envelope envelope = new Envelope(date, subject, fromAddresses, 
						senderAddresses, replyToAddresses, toAddresses, ccAddresses, 
						bccAddresses, inReplyTo, messageId);
		return envelope;
	}

	private Address[] buildAddresses(String value, Address[] defaults) {
		Address[] addresses = buildAddresses(value);
		return addresses == null ? defaults : addresses;
	}
	
	private Address[] buildAddresses(String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		} else {
			AddressList addressList = LenientAddressParser.DEFAULT.parseAddressList(value);
			final int size = addressList.size();
			final List<Address> addresses = new ArrayList<Address>(size);
			for (org.apache.james.mime4j.dom.address.Address address : addressList) {
				if (address instanceof Group) {
					addAddresses((Group) address, addresses);
				} else if (address instanceof Mailbox) {
					Address mailboxAddress = buildAddress((Mailbox) address);
					addresses.add(mailboxAddress);
				} else {
					logger.warn("Unknown address type");
				}
			}
			return addresses.toArray(Address.EMPTY);
		}
	}
	
	private void addAddresses(Group group, List<Address> addresses) {
		String groupName = group.getName();
		// Start group
		addresses.add(new Address(null, null, groupName, null));
		MailboxList mailboxList = group.getMailboxes();
		for (int i = 0; i < mailboxList.size(); i++) {
			Address mailboxAddress = buildAddress(mailboxList.get(i));
			addresses.add(mailboxAddress);
		}
		// End group
		addresses.add(new Address(null, null, null, null));
	}

	private Address buildAddress(Mailbox mailbox) {
		// JavaMail raises exception when personal name is surrounded with
		// double quotation mark.
		String name = StringUtils.strip(mailbox.getName());
		if (name != null) {
			// Encode the mailbox name
			name = EncoderUtil.encodeAddressDisplayName(name);
		}
		String domain = mailbox.getDomain();
		DomainList route = mailbox.getRoute();
		String atDomainList;
		if (CollectionUtils.isEmpty(route)) {
			atDomainList = null;
		} else {
			atDomainList = route.toRouteString();
		}
		String localPart = mailbox.getLocalPart();
		return new Address(atDomainList, domain, localPart, name);
	}

}
