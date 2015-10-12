package com.hs.mail.imap.processor;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.message.request.CreateRequest;
import com.hs.mail.imap.message.responder.Responder;
import com.hs.mail.imap.message.response.HumanReadableText;

public class CreateProcessorTest extends AbstractImapProcessorTest {

    private static final String COMMAND = "CREATE";
	
	Responder responder;
	CreateProcessor processor = new MockCreateProcessor();
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		responder = mock(Responder.class);
	}
	
	@Test
	public void testCreateINBOX() throws Exception {
		CreateRequest message = new CreateRequest(TAG, COMMAND, "INBOX");
		processor.doProcess(session, message, responder);

		verify(responder, times(1)).taggedNo(message,
				HumanReadableText.FAILED_TO_CREATE_INBOX);
	}

	@Test
	public void testCreateExistingMailbox() throws Exception {
		expectMailboxExist(true);
		
		CreateRequest message = new CreateRequest(TAG, COMMAND, "MAILBOX");
		processor.doProcess(session, message, responder);

		verify(responder, times(1)).taggedNo(message,
				HumanReadableText.MAILBOX_EXISTS);
	}
	
	@Test
	public void testCreateNotExistingMailbox() throws Exception {
		expectMailboxExist(false);
		
		CreateRequest message = new CreateRequest(TAG, COMMAND, "MAILBOX");
		processor.doProcess(session, message, responder);

		verify(mailboxManager, times(1)).createMailbox(eq(1L), eq("MAILBOX"));
		verify(responder, times(1)).okCompleted(message);
	}

	private void expectMailboxExist(boolean exist) throws Exception {
		when(session.getUserID()).thenReturn(1L);
		when(mailboxManager.mailboxExists(eq(1L), eq("MAILBOX"))).thenReturn(
				exist);
	}
	
	static class MockCreateProcessor extends CreateProcessor {
    	protected MailboxManager getMailboxManager() {
    		return mailboxManager;
    	}
	}
	
}
