package com.hs.mail.adm.command;

import java.util.List;

import com.hs.mail.util.InetAddressMatcher;

public class MyNetworksCommand implements Command {

	@Override
	public void execute(List<String> tokens) throws Exception {
		String networks = InetAddressMatcher.getCidrSignature();
		System.out.println(networks);
	}

}
