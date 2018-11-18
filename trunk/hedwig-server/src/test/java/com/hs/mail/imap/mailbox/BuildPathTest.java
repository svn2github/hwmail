package com.hs.mail.imap.mailbox;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.hs.mail.exception.MailboxNotFoundException;
import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.message.response.HumanReadableText;
import com.hs.mail.imap.user.User;
import com.hs.mail.imap.user.UserManager;

public class BuildPathTest {

	private static UserManager userManager = mock(UserManager.class);
	private static User someone = mock(User.class); 
	private static User mark = mock(User.class); 
	private static ImapSession session = new ImapSession();
	
	@Before
	public void setUp() throws Exception {
		reset(userManager);
		reset(someone);
		session.authenticated(1L);
	}

	@Test
	public void test() throws MailboxNotFoundException {
		when(userManager.getUserByAddress(eq("someone"))).thenReturn(someone);
		when(someone.getID()).thenReturn(2L);
		when(userManager.getUserByAddress(eq("mark"))).thenReturn(mark);
		when(mark.getID()).thenReturn(3L);
		
		//MailboxPath path = buildMailboxPath(session, "~.%");
		String[] mailboxNames = { "Private.HERA", "~someone", "~someone.INBOX", "#public.%", "~mark.%", "~m%" };
		for (String mailboxName : mailboxNames) {
			MailboxPath path = buildMailboxPath(session, mailboxName);
			System.out.println(path);
		}
		
		System.out.println();
		MailboxPath path = buildMailboxPath(session, MailboxPath.interpret("", "~mark.%"));
		System.out.println(path);
	}

	protected MailboxPath buildMailboxPath(ImapSession session,
			String mailboxName) throws MailboxNotFoundException {
		// Personal namespace
		if (mailboxName == null
				|| !(mailboxName.startsWith(ImapConstants.SHARED_PREFIX)
						|| mailboxName.startsWith(ImapConstants.USER_PREFIX))) {
			return new MailboxPath(null, mailboxName, session.getUserID());
		}

		String namespace = null;
		int namespaceLength = mailboxName.indexOf(Mailbox.folderSeparator);
		if (namespaceLength > -1) {
			namespace = mailboxName.substring(0, namespaceLength);
		} else {
			namespace = mailboxName;
		}

		// Shared namespace 
		if (mailboxName.startsWith(ImapConstants.SHARED_PREFIX)) {
			return new MailboxPath(namespace, mailboxName,
					ImapConstants.ANYONE_ID);
		}

		// Other user's namespace
		if (StringUtils.containsAny(namespace, "*%")) {
			return new MailboxPath(namespace, namespace.substring(1),
					ImapConstants.ANYONE_ID);
		}
		User user = userManager.getUserByAddress(namespace.substring(1));
		if (user == null) {
			throw new MailboxNotFoundException(
					HumanReadableText.MAILBOX_NOT_FOUND);
		}

		return new MailboxPath(namespace,
				(namespaceLength > -1)
						? mailboxName.substring(namespaceLength + 1)
						: StringUtils.EMPTY,
				user.getID());
	}
	
}
