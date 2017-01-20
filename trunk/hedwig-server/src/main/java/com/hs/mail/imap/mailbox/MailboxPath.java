package com.hs.mail.imap.mailbox;

import org.apache.commons.lang3.StringUtils;

import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.ImapSession;

/**
 * The path to a mailbox
 * 
 * @author Wonchul Doh
 * @since Dec 13, 2016
 *
 */
public class MailboxPath {

	public static final String PERSONAL_NAMESPACE = "";

	private String namespace;
	private String fullname;
	private long userID;
	
	public MailboxPath(ImapSession session, String mailboxName) {
		fullname = mailboxName;
		if (fullname.startsWith(ImapConstants.NAMESPACE_PREFIX)) {
			int namespaceLength = this.fullname.indexOf(Mailbox.folderSeparator);
			if (namespaceLength > -1) {
				namespace = fullname.substring(0, namespaceLength);
			} else {
				namespace = fullname;
			}
			userID = ImapConstants.ANYONE_ID;
		} else {
			namespace = PERSONAL_NAMESPACE;
			userID = session.getUserID();
		}
	}
	
	public MailboxPath(ImapSession session, String referenceName,
			String mailboxName) {
		this(session, interpret(referenceName, mailboxName,
				Mailbox.folderSeparator));
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
	
	public String getBaseName() {
		return getBaseName(fullname, Mailbox.folderSeparator);
	}
	
	public static String interpret(String referenceName, String mailboxName,
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
