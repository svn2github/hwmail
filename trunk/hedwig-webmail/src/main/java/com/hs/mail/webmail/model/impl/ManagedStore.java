package com.hs.mail.webmail.model.impl;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.hs.mail.webmail.model.WmaStore;

public class ManagedStore implements HttpSessionBindingListener {
	
	private final WmaStore wstore; 

	public ManagedStore(WmaStore wstore) {
		this.wstore = wstore;
	}

	public WmaStore getWmaStore() {
		return wstore;
	}

	@Override
	public void valueBound(HttpSessionBindingEvent event) {
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		if (wstore != null) {
			wstore.close();
		}
	}

}
