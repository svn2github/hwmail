package com.hs.mail.imap.processor;

import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.message.request.CloseRequest;
import com.hs.mail.imap.message.responder.Responder;

public class CloseProcessorTest extends AbstractImapProcessorTest {

    private static final String COMMAND = "CLOSE";
	
	Responder responder;
	CloseProcessor processor = new MockCloseProcessor();
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		responder = mock(Responder.class);
		expectGetSelectedMailbox();
	}
	
    private void expectGetSelectedMailbox() throws Exception {
		when(session.getSelectedMailbox()).thenReturn(selectedMailbox);
		when(selectedMailbox.getMailboxID()).thenReturn(1L);
    }
    
	@Test
	public void testReadOnlyClose() throws Exception {
		when(selectedMailbox.isReadOnly()).thenReturn(true);
		
		CloseRequest message = new CloseRequest(TAG, COMMAND);
		processor.doProcess(session, message, responder);
		
		verify(mailboxManager, never()).expunge(anyLong());
		verify(session, times(1)).deselect();
		verify(responder, times(1)).okCompleted(message);
	}
	
	@Test
	public void testReadWriteClose() throws Exception {
		when(selectedMailbox.isReadOnly()).thenReturn(false);
		when(mailboxManager.getEventDispatcher()).thenAnswer(RETURNS_MOCKS);
		when(mailboxManager.expunge(eq(1L))).thenReturn(
				Arrays.asList(new Long[] { 147L }));
		
		CloseRequest message = new CloseRequest(TAG, COMMAND);
		processor.doProcess(session, message, responder);

		verify(mailboxManager, times(1)).expunge(eq(1L));
		verify(mailboxManager, times(1)).deleteMessage(eq(147L));
		verify(session, times(1)).deselect();
		verify(responder, times(1)).okCompleted(message);
	}

    static class MockCloseProcessor extends CloseProcessor {
    	protected MailboxManager getMailboxManager() {
    		return mailboxManager;
    	}
    }
    
}
