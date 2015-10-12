package com.hs.mail.imap.mailbox;

import static org.junit.Assert.*;

import org.junit.Test;

public class MailboxQueryTest {

	@Test
	public void testWildCard() throws Exception {
		MailboxQuery mq = new MailboxQuery("", "this.%.%.%.h*");
		assertTrue(mq.match("this.is.a.mailbox.hierarchy"));
		assertTrue(!mq.match("this.hierarchy"));
	}

	@Test
	public void testNotEmptyReferenceName() throws Exception {
		MailboxQuery mq = new MailboxQuery("#news", "comp.mail.*");
		assertTrue(mq.match("#news.comp.mail.imap"));
		assertTrue(mq.match("#news.comp.mail.imap.protocol"));
		assertTrue(!mq.match("#news.comp.wiki.user"));
	}
	
}
