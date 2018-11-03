package com.hs.mail.imap.mailbox;

import java.util.List;

public class MailboxACL {
	
	/**
	 * RFC 4314 - Standard Rights
	 */
	public static final String STD_RIGHTS = "lrswipkxtea";

	public static char l_Lookup_RIGHT         = 'l';
	public static char r_Read_RIGHT           = 'r';
	public static char s_WriteSeenFlag_RIGHT  = 's';
	public static char w_Write_RIGHT          = 'w';
	public static char i_Insert_RIGHT         = 'i';
	public static char p_Post_RIGHT           = 'p';
	public static char k_CreateMailbox_RIGHT  = 'k';
	public static char x_DeleteMailbox_RIGHT  = 'x';
	public static char t_DeleteMessages_RIGHT = 't';
	public static char e_PerformExpunge_RIGHT = 'e';
	public static char a_Administer_RIGHT     = 'a';
	
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
