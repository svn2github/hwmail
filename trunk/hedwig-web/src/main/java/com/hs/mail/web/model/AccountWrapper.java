package com.hs.mail.web.model;

import com.hs.mail.imap.user.User;

public class AccountWrapper {

	private String localPart;
	
	private String domain;
	
	private long size;

	private User user;
	
	public AccountWrapper() {
		this.user = new User();
	}
	
	public AccountWrapper(User user) {
		this.user = user;
		if (user != null) {
			setLocalPart(user.getLocalPart());
			setDomain(user.getDomain());
		}
	}

	public String getLocalPart() {
		return localPart;
	}

	public void setLocalPart(String localPart) {
		this.localPart = localPart;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getID() {
		return user.getID();
	}

	public void setID(long id) {
		user.setID(id);
	}

	public String getPassword() {
		return user.getPassword();
	}

	public void setPassword(String passwd) {
		user.setPassword(passwd);
	}

	public String getForwardTo() {
		return user.getForwardTo();
	}

	public void setForwardTo(String forwardTo) {
		user.setForwardTo(forwardTo);
	}

	public long getQuota() {
		return user.getQuota();
	}

	public void setQuota(long quota) {
		user.setQuota(quota);
	}
	
	public static User createUser(AccountWrapper wrapper) {
		User user = wrapper.user;
		user.setUserID(wrapper.localPart + '@' + wrapper.domain);
		return user;
	}
	
}
