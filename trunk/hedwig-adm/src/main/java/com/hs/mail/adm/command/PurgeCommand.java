package com.hs.mail.adm.command;

import java.util.List;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.hs.mail.imap.dao.DaoFactory;
import com.hs.mail.imap.dao.MessageDao;
import com.hs.mail.imap.mailbox.DefaultMailboxManager;
import com.hs.mail.imap.message.PhysMessage;

public class PurgeCommand extends AbstractCommand {

	private TransactionTemplate transactionTemplate;

	private int total = 0;

	private int count = 0;

	@Override
	protected void runTask(List<String> tokens) {
		DefaultMailboxManager manager = (DefaultMailboxManager) getMailboxManager();
		this.transactionTemplate = manager.getTransactionTemplate();

		MessageDao dao = DaoFactory.getMessageDao();
		this.total = dao.getDanglingMessageCount();
		this.count = 0;
		
		if (total > 0) {
			dao.purgeMessages(new MessageDao.PhysMessageCallback() {
				public void processPhysMessage(PhysMessage pm) {
					deletePhysicalMessage(pm);
				}
			});
		}
	}

	private void deletePhysicalMessage(final PhysMessage pm) {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(
					TransactionStatus status) {
				MessageDao dao = DaoFactory.getMessageDao();
				dao.deletePhysicalMessage(pm);
			}
		});
		progress();
	}

	private void progress() {
		count++;
		if (verbose) {
			System.out.printf("\r%5.1f%%",
					Double.valueOf(count * 100.0D / total));
		}
	}

}
