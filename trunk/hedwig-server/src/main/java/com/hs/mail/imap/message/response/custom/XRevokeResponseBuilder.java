package com.hs.mail.imap.message.response.custom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.hs.mail.container.config.Config;
import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.processor.fetch.Address;
import com.hs.mail.imap.processor.fetch.Envelope;
import com.hs.mail.imap.processor.fetch.EnvelopeBuilder;
import com.hs.mail.imap.user.UserManager;

public class XRevokeResponseBuilder {

	private MailboxManager manager;
	private UserManager usermgr;
	private Envelope envelope;
	
	public XRevokeResponseBuilder(MailboxManager manager, UserManager usermgr,
			long physMessageID) {
		this.manager = manager;
		this.usermgr = usermgr;
		this.envelope = buildEnvelope(physMessageID);
	}
	
	public Map<String, String> build(long uid, String flag,
			List<String> recipients) {
		Map<String, String> response = buildAddresses(recipients);
		for (String address : response.keySet()) {
			if (response.get(address) == null) {
				// Cannot revoke messages sent to remote recipients
				long userId = usermgr.getUserID(address);
				if (userId != 0) {
					String responseCode = verifyAndRevoke(uid, userId, flag);
					response.put(address, responseCode);
				} else {
					// Recipient has been deleted
					response.put(address, NONEXISTENT);
				}
			}
		}
		return response;
	}

	public long getSenderID() {
		Address[] sender = envelope.getSender();
		if (ArrayUtils.isNotEmpty(sender)) {
			return usermgr.getUserID(sender[0].getAddress());
		} else {
			return 0;
		}
	}
	
	private Map<String, String> buildAddresses(List<String> recipients) {
		Map<String, String> originals = new HashMap<String, String>();
		buildAddresses(originals, envelope.getTo());
		buildAddresses(originals, envelope.getCc());
		buildAddresses(originals, envelope.getBcc());
		if (CollectionUtils.isNotEmpty(recipients)) {
			// Verify recipients addresses, we want to preserve the insertion
			// order
			Map<String, String> somebodys = new LinkedHashMap<String, String>();
			for (String recipient : recipients) {
				if (originals.containsKey(recipient)) {
					somebodys.put(recipient, originals.get(recipient));
				} else {
					somebodys.put(recipient, NOPERM);
				}
			}
			return somebodys;
		} else {
			return originals;
		}
	}
	
	private void buildAddresses(Map<String, String> map, Address[] addresses) {
		if (ArrayUtils.isNotEmpty(addresses)) {
			for (Address address : addresses) {
				// Cannot revoke remote recipients message
				map.put(address.getAddress(),
						Config.isLocal(address.getHostName()) ? null : NOPERM);
			}
		}
	}
	
	private Map<String, String> getHeader(long physmessageid, String[] fields) {
		return manager.getHeader(physmessageid, fields);
	}

	private Envelope buildEnvelope(long physmessageid) {
		Map<String, String> header = getHeader(physmessageid, WANTED_FIELDS);
		return new EnvelopeBuilder().build(header);
	}

	private String verify(String flag, List<Map<String, Object>> messages) {
		if (CollectionUtils.isEmpty(messages)) {
			return NONEXISTENT;
		}
		if (!"ALL".equals(flag) && messages.size() != 1) {
			return CANNOT;
		}
		Map<String, Object> rs = messages.get(0);
		if ("Y".equals(rs.get("deleted_flag"))) {
			return NONEXISTENT;
		}
		if ("UNSEEN".equals(flag) && "Y".equals(rs.get("seen_flag"))) {
			return CANNOT;
		}
		if ("RECENT".equals(flag) && "N".equals(rs.get("recent_flag"))) {
			return CANNOT;
		}
		return null;
	}
	
	private String verifyAndRevoke(long uid, long userId, String flag) {
		List<Map<String, Object>> messages = manager.getMessageByMessageID(
				userId, envelope.getMessageId());
		removeSource(uid, messages);
		String responseCode = verify(flag, messages);
		if (responseCode == null) {
			try {
				for (Map<String, Object> message : messages) {
					long messageId = getLong(message, "messageid");
					manager.deleteMessage(messageId);
				}
				return OK;
			} catch (Exception ex) {
				return CANNOT;
			}
		}
		return responseCode;
	}
	
	private void removeSource(long uid, List<Map<String, Object>> messages) {
		for (Iterator<Map<String, Object>> iterator = messages.iterator(); 
				iterator.hasNext();) {
			long messageId = getLong(iterator.next(), "messageid");
			if (messageId == uid) {
				// Remove the current element from the iterator and the list.
				iterator.remove();
			}
		}
	}
	
	private long getLong(Map<String, Object> rs, String columnName) {
		Object obj = rs.get(columnName);
		return (obj instanceof java.math.BigDecimal) 
				? ((java.math.BigDecimal) obj).longValue()
				: (Long) obj;
	}
	
	static final String[] WANTED_FIELDS = new String[] { 
		ImapConstants.RFC822_FROM, ImapConstants.RFC822_SENDER,
		ImapConstants.RFC822_TO, ImapConstants.RFC822_CC, ImapConstants.RFC822_BCC,
		ImapConstants.RFC822_MESSAGE_ID };

	static final String NOPERM      = "NOPERM";
	static final String OK          = "OK";
	static final String CANNOT      = "CANNOT";
	static final String NONEXISTENT = "NONEXISTENT";
	
}
