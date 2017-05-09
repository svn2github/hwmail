package com.hs.mail.adm.command;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.hs.mail.imap.mailbox.MailboxManager;

public class ExpungeCommand extends AbstractMailboxCommand {

	@Override
	protected void runTask(List<String> tokens) throws Exception {
		super.runTask(tokens);

		MailboxManager manager = getMailboxManager();
		List<Long> mailboxIds = getMailboxIDList();
		if (CollectionUtils.isNotEmpty(mailboxIds)) {
			for (Long mailboxID : mailboxIds) {
				List<Long> uids = search(mailboxID);
				if (CollectionUtils.isNotEmpty(uids)) {
					for (Long uid : uids) {
						manager.deleteMessage(uid);
					}
				}
			}
		}
	}

}
