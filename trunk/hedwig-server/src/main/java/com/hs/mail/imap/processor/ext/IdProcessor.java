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
package com.hs.mail.imap.processor.ext;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.jboss.netty.channel.Channel;

import com.hs.mail.container.config.HedwigVersion;
import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.message.request.ImapRequest;
import com.hs.mail.imap.message.request.ext.IdRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.responder.ext.IdResponder;
import com.hs.mail.imap.processor.AbstractImapProcessor;

/**
 * 
 * @author Won Chul Doh
 * @since Oct 16, 2018
 *
 */
public class IdProcessor extends AbstractImapProcessor {

	@Override
	protected void doProcess(ImapSession session, ImapRequest message,
			Responder responder) throws Exception {
		doProcess(session, (IdRequest) message, (IdResponder) responder);
	}

	private void doProcess(ImapSession session, IdRequest request,
			IdResponder responder) {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("name", "Hedwig");
		params.put("version", HedwigVersion.getVERSION());
		params.put("os", SystemUtils.OS_NAME);
		params.put("os-version", SystemUtils.OS_VERSION);
		
		responder.respond(params);
		responder.okCompleted(request);
	}

	@Override
	protected Responder createResponder(Channel channel, ImapRequest request) {
		return new IdResponder(channel, request);
	}

}
