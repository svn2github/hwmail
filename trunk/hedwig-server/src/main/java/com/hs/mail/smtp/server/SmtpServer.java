package com.hs.mail.smtp.server;

import java.io.IOException;

import org.springframework.beans.factory.InitializingBean;

import com.hs.mail.container.config.Config;
import com.hs.mail.container.server.DefaultServer;
import com.hs.mail.container.server.socket.DefaultServerSocketFactory;
import com.hs.mail.container.server.socket.TLSServerSocketFactory;
import com.hs.mail.smtp.processor.SmtpProcessorFactory;
import com.hs.mail.util.RollingPrintStream;

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
		connectionHandler = new SmtpConnectionHandler();
			
		if (Config.getBooleanProperty("smtp_trace_protocol", false)) {
			((SmtpConnectionHandler) connectionHandler).setDebug(true);
			String path = Config.getProperty("smtp_protocol_log", null);
			if (path != null) {
				try {
					((SmtpConnectionHandler) connectionHandler)
							.setDebugOut(new RollingPrintStream(path));
				} catch (IOException e) {
					// Ignore this exception
				}
			}
		}
		serverSocketFactory = (isUseTLS()) 
				? new TLSServerSocketFactory(Config.getSSLContext())
				: new DefaultServerSocketFactory();
		
		super.configure();
		// Configure command processors
		SmtpProcessorFactory.configure();
		
		// Start the server
		start();
		
		System.out.println("SMTP Service started on port:" + getPort());
	}
	
}
