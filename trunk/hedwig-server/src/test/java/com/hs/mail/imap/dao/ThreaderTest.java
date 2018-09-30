package com.hs.mail.imap.dao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.mailbox.SelectedMailbox;
import com.hs.mail.imap.mailbox.UidToMsnMapper;
import com.hs.mail.imap.message.search.AllKey;
import com.hs.mail.imap.message.thread.Threadable;
import com.hs.mail.imap.processor.ext.thread.Threader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-context.xml")
public class ThreaderTest {
	
	@Autowired
	private MailboxManager mailboxManager;

	@Test
	public void test() {
		Threadable first = null, last = null;
		List<Threadable> threadables = searchThread(3);
		for (Threadable threadable : threadables) {
			if (first == null)
				first = threadable;
			else
				last.setNext(threadable);
			last = threadable;
		}
		
		Threader t = new Threader();
		last = null;
		first = t.thread(first);
		printThread(first, 0);
	}

	private List<Threadable> searchThread(long mailboxID) {
		SelectedMailbox selected = new SelectedMailbox(-1L, mailboxID, true);
		UidToMsnMapper map = new UidToMsnMapper(selected, false);
		return mailboxManager.searchThread(map, mailboxID, new AllKey());
	}

	private static void printThread(Threadable thread, int depth) {
		for (int i = 0; i < depth; i++)
			System.out.print("  ");
		System.out.println(thread.toString());
		if (thread.getChild() != null)
			printThread(thread.getChild(), depth + 1);
		if (thread.getNext() != null)
			printThread(thread.getNext(), depth);
	}

}
