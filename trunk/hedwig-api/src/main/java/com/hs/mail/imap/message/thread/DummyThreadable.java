package com.hs.mail.imap.message.thread;

class DummyThreadable extends ThreadableMessage {

	public DummyThreadable(long uid, ThreadableMeta meta) {
		super(uid, meta);
	}

	@Override
	public boolean isDummy() {
		return true;
	}
	
}
