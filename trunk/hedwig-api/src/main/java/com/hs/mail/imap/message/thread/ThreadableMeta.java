package com.hs.mail.imap.message.thread;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.codec.DecoderUtil;

import com.hs.mail.imap.ImapConstants;
import com.hs.mail.util.MessageIDGenerator;

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

	private String simplifySubject(String subject) {
		String value = subject;
		if (value != null) {
			value = DecoderUtil.decodeEncodedWords(subject,
					DecodeMonitor.SILENT);
			int strLen = value.length();
			int i = 0;
			if (strLen > i && (value.charAt(i) == '[' || value.charAt(i) == '('
					|| Character.isWhitespace(value.charAt(i)))) {
				i++;
			}
			if (strLen > 2 + i
					&& (value.charAt(i) == 'r' || value.charAt(i) == 'R')
					&& (value.charAt(i + 1) == 'e'
							|| value.charAt(i + 1) == 'E')) {
				if (value.charAt(i + 2) == ':' || value.charAt(i + 2) == ']'
						|| value.charAt(i + 2) == ')') {
					this.reply = true;
					return StringUtils.trim(value.substring(i + 3));
				}
			}
		}
		return value;
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
