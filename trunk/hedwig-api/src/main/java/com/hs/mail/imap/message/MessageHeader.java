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
import org.apache.james.mime4j.util.ByteSequence;
import org.apache.james.mime4j.util.CharsetUtil;
import org.apache.james.mime4j.util.MimeUtil;

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

		String value = getNonAsciiValue(field);
		return (value != null) ? value : field.getValue();
	}

    public Date getDate() {
		DateTimeField field = obtainField(FieldName.DATE);
		if (field == null)
			return null;

		return field.getDate();
	}

	public String getFrom() {
		MailboxListField field = obtainField(FieldName.FROM);
		if (field == null)
			return null;

		String value = getNonAsciiValue(field);
		if (value == null)
			value = DecoderUtil.decodeEncodedWords(field.getBody(),
					DecodeMonitor.SILENT);
		
		return unquoteName(value);
	}
	
	public String getFromAddress() {
        MailboxListField field = obtainField(FieldName.FROM);
        if (field == null)
            return null;

        MailboxList mailboxList = field.getMailboxList();
		return CollectionUtils.isEmpty(mailboxList)
			? field.getBody()
			: mailboxList.get(0).getAddress();
	}
	
    public AddressList getAddressList(String fieldName) {
		AddressListField field = obtainField(fieldName);
		if (field == null)
			return null;

		return field.getAddressList();
	}
    
	private static String getNonAsciiValue(Field field) {
		ByteSequence raw = field.getRaw();
		if (raw == null || raw.length() == 0) {
			return null;
		}

		if (indexOf(raw, "=?") == -1) {
			// Raw text is not "encoded-word".
			int len = raw.length();
			int off = field.getName().length() + 1;
			if (len > off + 1
					&& (CharsetUtil.isWhitespace((char) raw.byteAt(off)))) {
				// Skip white spaces
				off++;
			}
			for (int i = off; i < len; i++) {
				if (!CharsetUtil.isASCII((char) raw.byteAt(i))) {
					// Non-ascii text found, treat this text "as is".
					byte[] b = new byte[len - off];  
					for (int j = 0; j < b.length; j++) {
						b[j] = raw.byteAt(off + j);
					}
					return MimeUtil.unfold(new String(b));
				}
			}
		}
		return null;
	}	
	
	private static int indexOf(ByteSequence bs, String str) {
		int targetLength = str.length();
		int max = bs.length() - targetLength;
		char first = str.charAt(0);
		for (int i = 0; i < max; i++) {
			// Loook for the first character
			if ((char) bs.byteAt(i) != first) {
				while (++i < max && ((char) bs.byteAt(i) != first));
			}
			// Found the first character, now look at the rest of v2
			if (i < max) {
				int j = i + 1;
				int end = j + targetLength - 1;
				for (int k = 1; j < end
						&& ((char) bs.byteAt(j) == str.charAt(k)); j++, k++);

				if (j == end) {
					// Found whole string
					return i;
				}
			}
		}
		return -1;
	}
	
	private static String unquoteName(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		
		int pos = 0;
		char current = str.charAt(pos);
		if (current != '\"') {
			return str;
		}

		StringBuilder dst = new StringBuilder();
		pos++;
		int len = str.length();
		boolean escaped = false;
		while (pos < len) {
			current = str.charAt(pos++);
			if (escaped) {
				if (current != '\"' && current != '\\') {
					dst.append('\\');
				}
				dst.append(current);
				escaped = false;
			} else {
				if (current == '\"') {
					break;
				}
				if (current == '\\') {
					escaped = true;
				} else if (current != '\r' && current != '\n') {
					dst.append(current);
				}
			}
		}
		
		while (pos < len) {
			dst.append(str.charAt(pos++));
		}

		return dst.toString();
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
