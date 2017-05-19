package com.hs.mail.smtp.processor.hook;

import java.util.StringTokenizer;

import org.junit.AfterClass;
import org.junit.Test;

public class DNSRBLHandlerTest {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		String ipAddress = "1.2.3.4";
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(ipAddress, " .", false);
        while (st.hasMoreTokens()) {
            sb.insert(0, st.nextToken() + ".");
        }
        String reversedOctets = sb.toString();
        System.out.println(reversedOctets);
	}

}
