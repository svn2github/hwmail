package com.hs.mail.web.model;

import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.mailbox.Mailbox;

public class PublicFolderWrapper {

	private String namespace;
	
	private String name;
	
	public PublicFolderWrapper() {
	}
	
	public PublicFolderWrapper(String namespace) {
		this.namespace = namespace;
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

	public String getFullName() {
		return new StringBuilder(ImapConstants.NAMESPACE_PREFIX).append(namespace).append(Mailbox.folderSeparator)
				.append(name).toString();
	}

}
