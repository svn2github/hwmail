package com.hs.mail.exception;

/**
 * Signals that an attempt to access the mailbox has failed.
 * 
 * @author Wonchul Doh
 * @since Dec 15, 2016
 *
 */
public class MailboxNotFoundException extends MailboxException {

	private static final long serialVersionUID = 5671828626135934733L;

	public MailboxNotFoundException(String message) {
		super(message);
	}
	
	public MailboxNotFoundException(String message, String name) {
		super(message + ((name == null)
				? ""
				: " (" + name + ")"));
	}

}
