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
package com.hs.mail.imap.message.builder.ext;

import java.util.LinkedList;

import javax.mail.internet.MimeUtility;

import com.hs.mail.imap.message.builder.SearchRequestBuilder;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.ThreadRequest;
import com.hs.mail.imap.message.search.SearchKey;
import com.hs.mail.imap.parser.Token;
import com.hs.mail.imap.server.codec.ImapMessage;

/**
 * 
 * @author Won Chul Doh
 * @since 30 Sep, 2018
 *
 */
public class ThreadRequestBuilder extends SearchRequestBuilder {

	@Override
	public ImapRequest createRequest(String tag, String command,
			ImapMessage message, boolean useUID) {
		LinkedList<Token> tokens = message.getTokens();
		String algorithm = tokens.remove().value;
		String charset = MimeUtility.javaCharset(tokens.remove().value);
		SearchKey searchKey = createSearchKey(tag, tokens, charset);
		return new ThreadRequest(tag, command, charset, algorithm, searchKey,
				useUID);
	}

}
