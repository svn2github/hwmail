package com.hs.mail.webmail.exception;

public class WmaSessionRequiredException extends RuntimeException {

	private static final long serialVersionUID = 5821314128371739173L;

	private String expectedAttribute;

	public WmaSessionRequiredException(String msg) {
		super(msg);
	}

	public WmaSessionRequiredException(String msg, String expectedAttribute) {
		super(msg);
		this.expectedAttribute = expectedAttribute;
	}

	public String getExpectedAttribute() {
		return expectedAttribute;
	}

}
