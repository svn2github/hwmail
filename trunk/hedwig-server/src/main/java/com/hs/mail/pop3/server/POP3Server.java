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
package com.hs.mail.pop3.server;

import org.springframework.beans.factory.InitializingBean;

import com.hs.mail.container.config.Config;
import com.hs.mail.container.server.DefaultServer;
import com.hs.mail.container.server.socket.DefaultServerSocketFactory;
import com.hs.mail.container.server.socket.TLSServerSocketFactory;

/**
 * 
 * @author Won Chul Doh
 * @since April 11, 2018
 * 
 */
public class POP3Server extends DefaultServer implements InitializingBean {

	private boolean useTLS = false;
	
	public void setUseTLS(boolean useTLS) {
		this.useTLS = useTLS;
	}

	public boolean isUseTLS() {
		return useTLS;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Configure the server
		connectionTimeout = (int) Config.getNumberProperty("pop3d_timeout", 60000); 

		serverSocketFactory = (isUseTLS()) 
				? new TLSServerSocketFactory(Config.getSSLContext())
				: new DefaultServerSocketFactory();
		
		// Configure connection handler
		connectionHandler = new POP3ConnectionHandler(); 
		connectionHandler.configure();
	
		// Start the server
		start();
	
		System.out.println("POP3 Service started on port:" + getPort());
	}

}
