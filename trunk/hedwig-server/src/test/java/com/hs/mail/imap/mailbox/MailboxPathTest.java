package com.hs.mail.imap.mailbox;

import static org.junit.Assert.*;

import org.junit.Test;

public class MailboxPathTest {

	@Test
	public void test() {
		MailboxPath path = new MailboxPath("", "this.is.%.sample.h*");
		assertEquals("this.is", path.getBaseName());
		assertEquals("this.is.%.sample.h*", path.getFullName());
	}
	
	@Test
	public void testNotEmptyReferenceName() {
		MailboxPath path = new MailboxPath("#news", "comp.mail.*");
		assertEquals("#news.comp.mail", path.getBaseName());
		assertEquals("#news.comp.mail.*", path.getFullName());
	}
	
	@Test
	public void testNamespace() throws Exception {
		assertTrue(isNamespace("", "#news."));
		assertTrue(isNamespace("", "#news"));
		assertFalse(isNamespace("", "#news.comp"));
		assertFalse(isNamespace("", "news"));
	}
	
	private static boolean isNamespace(String referenceName, String mailboxName) {
		MailboxPath path = new MailboxPath(referenceName, mailboxName);
		return path.isNamespace();
	}

}
