package com.hs.mail.webmail.model.impl;

import javax.mail.Folder;
import javax.mail.MessagingException;

import com.hs.mail.webmail.exception.WmaException;
import com.sun.mail.iap.Argument;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.protocol.IMAPProtocol;

public class HwFolder extends WmaFolderImpl {

	public HwFolder(IMAPFolder folder) {
		super(folder);
	}

	public void revokeByUID(long uid) throws MessagingException {
		revoke(String.valueOf(uid), true);
	}
	
	public void revoke(int msgnum) throws MessagingException {
		revoke(String.valueOf(msgnum), false);
	}

	public static HwFolder createLight(Folder folder) throws WmaException {
		try {
			HwFolder hwfolder = new HwFolder((IMAPFolder) folder);
			hwfolder.setType(folder.getType());
			return hwfolder;
		} catch (MessagingException mex) {
			throw new WmaException("wma.folder.failedcreation");
		}
	}
	
	private void revoke(final String arg, final boolean uid)
			throws MessagingException {
		try {
			folder.open(Folder.READ_ONLY);
			folder.doCommand(new IMAPFolder.ProtocolCommand() {

				@Override
				public Object doCommand(IMAPProtocol p)
						throws ProtocolException {
					Argument args = new Argument();
					args.writeString(arg);
					Response[] r = (uid) ? p.command("UID XREVOKE", args) : p
							.command("XREVOKE", args);
					Response response = r[r.length - 1];
					if (!response.isOK()) {
						throw new ProtocolException("Unable to revoke message");
					}
					return null;
				}
			});

		} finally {
			shutdownFolder(folder);
		}
	}

}
