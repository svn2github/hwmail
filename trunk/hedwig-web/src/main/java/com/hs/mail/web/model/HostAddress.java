package com.hs.mail.web.model;

import java.net.InetAddress;

public class HostAddress {

	private String hostAddress;
	
	private String hostName;

	public HostAddress() {
	}
	
	public HostAddress(InetAddress address) {
		this.hostAddress = address.getHostAddress();
		this.hostName = address.getHostName();
	}
	
	public String getHostAddress() {
		return hostAddress;
	}

	public String getHostName() {
		return hostName;
	}

}
