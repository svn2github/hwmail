package com.hs.mail.imap.mailbox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.hs.mail.imap.ImapSession;

public class MailboxPathTest {
	
	private static ImapSession session = new ImapSession();

	@Test
	public void test() {
		MailboxPath path = new MailboxPath(session, "", "this.is.%.sample.h*");
		assertEquals("this.is", path.getBaseName());
		assertEquals("this.is.%.sample.h*", path.getFullName());
	}
	
	@Test
	public void testNotEmptyReferenceName() {
		MailboxPath path = new MailboxPath(session, "#news", "comp.mail.*");
		assertEquals("#news.comp.mail", path.getBaseName());
		assertEquals("#news.comp.mail.*", path.getFullName());
	}
	
}
