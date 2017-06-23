package com.hs.mail.smtp.processor.hook;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.junit.Test;

import com.hs.mail.smtp.message.MailAddress;
import com.hs.mail.smtp.processor.hook.AccessTable.Action;
import com.hs.mail.test.TestUtil;

public class AccessTableTest {

	@Test
	public void test() throws IOException {
		File file = TestUtil.getResourceFile("/access.table");
		AccessTable access = new AccessTable(file);
		assertEquals(access.findAction(new MailAddress("<myfriend@example.com>")), Action.OK);
		assertEquals(access.findAction(new MailAddress("<yourfriend@example.com>")), Action.REJECT);
		assertEquals(access.findAction(new MailAddress("<theboss@deals.sales.com>")), Action.OK);
		assertEquals(access.findAction(new MailAddress("<theboss@deals.marketing.com>")), Action.REJECT);
	}

	@Test
	public void testIp() throws Exception {
		File file = TestUtil.getResourceFile("/ip.table");
		AccessTable access = new AccessTable(file);
		assertNull(access.findAction(InetAddress.getByName("1.2.3.3")));
		assertEquals(access.findAction(InetAddress.getByName("1.2.3.4")), Action.REJECT);
		assertEquals(access.findAction(InetAddress.getByName("1.2.4.4")), Action.REJECT);
		assertEquals(access.findAction(InetAddress.getByName("1.2.4.5")), Action.OK);
		assertEquals(access.findAction(InetAddress.getByName("10.1.4.5")), Action.OK);
		assertEquals(access.findAction(InetAddress.getByName("10.2.4.5")), Action.REJECT);
		assertEquals(access.findAction(InetAddress.getByName("2.3.4.5")), Action.REJECT);
		assertNull(access.findAction(InetAddress.getByName("3.2.4.5")));
	}

}
