/*
 * Copyright 2018 the original author or authors.
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
package com.hs.mail.pop3.processor;

import java.util.List;

import com.hs.mail.imap.message.MessageMetaData;
import com.hs.mail.pop3.POP3Exception;
import com.hs.mail.pop3.POP3Session;

/**
 * 
 * @author Won Chul Doh
 * @since April 12, 2018
 * 
 */
public class POP3Utils {

	public static int getNumberParameter(String parameters) {
		try {
			return Integer.parseInt(parameters);
		} catch (Exception ex) {
			StringBuilder responseBuffer = new StringBuilder(64)
					.append(parameters)
					.append(" is not a valid number");
			throw new POP3Exception(responseBuffer.toString(), ex);
		}
	}
	
	public static MessageMetaData getMetaData(POP3Session session, int num) {
		List<MessageMetaData> uidList = session.getUidList();
		if (uidList == null || num > uidList.size()) {
			StringBuilder responseBuffer = new StringBuilder(64)
					.append("message ")
					.append(num)
					.append(" does not exist.");
			throw new POP3Exception(responseBuffer.toString());
		} else {
			List<Long> deletedUidList = session.getDeletedUidList();
			MessageMetaData data = uidList.get(num - 1);
			if (deletedUidList.contains(data.getUid())) {
				StringBuilder responseBuffer = new StringBuilder(64)
						.append("message ")
						.append(num)
						.append(" already deleted.");
				throw new POP3Exception(responseBuffer.toString());
			}			
			return data; 
		}
	}

}
