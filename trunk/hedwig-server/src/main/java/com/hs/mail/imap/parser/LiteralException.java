package com.hs.mail.imap.parser;

public class LiteralException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	private boolean synchronous;
	private long length;
	
	public LiteralException(boolean synchronous, long length) {
		this.synchronous = synchronous;
		this.length = length;
	}

	public boolean isSynchronous() {
		return synchronous;
	}

	public long getLength() {
		return length;
	}

}
