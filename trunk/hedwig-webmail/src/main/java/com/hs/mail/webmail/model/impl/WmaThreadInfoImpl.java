package com.hs.mail.webmail.model.impl;

import java.util.List;

import javax.mail.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaMessageInfo;

public class WmaThreadInfoImpl extends WmaMessageInfoImpl {
	
	// logging
	private static Logger log = LoggerFactory.getLogger(WmaThreadInfoImpl.class);	

	// instance attributes
	private int depth;
	private List<WmaMessageInfo> conversations;
	
	protected WmaThreadInfoImpl(long uid, int depth) {
		super(uid);
		setDepth(depth);
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public List<WmaMessageInfo> getConversations() {
		return conversations;
	}

	public void setConversations(List<WmaMessageInfo> conversations) {
		this.conversations = conversations;
	}

	public static WmaThreadInfoImpl createMessageInfo(long uid, Message msg,
			int depth) throws WmaException {
		WmaThreadInfoImpl messageinfo = null;
		try {
			messageinfo = new WmaThreadInfoImpl(uid, depth);
			messageinfo.prepare(msg);
			return messageinfo;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return messageinfo;
		}
	}

	public static WmaThreadInfoImpl createDummpy(int depth) {
		return new WmaThreadInfoImpl(-1, depth);
	}

}
