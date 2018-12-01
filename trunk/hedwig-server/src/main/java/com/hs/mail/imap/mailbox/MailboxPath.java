package com.hs.mail.imap.mailbox;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The path to a mailbox
 * 
 * @author Wonchul Doh
 * @since Dec 13, 2016
 *
 */
public class MailboxPath {

	private String namespace;
	private String fullname;
	private long userID;
	
	public MailboxPath(String namespace, String fullname, long userID) {
		this.namespace = namespace;
		this.fullname = fullname;
		this.userID = userID;
	}

	public String getNamespace() {
		return namespace;
	}

	public long getUserID() {
		return userID;
	}

	public String getFullName() {
		return fullname;
	}

	public String prependNamespace(String fullname) {
		return new StringBuilder(namespace).append(Mailbox.folderSeparator)
				.append(fullname).toString();
	}
	
	public String getBaseName() {
		return getBaseName(fullname, Mailbox.folderSeparator);
	}

	public boolean isPersonalNamespace() {
		return (namespace == null);
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	public static String interpret(String referenceName, String mailboxName) {
		return interpret(referenceName, mailboxName, Mailbox.folderSeparator);
	}
	
	private static String interpret(String referenceName, String mailboxName,
			String sep) {
		StringBuilder sb = new StringBuilder(referenceName);
		if (StringUtils.isNotEmpty(referenceName)) {
			if (!mailboxName.startsWith(sep)) {
				sb.append(sep);
			}
		}
		return sb.append(StringUtils.removeEnd(mailboxName, sep)).toString();
	}

	private static String getBaseName(String str, String sep) {
		String ret = str;
		while (StringUtils.containsAny(ret, "*%")) {
			int pos = ret.lastIndexOf(sep);
			if (pos == -1) {
				return "";
			}
			ret = ret.substring(0, pos);
		}
		return ret;
	}

}
