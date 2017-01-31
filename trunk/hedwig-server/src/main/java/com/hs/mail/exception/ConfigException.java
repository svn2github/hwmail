package com.hs.mail.exception;

public class ConfigException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConfigException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor for ConfigException.
	 * 
	 * @param message
	 */
	public ConfigException(String message) {
		super(message);
	}

}
