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

import java.util.Hashtable;
import java.util.Map;

import com.hs.mail.exception.LookupException;

/**
 * 
 * @author Won Chul Doh
 * @since April 12, 2018
 * 
 */
public class POP3ProcessorFactory {

	public static POP3Processor createPOP3Processor(String command) {
		POP3Processor processor = processorMap.get(command.toLowerCase());
		if (null == processor)
			throw new LookupException("Class for '" + command + "' not found.");
		return processor;
	}

	static private Map<String, POP3Processor> processorMap = new Hashtable<String, POP3Processor>();
	static {
		processorMap.put("capa", new CapaProcessor());
		processorMap.put("dele", new DeleProcessor());
		processorMap.put("list", new ListProcessor());
		processorMap.put("noop", new NoopProcessor());
		processorMap.put("pass", new PassProcessor());
		processorMap.put("quit", new QuitProcessor());
		processorMap.put("retr", new RetrProcessor());
		processorMap.put("rset", new RsetProcessor());
		processorMap.put("stat", new StatProcessor());
		processorMap.put("top" , new TopProcessor());
		processorMap.put("uidl", new UidlProcessor());
		processorMap.put("user", new UserProcessor());
	}

}
