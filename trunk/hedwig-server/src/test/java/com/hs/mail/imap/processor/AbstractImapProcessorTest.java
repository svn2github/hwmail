package com.hs.mail.imap.processor;

import static org.mockito.Mockito.*;

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.dao.MockDaoFactory;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.mailbox.SelectedMailbox;
import com.hs.mail.imap.user.UserManager;

public abstract class AbstractImapProcessorTest {

	protected static final String TAG = "TAG";
	
	protected static MailboxManager mailboxManager = mock(MailboxManager.class);
	protected static UserManager userManager = mock(UserManager.class);
	
	protected ImapSession session;
	protected SelectedMailbox selectedMailbox;

	protected void setUp() throws Exception {
		MockDaoFactory.getInstance();
		session = mock(ImapSession.class);
		selectedMailbox = mock(SelectedMailbox.class);
		reset(mailboxManager);
		reset(userManager);
	}

	protected void tearDown() throws Exception {
	}

}
