package com.hs.mail.imap.dao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.hs.mail.exception.MailboxNotFoundException;
import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.mailbox.MailboxPath;
import com.hs.mail.imap.processor.AbstractImapProcessorTest;
import com.hs.mail.imap.processor.ListProcessor;
import com.hs.mail.imap.user.User;
import com.hs.mail.imap.user.UserManager;

public class MailboxPathTest extends AbstractImapProcessorTest {

	User someone = mock(User.class); 
	User mark = mock(User.class); 
	MockListProcessor processor = new MockListProcessor();

	@Before
	public void setUp() throws Exception {
		super.setUp();
		when(session.getUserID()).thenReturn(1L);
		when(userManager.getUserByAddress(eq("someone"))).thenReturn(someone);
		when(someone.getID()).thenReturn(2L);
		when(userManager.getUserByAddress(eq("mark"))).thenReturn(mark);
		when(mark.getID()).thenReturn(3L);
	}
	
	@Test
	public void test() throws Exception {
		MailboxPath path = null;
		
		path = processor.buildMailboxPath(session, "Private.HERA");
		assertEquals(path.getNamespace(), null);
		assertEquals(path.getUserID(), session.getUserID());
		assertEquals(path.getFullName(), "Private.HERA");
		assertFalse(path.isNamespace());
		
		path = processor.buildMailboxPath(session, "~someone");
		assertEquals(path.getNamespace(), "~someone");
		assertEquals(path.getUserID(), 2L);
		assertEquals(path.getFullName(), "");
		assertTrue(path.isNamespace());
		
		path = processor.buildMailboxPath(session, "~someone.INBOX");
		assertEquals(path.getNamespace(), "~someone");
		assertEquals(path.getUserID(), 2L);
		assertEquals(path.getFullName(), "INBOX");
		assertFalse(path.isNamespace());
		
		path = processor.buildMailboxPath(session, "#public.%");
		assertEquals(path.getNamespace(), "#public");
		assertEquals(path.getUserID(), 0L);
		assertEquals(path.getFullName(), "#public.%");
		assertEquals(path.getBaseName(), "#public");
		assertFalse(path.isNamespace());
		
		path = processor.buildMailboxPath(session, "~mark.%");
		assertEquals(path.getNamespace(), "~mark");
		assertEquals(path.getUserID(), 3L);
		assertEquals(path.getFullName(), "%");
		assertEquals(path.getBaseName(), "");
		assertFalse(path.isNamespace());
		
		path = processor.buildMailboxPath(session, "~m%");
		assertEquals(path.getNamespace(), "~m%");
		assertEquals(path.getUserID(), 0L);
		assertEquals(path.getFullName(), "");
		assertEquals(path.getBaseName(), "");
		assertTrue(path.isNamespace());
	}

    static class MockListProcessor extends ListProcessor {

    	@Override
		public MailboxPath buildMailboxPath(ImapSession session,
				String mailboxName) throws MailboxNotFoundException {
			return super.buildMailboxPath(session, mailboxName);
		}

    	@Override
		protected MailboxManager getMailboxManager() {
    		return mailboxManager;
    	}

    	@Override
    	protected UserManager getUserManager() {
    		return userManager;
    	}
    }

}
