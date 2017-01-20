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
package com.hs.mail.smtp.processor;

import java.util.Hashtable;
import java.util.Map;

import com.hs.mail.exception.LookupException;

/**
 * 
 * @author Won Chul Doh
 * @since May 29, 2010
 * 
 */
public class SmtpProcessorFactory {
	
	public static void configure() {
		for (SmtpProcessor processor : processorMap.values()) {
			processor.configure();
		}
	}
	
	public static SmtpProcessor createSmtpProcessor(String command) {
		SmtpProcessor processor = processorMap.get(command.toLowerCase());
		if (null == processor)
			throw new LookupException("Class for '" + command + "' not found.");
		return processor;
	}

	static private Map<String, SmtpProcessor> processorMap = new Hashtable<String, SmtpProcessor>();
	static {
		processorMap.put("auth", new AuthProcessor());
		processorMap.put("data", new DataProcessor());
		processorMap.put("ehlo", new EhloProcessor());
		processorMap.put("expn", new ExpnProcessor());
		processorMap.put("helo", new HeloProcessor());
		processorMap.put("help", new HelpProcessor());
		processorMap.put("mail", new MailProcessor());
		processorMap.put("noop", new NoopProcessor());
		processorMap.put("quit", new QuitProcessor());
		processorMap.put("rcpt", new RcptProcessor());
		processorMap.put("rset", new RsetProcessor());
		processorMap.put("vrfy", new VrfyProcessor());
	}

}
