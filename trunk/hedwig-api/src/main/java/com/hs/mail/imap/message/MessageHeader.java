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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.codec.DecoderUtil;
import org.apache.james.mime4j.codec.EncoderUtil;
import org.apache.james.mime4j.codec.EncoderUtil.Usage;
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
import org.apache.james.mime4j.stream.RawField;
import org.apache.james.mime4j.util.ByteSequence;
import org.apache.james.mime4j.util.CharsetUtil;

import com.hs.mail.util.FileUtils;

/**
 * 
 * @author Won Chul Doh
 * @since Mar 8, 2010
 *
 */
public class MessageHeader {
	
	private static String[] ADDRESS_FIELDS = { 
			FieldName.FROM.toLowerCase(),
			FieldName.RESENT_FROM.toLowerCase(),
			FieldName.SENDER.toLowerCase(),
			FieldName.RESENT_SENDER.toLowerCase(), 
			FieldName.TO.toLowerCase(),
			FieldName.RESENT_TO.toLowerCase(), 
			FieldName.CC.toLowerCase(),
			FieldName.RESENT_CC.toLowerCase(), 
			FieldName.BCC.toLowerCase(),
			FieldName.RESENT_BCC.toLowerCase(),
			FieldName.REPLY_TO.toLowerCase() 
	};
	
	private Header header = new HeaderImpl();
	private String charset = null;
	private File source = null;

	public MessageHeader(File source) throws IOException {
		this.source = source;
		InputStream input = null;
		try {
			input = new FileInputStream(source);
			parse(input);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
	
	private void parse(InputStream is) throws IOException {
		final MimeStreamParser parser = createMimeParser();
		parser.setContentHandler(new AbstractContentHandler() {
			@Override
			public void endHeader() throws MimeException {
				parser.stop();
			}
			@Override
			public void field(Field field) throws MimeException {
				ParsedField parsedField = parseField(field);
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
		DateTimeField field = obtainField(FieldName.DATE);
		if (field == null)
			return null;

		return field.getDate();
	}

	public String getFrom() {
		MailboxListField field = obtainField(FieldName.FROM);
		if (field == null)
			return null;

		String value = DecoderUtil.decodeEncodedWords(field.getBody(),
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
    
	private static int indexOf(ByteSequence bs, String str) {
		int targetLength = str.length();
		int max = bs.length() - targetLength;
		char first = str.charAt(0);
		for (int i = 0; i < max; i++) {
			// Look for the first character
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

	/*
	 * Some poor mail client creates messages with NON-ASCII headers. 
	 * Mime4j cannot parse these messages correctly.
	 * Encode field body if the field contains NON-ASCII character.
	 */
	ParsedField parseField(Field field) throws MimeException {
		ByteSequence raw = field.getRaw();
		if (raw != null && raw.length() > 0) {
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
						// Non-ASCII text found, treat this text "as is".
						byte[] b = new byte[len - off];  
						for (int j = 0; j < b.length; j++) {
							b[j] = raw.byteAt(off + j);
						}
						if (charset == null) {
							charset = FileUtils.guessEncoding(source);
						}
						String rawStr = null;
						try {
							rawStr = new String(b, charset);
						} catch (UnsupportedEncodingException ex) {
							rawStr = new String(b);
						}
						if (ArrayUtils.contains(ADDRESS_FIELDS, field.getName().toLowerCase())) {
							try {
								InternetAddress[] addresslist = InternetAddress.parse(rawStr, false);
								for (int k = 0; k < addresslist.length; k++) {
									String pers = addresslist[k].getPersonal();
									if (pers != null
											&& !CharsetUtil.isASCII(pers)) {
										addresslist[k] = new InternetAddress(addresslist[k].getAddress(), pers, "UTF-8");
									}
								}
								rawStr = StringUtils.join(addresslist, ", ");
							} catch (Exception e) {
								// IGNORE
								break;
							}
						} else {
							rawStr = EncoderUtil.encodeEncodedWord(rawStr, Usage.TEXT_TOKEN, 0);
						}
						RawField rawField = new RawField(field.getName(), rawStr); 
						return LenientFieldParser.getParser().parse(rawField, DecodeMonitor.SILENT);
					}
				}
			}
		}
		return LenientFieldParser.parse(field.getRaw(), DecodeMonitor.SILENT);
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
