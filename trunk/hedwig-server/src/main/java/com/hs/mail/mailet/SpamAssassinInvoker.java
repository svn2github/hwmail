package com.hs.mail.mailet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.mail.MessagingException;

import org.apache.commons.io.FileUtils;

import com.hs.mail.smtp.message.SmtpMessage;

/**
 * Sends the message through daemonized SpamAssassin (spamd), visit <a href=
 * "https://svn.apache.org/repos/asf/spamassassin/trunk/spamd/PROTOCOL">The
 * SpamAssassin Network Protocol</a> for info on protocol.
 */
public class SpamAssassinInvoker {

	/** The mail attribute under which the status get stored. */
	public final static String STATUS_MAIL_ATTRIBUTE_NAME = "X-Spam-Status";
	
	/** The mail attribute under which the flag get stored. */
	public final static String FLAG_MAIL_ATTRIBUTE_NAME = "X-Spam-Flag";

	private String spamdHost;
	
	private int spamdPort;
	
	private String hits = "?";
	
	private String required = "?";
	
	private Map<String, String> headers = new HashMap<String, String>();

	/**
	 * Initialize the spamassassin invoker
	 * 
	 * @param spamdHost
	 *            The host on which spamd runs
	 * @param spamdPort
	 *            The port on which spamd listen
	 */
	public SpamAssassinInvoker(String spamdHost, int spamdPort) {
		this.spamdHost = spamdHost;
		this.spamdPort = spamdPort;
	}

	/**
	 * Scan a MailMessage for spam by passing it to spamd.
	 * 
	 * @param message
	 *            The MailMessage to scan
	 * @return true if spam otherwise false
	 * @throws MessagingException
	 *             if an error on scanning is detected
	 */
	public boolean scanMail(SmtpMessage message) throws MessagingException {
		Socket socket = null;
		OutputStream out = null;
		BufferedReader in = null;
		
		try {
			socket = new Socket(spamdHost, spamdPort);
			
			out = socket.getOutputStream();
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.write("CHECK SPAMC/1.2\r\n\r\n".getBytes());
			
			// pass the message to spamd
			FileUtils.copyFile(message.getDataFile(), out);
			out.flush();
			socket.shutdownOutput();
			
			String s = null;
			while ((s = in.readLine()) != null) {
				if (s.startsWith("Spam:")) {
					StringTokenizer t = new StringTokenizer(s, " ");
					boolean spam;
					try {
						t.nextToken();
						spam = Boolean.valueOf(t.nextToken()).booleanValue();
					} catch (Exception e) {
						// On exception return false
						return false;
					}
					t.nextToken();
					hits = t.nextToken();
					t.nextToken();
					required = t.nextToken();
					
					if (spam) {
						// message was spam
						headers.put(FLAG_MAIL_ATTRIBUTE_NAME, "Yes");
						headers.put(STATUS_MAIL_ATTRIBUTE_NAME,
								new StringBuffer("Yes, hits=").append(hits)
										.append(" required=").append(required)
										.toString());
						
						// spam detected
						return true;
					} else {
						// add headers
						headers.put(FLAG_MAIL_ATTRIBUTE_NAME, "No");
						headers.put(STATUS_MAIL_ATTRIBUTE_NAME,
								new StringBuffer("No, hits=").append(hits)
										.append(" required=").append(required)
										.toString());
						
						return false;
					}
				}
			}
			return false;
		} catch (UnknownHostException e) {
			throw new MessagingException(
					"Error communicating with spamd. Unknown host: "
							+ spamdHost);
		} catch (IOException e) {
			throw new MessagingException("Error communicating with spamd on "
					+ spamdHost + ":" + spamdPort + " Exception: " + e);
		} finally {
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				// Should never happen
			}
		}
	}

	/**
	 * Return the hits which was returned by spamd
	 * 
	 * @return hits The hits which was detected
	 */
	public String getHits() {
		return hits;
	}

	/**
	 * Return the required hits
	 * 
	 * @return required The required hits before a message is handled as spam
	 */
	public String getRequiredHits() {
		return required;
	}

	/**
	 * Return the headers as attributes which spamd generates
	 * 
	 * @return headers Map of headers to add as attributes
	 */
	public Map<String, String> getHeadersAsAttribute() {
		return headers;
	}

}
