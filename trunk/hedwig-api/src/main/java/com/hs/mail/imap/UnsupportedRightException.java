package com.hs.mail.imap;

public class UnsupportedRightException extends Exception {

	private static final long serialVersionUID = 1079894029366919256L;

    private char unsupportedRight;

	public UnsupportedRightException(char right) {
		super("Unsupported right flag '"+ right +"'.");
		this.unsupportedRight = right;
	}

	public char getUnsupportedRight() {
		return unsupportedRight;
	}

}
