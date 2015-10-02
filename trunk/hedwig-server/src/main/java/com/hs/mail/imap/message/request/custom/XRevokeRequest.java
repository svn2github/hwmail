package com.hs.mail.imap.message.request.custom;

import com.hs.mail.imap.ImapSession;
import com.hs.mail.imap.ImapSession.State;
import com.hs.mail.imap.message.SequenceRange;
import com.hs.mail.imap.message.request.ImapRequest;

/**
 * 
 * @author Won Chul Doh
 * @since Aug 15, 2011
 *
 */
public class XRevokeRequest extends ImapRequest {

	private final SequenceRange[] sequenceSet;
	private final boolean useUID;
	
	public XRevokeRequest(String tag, String command,
			SequenceRange[] sequenceSet, boolean useUID) {
		super(tag, command);
		this.sequenceSet = sequenceSet;
		this.useUID = useUID;
	}
	
	public SequenceRange[] getSequenceSet() {
		return sequenceSet;
	}

	public boolean isUseUID() {
		return useUID;
	}

	@Override
	public boolean validForState(State state) {
		return state == ImapSession.State.SELECTED;
	}

}
