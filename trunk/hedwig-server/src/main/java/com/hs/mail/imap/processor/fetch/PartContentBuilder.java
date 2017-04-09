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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.james.mime4j.stream.Field;
import org.apache.james.mime4j.stream.MimeConfig;
import org.apache.james.mime4j.stream.MimeTokenStream;
import org.apache.james.mime4j.stream.RecursionMode;

/**
 * 
 * @author Won Chul Doh
 * @since Mar 8, 2010
 *
 */
public class PartContentBuilder {

	public static final byte[] EMPTY = {};

	private final MimeTokenStream parser;

    private boolean empty = false;

	private boolean topLevel = true;

	public PartContentBuilder() {
		MimeConfig config = MimeConfig.custom().setMaxLineLen(-1)
				.setMaxHeaderLen(-1).build();

		parser = new MimeTokenStream(config);
	}

    public void markEmpty() {
        empty = true;
    }

	public void build(InputStream is, int[] path) throws IOException,
			MimeException {
		parser.setRecursionMode(RecursionMode.M_RECURSE);
		parser.parse(is);
		topLevel = true;
		try {
			if (path != null) {
				for (int next : path) {
					to(next);
				}
			}
		} catch (PartNotFoundException e) {
			// Missing parts should return zero sized content
			markEmpty();
		}
	}
	
	private void skipToStartOfInner(int position)
			throws IOException, MimeException {
		final EntityState state = parser.next();
		switch (state) {
			case T_START_MULTIPART :
				break;
			case T_START_MESSAGE :
				break;
			case T_END_OF_STREAM :
				throw new PartNotFoundException(position);
			case T_END_BODYPART :
				throw new PartNotFoundException(position);
			default :
				skipToStartOfInner(position);
		}
	}

	public void to(int position) throws IOException, MimeException {
        try {
            if (topLevel) {
                topLevel = false;
            } else {
                skipToStartOfInner(position);
            }
			for (int count = 0; count < position;) {
				final EntityState state = parser.next();
				switch (state) {
					case T_BODY :
						if (position == 1) {
							count++;
						}
						break;
					case T_START_BODYPART :
						count++;
						break;
					case T_START_MULTIPART :
						if (count > 0 && count < position) {
							ignore();
						}
						break;
					case T_END_OF_STREAM :
						throw new PartNotFoundException(position);
					default :
						break;
				}
			}
        } catch (IllegalStateException e) {
            throw new PartNotFoundException(position, e);
        }
	}

	private void ignore() throws IOException, MimeException {
		for (EntityState state = parser.next(); 
				state != EntityState.T_END_MULTIPART; 
				state = parser.next()) {
			if (state == EntityState.T_START_MULTIPART) {
				ignore();
			} else if (state == EntityState.T_END_OF_STREAM) {
				throw new UnexpectedEOFException();
			}
		}
	}
	
	public Map<String, String> getMimeHeader() throws IOException,
			MimeException {
		Map<String, String> header = new HashMap<String, String>();
		if (!empty) {
			for (EntityState state = parser.getState(); 
					state != EntityState.T_END_HEADER; 
					state = parser.next()) {
				if (state == EntityState.T_FIELD) {
					Field field = parser.getField();
					header.put(field.getName(),
							StringUtils.trim(field.getBody()));
				} else if (state == EntityState.T_END_OF_STREAM) {
					throw new UnexpectedEOFException();
				}
			}
		}
		return header;
	}
	
	public Map<String, String> getMessageHeader() throws IOException,
			MimeException {
		advanceToMessage();
		return getMimeHeader();
	}
	
	private void advanceToMessage() throws IOException, MimeException {
		for (EntityState state = parser.getState(); 
				state != EntityState.T_START_MESSAGE; 
				state = parser.next()) {
			if (state == EntityState.T_END_OF_STREAM) {
				throw new UnexpectedEOFException();
			}
		}
	}
	
	public byte[] getMimeBodyContent() throws IOException, MimeException {
		if (empty) {
			return EMPTY;
		} else {
			parser.setRecursionMode(RecursionMode.M_FLAT);
			for (EntityState state = parser.getState(); 
					state != EntityState.T_BODY && state != EntityState.T_START_MULTIPART; 
					state = parser.next()) {
				if (state == EntityState.T_END_OF_STREAM) {
					return EMPTY;
				}
			}
			return IOUtils.toByteArray(parser.getInputStream());
		}
	}
	
	public byte[] getMessageBodyContent() throws IOException, MimeException {
		try {
			advanceToMessageBody();
			return getMimeBodyContent();
		} catch (UnexpectedEOFException e) {
			return EMPTY;
		}
	}
	
	private void advanceToMessageBody() throws IOException, MimeException {
		for (EntityState state = parser.getState(); 
				state != EntityState.T_BODY; 
				state = parser.next()) {
			if (state == EntityState.T_END_OF_STREAM) {
				throw new UnexpectedEOFException();
			}
		}
	}
	
	public static final class UnexpectedEOFException extends MimeException {

		private static final long serialVersionUID = 1L;

		public UnexpectedEOFException() {
			super("Premature end of stream");
		}
	
	}

	public static final class PartNotFoundException extends MimeException {

		private static final long serialVersionUID = 1L;
		private final int position;

		public PartNotFoundException(int position) {
			this(position, null);
		}

		public PartNotFoundException(int position, Exception e) {
			super("Part " + position + " not found.", e);
			this.position = position;
		}

		public int getPosition() {
			return position;
		}

	}

}
