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
package com.hs.mail.imap.message.thread;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.codec.DecoderUtil;

import com.hs.mail.imap.ImapConstants;
import com.hs.mail.util.MessageIDGenerator;

/**
 * 
 * @author Won Chul Doh
 * @since Sep 30, 2018
 *
 */
class ThreadableMeta {
	private String subject;
	private String threadID;
	private String[] threadReferences;
	private boolean reply = false;

	public ThreadableMeta(Map<String, String> header) {
		this.subject = simplifySubject(header.get(ImapConstants.RFC822_SUBJECT));
		this.threadID = getMessageID(header.get(ImapConstants.RFC822_MESSAGE_ID));
		String refs = header.get(ImapConstants.RFC822_REFERENCES);
		if (refs == null) {
			// Only examine the In-Reply-To header if there is no References header.
			refs = header.get(ImapConstants.RFC822_IN_REPLY_TO);
		}
		this.threadReferences = splitReferences(refs);
	}

	public String messageThreadID() {
		return threadID;
	}

	public String[] messageThreadReferences() {
		return threadReferences;
	}

	public String simplifiedSubject() {
		return subject;
	}

	public boolean subjectIsReply() {
		return reply;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

	private String simplifySubject(String subject) {
		String value = subject;
		if (value != null) {
			value = DecoderUtil.decodeEncodedWords(subject,
					DecodeMonitor.SILENT);
			return stripRe(value);
		}
		return value;
	}
	
	private String stripRe(String str) {
		int strLen = str.length();
		int start = 0;
		boolean done = false;
		while (!done) {
			done = true;

			// Skip white spaces.
			while (start < strLen && Character.isWhitespace(str.charAt(start)))
				start++;
			
			if (start < strLen && (str.charAt(start) == '[' || str.charAt(start) == '('))
				start++;
		
			if (start < (strLen - 2) 
					&& (str.charAt(start) == 'r' || str.charAt(start) == 'R')
					&& (str.charAt(start + 1) == 'e' || str.charAt(start + 1) == 'E')) {
				if (str.charAt(start + 2) == ':' 
						|| str.charAt(start + 2) == ']' || str.charAt(start + 2) == ')') {
					// Skip over Re:
					start += 3;
					this.reply = true;
					done = false;	// Keep going
				}
			}
		}
		
		int end = strLen;
		// Strip tailing white spaces.
		while (end > start && Character.isWhitespace(str.charAt(end - 1)))
			end--;
	
		if (start == 0 && end == strLen)
			return str;
		else
			return str.substring(start, end);
	}
	
	private String getMessageID(String messageID) {
		String tmp = StringUtils.trim(messageID);
		if (tmp != null) {
			if (tmp.length() > 0 && tmp.charAt(0) == '<'
					&& tmp.charAt(tmp.length() - 1) == '>') {
				return tmp.substring(1, tmp.length() - 1);
			}
		}
		// There must be a message ID on every message.
		tmp = MessageIDGenerator.generate("missing-id");
		return tmp.substring(1, tmp.length() - 1);
	}

	private String[] splitReferences(String refs) {
		int count = StringUtils.countMatches(refs, ">");
		if (count == 0) {
			return null;
		}
		String[] result = new String[count];
	    int start = 0;
	    int cur = 0;
	    int i = 0;
	    int strLen = refs.length(); 
	    while (i < strLen) {
	    	// Skip over everything up to and including "<".
	        while (start < strLen && refs.charAt(start) != '<')
	            start++;

	        // Skip over consecutive "<" -- I've seen "<<ID@HOST>>".
	        while (start < strLen && refs.charAt(start) == '<')
	          start++;

	        i = start;
	        while (i < strLen && refs.charAt(i) != '>')
	            i++;
	        
			if (i > start && i < strLen && refs.charAt(i) == '>') {
				result[cur++] = refs.substring(start, i);
		        start = i + 1;

		        // Skip over consecutive ">".
		        while (start < strLen && refs.charAt(start) == '>')
		          start++;

			} else {
				i++;
			}
	    }
	    
		if (cur != count) {
			// The number of ">" characters didn't equal the number of IDs we
			// extracted.
			if (cur == 0) {
				result = null;
			} else {
				String[] arr = new String[cur];
				System.arraycopy(result, 0, arr, 0, cur);
				result = arr;
			}
		}
	    
		return result;
	}

}
