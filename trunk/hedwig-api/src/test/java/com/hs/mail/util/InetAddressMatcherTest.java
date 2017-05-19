package com.hs.mail.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.Test;

public class InetAddressMatcherTest {

	@Test
	public void test() throws UnknownHostException, SocketException {
		InetAddress localHost = Inet4Address.getLocalHost();
		printCIDR(localHost);		
		
		InetAddress address = InetAddress.getByName("127.0.0.1");
		printCIDR(address);
	}

	private void printCIDR(InetAddress addr) throws SocketException {
		NetworkInterface ni = NetworkInterface.getByInetAddress(addr);
		System.out.println(addr.getHostAddress());
		System.out.println(ni.getInterfaceAddresses().get(0).getNetworkPrefixLength());
		for (InterfaceAddress iaddr : ni.getInterfaceAddresses()) {
			if (iaddr.getAddress() instanceof Inet4Address) {
			    System.out.println(iaddr.getAddress() + "/" + iaddr.getNetworkPrefixLength());
				break;
			}
		}
	}

}
