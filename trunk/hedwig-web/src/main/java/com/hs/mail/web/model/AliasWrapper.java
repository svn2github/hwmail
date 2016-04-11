package com.hs.mail.web.model;

import com.hs.mail.imap.user.Alias;

public class AliasWrapper {
	
	private String aliasName;
	
	private String domain;

	private Alias alias;

	public AliasWrapper() {
		this.alias = new Alias();
	}

	public AliasWrapper(Alias alias) {
		this.alias = alias;
		if (alias != null) {
			setAliasName(alias.getAliasName());
			setDomain(alias.getDomain());
		}
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public long getID() {
		return alias.getID();
	}

	public void setID(long id) {
		alias.setID(id);
	}

	public String getAlias() {
		return alias.getAlias();
	}

	public void setAlias(String address) {
		alias.setAlias(address);
	}

	public long getDeliverTo() {
		return alias.getDeliverTo();
	}

	public void setDeliverTo(long deliverTo) {
		alias.setDeliverTo(deliverTo);
	}

	public String getUserID() {
		return alias.getUserID();
	}

	public void setUserID(String userID) {
		alias.setUserID(userID);
	}

	public static Alias createAlias(AliasWrapper wrapper) {
		Alias alias = wrapper.alias;
		alias.setAlias(wrapper.aliasName + '@' + wrapper.domain);
		return alias;
	}
	
}
