package com.hs.mail.smtp.server;

import org.springframework.beans.factory.InitializingBean;

import com.hs.mail.container.config.Config;
import com.hs.mail.container.server.DefaultServer;
import com.hs.mail.container.server.socket.DefaultServerSocketFactory;
import com.hs.mail.container.server.socket.TLSServerSocketFactory;
import com.hs.mail.smtp.processor.SmtpProcessorFactory;

/**
 * 
 * @author Won Chul Doh
 * @since Jun 20, 2010
 * 
 */
public class SmtpServer extends DefaultServer implements InitializingBean {
	
	private boolean useTLS = false;
	
	public void setUseTLS(boolean useTLS) {
		this.useTLS = useTLS;
	}

	public boolean isUseTLS() {
		return useTLS;
	}
	
	public void afterPropertiesSet() throws Exception {
		// Configure the server
		connectionTimeout = (int) Config.getNumberProperty("smtpd_timeout", 300000); 
			
		serverSocketFactory = (isUseTLS()) 
				? new TLSServerSocketFactory(Config.getSSLContext())
				: new DefaultServerSocketFactory();
		
		super.configure();

		// Configure connection handler
		connectionHandler = new SmtpConnectionHandler();
		connectionHandler.configure();

		// Configure command processors
		SmtpProcessorFactory.configure();
		
		// Start the server
		start();

		System.out.println("SMTP Service started on port:" + getPort());
	}
	
}
