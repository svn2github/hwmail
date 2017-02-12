package com.hs.mail.smtp.processor.hook;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

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

}
