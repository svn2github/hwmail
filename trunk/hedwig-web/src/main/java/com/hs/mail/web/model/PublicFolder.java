package com.hs.mail.web.model;

import org.apache.commons.lang3.StringUtils;

import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.mailbox.Mailbox;

public class PublicFolder {

	private long mailboxID = 0;
	
	private long aliasID = 0;

	private String namespace;
	
	private String name;
	
	private String submissionAddress;
	
	public PublicFolder() {
	}
	
	public PublicFolder(String namespace) {
		this.namespace = namespace;
	}

	public long getMailboxID() {
		return mailboxID;
	}

	public void setMailboxID(long mailboxID) {
		this.mailboxID = mailboxID;
	}

	public long getAliasID() {
		return aliasID;
	}

	public void setAliasID(long aliasID) {
		this.aliasID = aliasID;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubmissionAddress() {
		return submissionAddress;
	}

	public void setSubmissionAddress(String address) {
		this.submissionAddress = address;
	}

	public String getFullName() {
		return new StringBuilder(ImapConstants.NAMESPACE_PREFIX)
				.append(namespace).append(Mailbox.folderSeparator).append(name)
				.toString();
	}
	
	public Mailbox getMailbox() {
		Mailbox mailbox = new Mailbox(getFullName(), 0);
		mailbox.setMailboxID(getMailboxID());
		return mailbox;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PublicFolder) {
			PublicFolder folder = (PublicFolder) obj;
			return StringUtils.equals(name, folder.name)
					&& StringUtils.equals(submissionAddress,
							folder.submissionAddress);
		} else {
			return false;
		}
	}
	
}
