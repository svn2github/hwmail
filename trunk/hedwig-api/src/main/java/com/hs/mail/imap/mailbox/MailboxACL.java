package com.hs.mail.imap.mailbox;

import java.util.List;

public class MailboxACL {
	
	public static final String STD_RIGHTS = "lrswipkxtea";

	public enum EditMode {
		REPLACE, ADD, DELETE
	}
	
	private String mailbox;
	
	private List<MailboxACLEntry> entries;
	
	public String getMailbox() {
		return mailbox;
	}

	public void setMailbox(String mailbox) {
		this.mailbox = mailbox;
	}

	public List<MailboxACLEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<MailboxACLEntry> entries) {
		this.entries = entries;
	}

	public static class MailboxACLEntry {

		private String identifier;

		private String rights;

		public MailboxACLEntry() {
		}

		public String getIdentifier() {
			return identifier;
		}

		public String getRights() {
			return rights;
		}

		public void setIdentifier(String identifier) {
			this.identifier = identifier;
		}

		public void setRights(String rights) {
			this.rights = rights;
		}

	}

}
