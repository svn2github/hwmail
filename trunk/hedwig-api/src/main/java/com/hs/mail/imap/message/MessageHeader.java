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
package com.hs.mail.imap.message;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.codec.DecoderUtil;
import org.apache.james.mime4j.dom.Header;
import org.apache.james.mime4j.dom.address.AddressList;
import org.apache.james.mime4j.dom.address.Mailbox;
import org.apache.james.mime4j.dom.address.MailboxList;
import org.apache.james.mime4j.dom.field.AddressListField;
import org.apache.james.mime4j.dom.field.DateTimeField;
import org.apache.james.mime4j.dom.field.FieldName;
import org.apache.james.mime4j.dom.field.MailboxListField;
import org.apache.james.mime4j.dom.field.ParsedField;
import org.apache.james.mime4j.dom.field.UnstructuredField;
import org.apache.james.mime4j.field.AbstractField;
import org.apache.james.mime4j.field.LenientFieldParser;
import org.apache.james.mime4j.io.MaxHeaderLimitException;
import org.apache.james.mime4j.message.HeaderImpl;
import org.apache.james.mime4j.parser.AbstractContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.Field;
import org.apache.james.mime4j.stream.MimeConfig;

/**
 * 
 * @author Won Chul Doh
 * @since Mar 8, 2010
 *
 */
public class MessageHeader {
	private Header header = new HeaderImpl();

	public MessageHeader(InputStream is) throws MimeIOException, IOException {
		final MimeStreamParser parser = createMimeParser();
		parser.setContentHandler(new AbstractContentHandler() {
			@Override
			public void endHeader() throws MimeException {
				parser.stop();
			}
			@Override
			public void field(Field field) throws MimeException {
				ParsedField parsedField = LenientFieldParser
						.parse(field.getRaw(), DecodeMonitor.SILENT);
				header.addField(parsedField);
			}
		});
		try {
			parser.parse(is);
		} catch (MaxHeaderLimitException ex) {
			// Ignore this exception
		} catch (MimeException ex) {
			throw new MimeIOException(ex);
		}
	}
	
	private static MimeStreamParser createMimeParser() {
		MimeConfig config = MimeConfig.custom().setMaxLineLen(-1).build();
		return new MimeStreamParser(config);
	}

	public Header getHeader() {
		return header;
	}

	public List<String> getValues(String fieldName) {
		List<AbstractField> fields = obtainFields(fieldName);
		List<String> results = new ArrayList<String>(fields.size());
		for (AbstractField field : fields) {
			if (field instanceof UnstructuredField)
				results.add(((UnstructuredField) field).getValue());
			else
				results.add(DecoderUtil.decodeEncodedWords(field.getBody(),
						DecodeMonitor.SILENT));
		}
		return results;
	}

    public String getSubject() {
		UnstructuredField field = obtainField(FieldName.SUBJECT);
		if (field == null)
			return null;

		return field.getValue();
	}

    public Date getDate() {
		DateTimeField dateField = obtainField(FieldName.DATE);
		if (dateField == null)
			return null;

		return dateField.getDate();
	}

	public Mailbox getFrom() {
		MailboxList mailboxList = getMailboxList(FieldName.FROM);
		if (CollectionUtils.isEmpty(mailboxList)) {
			Field field = header.getField(FieldName.FROM);
			return (field != null) ? new Mailbox(field.getBody(), null) : null;
		} else {
			return mailboxList.get(0);
		}
	}
	
	public Mailbox getReplyTo() {
		MailboxList mailboxList = getMailboxList(FieldName.REPLY_TO);
		if (CollectionUtils.isEmpty(mailboxList)) {
			return null;
		} else {
			return mailboxList.get(0);
		}
	}

    public AddressList getTo() {
		return getAddressList(FieldName.TO);
	}

    public AddressList getCc() {
        return getAddressList(FieldName.CC);
    }

    public AddressList getBcc() {
        return getAddressList(FieldName.BCC);
    }

    public AddressList getAddressList(String fieldName) {
		AddressListField field = obtainField(fieldName);
		if (field == null)
			return null;

		return field.getAddressList();
	}
    
    private MailboxList getMailboxList(String fieldName) {
        MailboxListField field = obtainField(fieldName);
        if (field == null)
            return null;

        return field.getMailboxList();
    }

	<F extends Field> F obtainField(String fieldName) {
		if (header == null)
			return null;

		@SuppressWarnings("unchecked")
		F field = (F) header.getField(fieldName);
		return field;
	}

	<F extends Field> List<F> obtainFields(String fieldName) {
		if (header == null)
			return null;

		@SuppressWarnings("unchecked")
		List<F> fields = (List<F>) header.getFields(fieldName);
		return fields;
	}

}
