package com.hs.mail.imap;

import org.springframework.dao.DataAccessException;

public class UnsupportedRightException extends DataAccessException {

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
