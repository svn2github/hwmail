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

import java.util.StringTokenizer;

import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.pop3.POP3Session;

/**
 * 
 * @author Won Chul Doh
 * @since April 11, 2018
 * 
 */
public interface POP3Processor {

	/** OK response. Requested content will follow */
    public final static String OK_RESPONSE = "+OK";

    /**
     * Error response. Requested content will not be provided. This prefix is
     * followed by a more detailed error message.
     */
    public final static String ERR_RESPONSE = "-ERR";
	
	public void process(POP3Session session, TcpTransport trans, StringTokenizer st);

}
