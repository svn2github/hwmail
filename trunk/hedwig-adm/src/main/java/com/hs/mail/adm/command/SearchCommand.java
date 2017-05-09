package com.hs.mail.adm.command;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class SearchCommand extends AbstractMailboxCommand {

	@Override
	protected void runTask(List<String> tokens) throws Exception {
		super.runTask(tokens);

		List<Long> mailboxIds = getMailboxIDList();
		if (CollectionUtils.isNotEmpty(mailboxIds)) {
			for (Long mailboxID : mailboxIds) {
				List<Long> uids = search(mailboxID);
				if (CollectionUtils.isNotEmpty(uids)) {
					for (Long uid : uids) {
						System.out.printf("%-20s %-20s\n", mailboxID, uid);
					}
				}
			}
		}
	}

}
