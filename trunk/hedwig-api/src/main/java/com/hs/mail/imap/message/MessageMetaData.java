package com.hs.mail.imap.message;

public class MessageMetaData {

	private final long uid;
	private final long size;

	public MessageMetaData(long uid, long size) {
		this.uid = uid;
		this.size = size;
	}

	public long getUid() {
		return uid;
	}

	public long getSize() {
		return size;
	}

	public String getUid(long identifier) {
		return String.valueOf(uid);
	}
	
}
