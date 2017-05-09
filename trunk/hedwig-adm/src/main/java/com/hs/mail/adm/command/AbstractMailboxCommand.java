package com.hs.mail.adm.command;

import java.util.ArrayList;
import java.util.List;

import com.hs.mail.adm.search.SearchKeyBuilder;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.mailbox.SelectedMailbox;
import com.hs.mail.imap.mailbox.UidToMsnMapper;
import com.hs.mail.imap.message.search.SearchKey;
import com.hs.mail.imap.user.User;
import com.sun.mail.imap.protocol.BASE64MailboxEncoder;

public class AbstractMailboxCommand extends AbstractCommand {

	/**
	 * Run the command only for the given user.
	 */
	protected String address;

	/*
	 * Name of the mailbox
	 */
	protected String mailboxName;

	/**
	 * Search messages matching this search key
	 */
	protected SearchKey searchKey;
	
	@Override
	protected void runTask(List<String> tokens) throws Exception {
		if (tokens.isEmpty()) {
			throw new Exception("Missing mailbox name");
		}

		String name = tokens.remove(0);
		mailboxName = BASE64MailboxEncoder.encode(name);
		searchKey = new SearchKeyBuilder().buildKey(tokens);
	}

	@Override
	protected void handleOption(String token, List<String> tokens) {
		if ("-u".equals(token)) {
			if (tokens.isEmpty() || tokens.get(0).startsWith("-")) {
				isPrintHelp = true;
				tokens.clear();
				return;
			}
			address = tokens.remove(0);
		} else {
			// Let super class handle unknown option
			super.handleOption(token, tokens);
		}
	}

	protected List<Long> getMailboxIDList() {
		if (address == null) {
			return getMailboxManager().getMailboxIDList(mailboxName);
		} else {
			List<Long> list = new ArrayList<Long>();
			User user = getUserManager().getUserByAddress(address);
			if (user != null) {
				Mailbox mbox = getMailboxManager().getMailbox(user.getID(), mailboxName);
				if (mbox != null) {
					list.add(mbox.getMailboxID());
					return list;
				}
			}
			return list;
		}
	}

	protected List<Long> search(long mailboxID) {
		MailboxManager manager = getMailboxManager();
		SelectedMailbox selected = new SelectedMailbox(-1L, mailboxID, true);
		UidToMsnMapper map = new UidToMsnMapper(selected, false);
		return manager.search(map, mailboxID, searchKey, null);
	}

}
