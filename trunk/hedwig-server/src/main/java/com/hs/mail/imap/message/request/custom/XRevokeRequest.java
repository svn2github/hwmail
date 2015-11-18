package com.hs.mail.imap.message.request.custom;

import java.util.List;

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.ImapSession.State;
import com.hs.mail.imap.message.request.ImapRequest;

/**
 * 
 * @author Won Chul Doh
 * @since Aug 15, 2011
 *
 */
public class XRevokeRequest extends ImapRequest {

	private final long sequenceNumber;
	private String flag;
	private List<String> recipients;
	private final boolean useUID;
	
	public XRevokeRequest(String tag, String command,
			long sequenceNumber, boolean useUID) {
		super(tag, command);
		this.sequenceNumber = sequenceNumber;
		this.useUID = useUID;
	}
	
	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public List<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	public boolean isUseUID() {
		return useUID;
	}

	@Override
	public boolean validForState(State state) {
		return state == ImapSession.State.SELECTED;
	}

}
