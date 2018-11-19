package com.hs.mail.smtp;

import static org.junit.Assert.*;

import org.apache.james.jspf.executor.SPFResult;
import org.apache.james.jspf.impl.DefaultSPF;
import org.apache.james.jspf.impl.SPF;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xbill.DNS.Type;

import com.hs.mail.dns.DnsServer;

public class SpammerTest {
	
	private static SPF spf = new DefaultSPF();
	
	private static DnsServer dnsServer = new DnsServer();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dnsServer.afterPropertiesSet();
	}
	
	@Test
	public void testSPF() {
		String ipAddress = "183.111.174.118"; //"176.241.128.66"; //"183.110.247.5";
		String mailFrom  = "david.kim@thebridgesoft.kr"; //"oxrqg@163.com"; //"wdo@konantech.com";
		String hostName  = "223.56.188.137"; //"14-125-93-178.pool.ukrtel.net"; //"WDO-PC";
		
		SPFResult spfResult = spf.checkSPF(ipAddress, mailFrom, hostName);
		//assertEquals("pass", spfResult.getResult());
		System.out.println(spfResult.getHeaderText());
	}

	@Test
	public void testValidSenderDomain() throws Exception {
		assertNull(dnsServer.lookup("foo.bar", Type.MX));
		assertNotNull(dnsServer.lookup("konantech.com", Type.MX));
		assertNotNull(dnsServer.lookup("handysoft.co.kr", Type.MX));
		assertNotNull(dnsServer.lookup("11st.co.kr", Type.MX));
	}

}
