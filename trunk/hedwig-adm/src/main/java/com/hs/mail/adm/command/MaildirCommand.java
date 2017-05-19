package com.hs.mail.adm.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.message.MailMessage;
import com.hs.mail.imap.processor.fetch.BodyStructureBuilder;

public class MaildirCommand extends AbstractCommand {
	
	private MailboxManager mailboxManager;

	private BodyStructureBuilder builder;
	
	@Override
	protected void runTask(List<String> tokens) throws Exception {
		if (tokens.isEmpty()) {
			throw new Exception("Missing mail location");
		}
		File dir = new File(tokens.remove(0));
		checkDirectoryForRead(dir);

		mailboxManager = getMailboxManager();
		
		synchronize(dir);
	}

	private void synchronize(File base) {
		File[] dirs = base.listFiles();
		if (ArrayUtils.isNotEmpty(dirs)) {
			for (File dir : dirs) {
				if (dir.isDirectory() && (dir.getName().length() < 3)) {
					// First two character of mail address 
					File[] accounts = dir.listFiles();
					if (ArrayUtils.isNotEmpty(accounts)) {
						for (File account : accounts) {
							long ownerID = getUserID(account.getName());
							createMailboxes(ownerID, account);
						}
					}
				}
			}
		}
	}

	private long getUserID(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void createMailboxes(long ownerID, File account) {
		File maildir = new File(account, "Maildir");
		File[] dirs = maildir.listFiles();
		if (ArrayUtils.isNotEmpty(dirs)) {
			createMailbox(ownerID, maildir, "INBOX");
			for (File dir : dirs) {
				if (dir.isDirectory() && dir.getName().startsWith(".")) {
					String mailboxName = dir.getName().substring(1);
					createMailbox(ownerID, dir, mailboxName);
				}
			}
		}
	}

	private void createMailbox(long ownerID, File dir, String mailboxName) {
		Mailbox mailbox = mailboxManager.createMailbox(ownerID, mailboxName);
		appendMessages(ownerID, mailbox.getMailboxID(), new File(dir, "cur"));
		appendMessages(ownerID, mailbox.getMailboxID(), new File(dir, "new"));
	}

	private void appendMessages(long ownerID, long mailboxID, File dir) {
		if (!dir.isDirectory()) {
			return;
		}
		File[] files = dir.listFiles();
		if (ArrayUtils.isNotEmpty(files)) {
			for (File file : files) {
				if (file.length() > 0) {
					appendMessage(ownerID, mailboxID, file);
				}
			}
		}
	}

	private void appendMessage(long ownerID, long mailboxID, File file) {
		try {
			MailMessage msg = MailMessage.createMailMessage(file);
			msg.setInternalDate(new Date(file.lastModified()));
			// mailboxManager.addMessage(ownerID, msg, mailboxID);
			msg.save(false);
			builder.build(msg.getInternalDate(), msg.getPhysMessageID());
		} catch (Exception e) {
		}
	}

	private static void checkDirectoryForRead(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory() == false) {
				throw new IOException("Directory '" + file + "' exists but is not a directory");
			}
			if (file.canRead() == false) {
				throw new IOException("Directory '" + file + "' cannot be read");
			}				
		} else {
            throw new FileNotFoundException("Directory '" + file + "' does not exist");
		}
	}
	
}
