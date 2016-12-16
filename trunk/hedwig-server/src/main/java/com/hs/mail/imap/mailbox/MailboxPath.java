package com.hs.mail.imap.mailbox;

import org.apache.commons.lang3.StringUtils;

import com.hs.mail.imap.ImapConstants;

/**
 * The path to a mailbox
 * 
 * @author Wonchul Doh
 * @since Dec 13, 2016
 *
 */
public class MailboxPath {

	public static final String PERSONAL_NAMESPACE	= "";

	private String namespace;
	private String user;
	private String fullname;
	private final boolean isNamespace;
	
	public MailboxPath(String referenceName, String mailboxName) {
		fullname = interpret(referenceName, mailboxName, Mailbox.folderSeparator);
		if (fullname.startsWith(ImapConstants.NAMESPACE_PREFIX)) {
			int namespaceLength = this.fullname.indexOf(Mailbox.folderSeparator);
			if (namespaceLength > -1) {
				namespace = fullname.substring(0, namespaceLength);
				// JavaMail appends separator to namespace when checking
				// existence of the namespace. (IMAPFolder.exists) 
				// I don't know why they do that.
				isNamespace = (fullname.length() == namespaceLength
						+ Mailbox.folderSeparator.length());
			} else {
				namespace = fullname;
				isNamespace = true;
			}
		} else {
			namespace = PERSONAL_NAMESPACE;
			isNamespace = false;
		}
	}
	
	public String getNamespace() {
		return namespace;
	}

	public String getUser() {
		return user;
	}

	public String getFullName() {
		return fullname;
	}
	
	public String getBaseName() {
		return getBaseName(fullname, Mailbox.folderSeparator);
	}
	
	public boolean isNamespace() {
		return isNamespace;
	}
	
	public static String interpret(String referenceName, String mailboxName,
			String sep) {
		StringBuilder sb = new StringBuilder(referenceName);
		if (StringUtils.isEmpty(referenceName)) {
			sb.append(mailboxName);
		} else if (mailboxName.startsWith(sep)) {
			sb.append(mailboxName);
		} else {
			sb.append(sep).append(mailboxName);
		}
		return sb.toString();
	}

	private static String getBaseName(String str, String sep) {
		String ret = str;
		while (StringUtils.containsAny(ret, "*%")) {
			int pos = ret.lastIndexOf(sep);
			if (pos > -1) {
				ret = ret.substring(0, pos);
			}
		}
		return ret;
	}

}
