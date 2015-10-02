package com.hs.mail.webmail.model.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.WmaSession;
import com.hs.mail.webmail.config.Configuration;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaAttachment;
import com.hs.mail.webmail.model.WmaMessage;
import com.hs.mail.webmail.model.WmaMessagePart;
import com.hs.mail.webmail.model.WmaPreferences;
import com.hs.mail.webmail.model.WmaStore;
import com.hs.mail.webmail.util.MimeBodyPartDataSource;
import com.hs.mail.webmail.util.WmaUtils;

public class WmaComposeMessage implements WmaMessage {
	
	private static Logger log = LoggerFactory.getLogger(WmaComposeMessage.class);

	/**
	 * Defines the X-MAILER set by wma. This can probably help one day to
	 * recognize special mailer features.
	 */
	public static final String X_MAILER_STRING = "wdo-webmail";

	// instance attributes
	private boolean reply = false;
	private boolean forward = false;
	private boolean draft = false;
	private String replyTo;
	private String encoding;
	private StringBuffer body;
	private String contentType;
	private int number = -1;
	private MimeMessage message;
	private MimeMultipart attachments;
	private boolean multipart = false;

	/**
	 * Constructs a <tt>WmaComposeMessage</tt> instance.
	 * 
	 * @param msg
	 *            the aggregated <tt>MimeMessage</tt>.
	 */
	WmaComposeMessage(MimeMessage msg) {
		message = msg;
		attachments = new MimeMultipart();
		try {
			message.setHeader("X-Mailer", X_MAILER_STRING);
		} catch (Exception ex) {
		}
	}

	 /**
	 * Tests if the message is a reply.
	 * 
	 * @return true if the message is a reply, false otherwise.
	 */
	public boolean isReply() {
		return reply;
	}

    /**
	 * Flags the message as a reply.
	 * 
	 * @param true if the message is a reply, false otherwise.
	 */
	public void setReply(boolean reply) {
		this.reply = reply;
	}

	public boolean isForward() {
		return forward;
	}

	/**
	 * Flags the message as a forward.
	 * 
	 * @param true if the message is a forward, false otherwise.
	 */
	public void setForward(boolean forward) {
		this.forward = forward;
	}

	public boolean isDraft() {
		return draft;
	}

	/**
	 * Flags the message as a draft.
	 * 
	 * @param true if the message is a draft, false otherwise.
	 */
	public void setDraft(boolean draft) {
		this.draft = draft;
	}

	public boolean isMultipart() {
		return multipart;
	}
	
	public void setMultipart(boolean multipart) {
		this.multipart = multipart;
	}

	public String getReplyTo() {
		return replyTo;
	}

	 /**
	 * Set the Reply-To address(es) of the message.
	 * 
	 * @param from
	 *            the Reply-To address(es) of the message as <tt>String</tt>.
	 */
	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getPriority() {
		return WmaUtils.getPriority(message);
	}

	public void setPriority(int priority) {
		final String[] p = { "1 (Highest)", "2 (High)", "3 (Normal)",
				"4 (Low)", "5 (Lowest)" };
		if (priority >= 1 && priority <= 5) {
			if (priority != 3) { // don't write if default
				try {
					message.setHeader("X-Priority", p[priority - 1]);
				} catch (MessagingException mex) {
				}
			}
		}
	}

	public boolean isSecure() {
		return "true".equals(WmaUtils.getHeader(message, "X-Secure", "false"));
	}

	public void setSecure(boolean secure) {
		if (secure) {
			try {
				message.setHeader("X-Secure", "true");
			} catch (MessagingException mex) {
			}
		}
	}
	
	public Address getFrom() {
		try {
			Address[] addresses = message.getFrom();
			if (addresses.length == 1) {
				return addresses[0];
			}
		} catch (Exception e) {
		}
		return null;
	}

    /**
	 * Set the sender(s) address of the message.
	 * 
	 * @param from
	 *            the sender(s) address of the message as <tt>String</tt>.
	 */
	public void setFrom(String from) {
		try {
			message.setFrom(InternetAddress.parse(from)[0]);
		} catch (Exception ex) {
		}
	}

	public String getTo() {
		try {
			return WmaUtils.prepareString(InternetAddress.toString(message
					.getRecipients(Message.RecipientType.TO)));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Sets the receiver's address(es) of the message as <tt>String</tt>.
	 * 
	 * @param to
	 *            the receiver(s) address(es) of the message as String.
	 * 
	 * @throws MessagingException
	 *             if the receiver's address(es) is (are) malformed.
	 */
	public void setTo(String to) throws MessagingException {
		if (StringUtils.isNotEmpty(to))
			message.setRecipients(Message.RecipientType.TO, to);
	}

	public String getCC() {
		try {
			return WmaUtils.prepareString(InternetAddress.toString(message
					.getRecipients(Message.RecipientType.CC)));
		} catch (Exception e) {
		return "";
		}
	}

	/**
	 * Sets the carbon copy receiver's address(es) of the message.
	 * 
	 * @param cc
	 *            the carbon copy receiver(s) address(es) of the message as
	 *            String.
	 * 
	 * @throws MessagingException
	 *             if the carbon copy receiver's address(es) is (are) malformed.
	 */
	public void setCC(String cc) throws MessagingException {
		if (StringUtils.isNotEmpty(cc))
			message.setRecipients(Message.RecipientType.CC, cc);
	}

	public String getBCC() {
		try {
			return WmaUtils.prepareString(InternetAddress.toString(message
					.getRecipients(Message.RecipientType.BCC)));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Sets the blind carbon copy receiver's address(es) of the message.
	 * 
	 * @param bcc
	 *            the blind carbon copy receiver(s) address(es) of the message
	 *            as String.
	 * 
	 * @throws MessagingException
	 *             if the blind carbon copy receiver's address(es) is (are)
	 *             malformed.
	 */
	public void setBCC(String bcc) throws MessagingException {
		if (StringUtils.isNotEmpty(bcc))
			message.setRecipients(Message.RecipientType.BCC, bcc);
	}

	public void setRecipients(Message.RecipientType type, Address[] addresses)
			throws MessagingException {
		this.message.setRecipients(type, addresses);
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Returns the <tt>Message</tt> instance associated with this
	 * <tt>WmaComposeMessage</tt>.
	 * 
	 * @return associated <tt>Message</tt> instance.
	 */
	public MimeMessage getMessage() {
		return message;
	}

	public String getSubject() {
		try {
			String[] headers = message.getHeader("Subject");
			return (headers != null) ? WmaUtils.prepareString(headers[0]) : "";
		} catch (Exception e) {
			return "";
		}
	}

    /**
	 * Sets the subject of the message.
	 * 
	 * @param subject
	 *            the subject of the message as <tt>String</tt>.
	 * 
	 * @throws WmaException
	 *             if the string is malformed (encoding, null).
	 */
	public void setSubject(String subject) {
		try {
			if (subject != null) {
				if (encoding != null) {
					message.setSubject(MimeUtility.encodeText(subject,
							encoding, "B"));
				} else {
					message.setSubject(subject);
				}
			} else {
				message.setSubject("");
			}
		} catch (Exception ex) {
		}
	}

	public void setNotifyURL(String url) {
		try {
			message.setHeader("X-Notify-URL", url);
		} catch (MessagingException mex) {
		}
	}

	public String getBody() {
		return (null == body || body.length() == 0) ? "" : body.toString();
	}
	
    /**
	 * Sets the body of the message.
	 * 
	 * @param body
	 *            the text/plain content of the message as <tt>String</tt>.
	 */
	public void setBody(String body) {
		this.body = (body != null) ? new StringBuffer(body) : new StringBuffer();
	}

    /**
	 * Appends to the body of the message.
	 * 
	 * @param str
	 *            more content for the message as <tt>String</tt>.
	 */
	public void appendBody(String str) {
		if (null == this.body) {
			this.body = new StringBuffer(str);
		} else {
			this.body.append(str);
		}
	}
	
	public String getContentType() {
		return (contentType != null) ? contentType : defaultContentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	private BodyPart buildBodyPart(WmaAttachment attach)
			throws MessagingException {
		String name = attach.getName();
		DataSource ds = attach.getDataSource();
		if (null == ds) {
			return null;
		}
		BodyPart mbp = new MimeBodyPart();
		mbp.setDataHandler(new DataHandler(ds));
		mbp.addHeader("Content-Type", attach.getContentType() + ";\n name=\""
				+ name + "\"");
		mbp.addHeader("Content-Disposition", attach.getDisposition()
				+ "; filename=\"" + name + "\"");
		// set a base64 transfer-encoding and the data handler
		mbp.addHeader("Content-Transfer-Encoding", "base64");
		return mbp;
	}

    /**
	 * Adds a attachment to this <tt>WmaComposeMessage</tt>.
	 * 
	 * @param attach
	 *            the <tt>WmaAttach</tt> that represents the attachment.
	 */
	public void addAttachment(WmaAttachment attach) {
		try {
			BodyPart bp = buildBodyPart(attach);
			if (null == bp) {
				// kick it out
			} else {
				attachments.addBodyPart(bp);
				multipart = true;
			}
		} catch (MessagingException mex) {
			log.error(mex.getMessage(), mex);
		}
	}
	
	private void buildBodyPart(WmaMessagePart part, boolean inline)
			throws WmaException {
		try {
			MimeBodyPart mbp = (MimeBodyPart) part.getPart();
			MimeBodyPart nmbp = new MimeBodyPart();
			// copy all headers
			for (Enumeration en = mbp.getAllHeaders(); en.hasMoreElements();) {
				Header h = (Header) en.nextElement();
				nmbp.setHeader(h.getName(), h.getValue());
			}
			// copy content
			InputStream in = mbp.getInputStream();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			IOUtils.copyLarge(in, bout);

			// create the datasource
			MimeBodyPartDataSource mbpds = new MimeBodyPartDataSource(
					part.getContentType(), part.getName(), bout.toByteArray());
			nmbp.setDataHandler(new DataHandler(mbpds));
			// ensure transfer encoding is set
			if (!part.isMimeType("message/rfc822")) {
				nmbp.setHeader("Content-Transfer-Encoding", "base64");
			}

			if (inline)
				nmbp.setDisposition(Part.INLINE);

			// append message's attachments to this message
			attachments.addBodyPart(nmbp);
			multipart = true;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new WmaException("wma.composemessage.failedattforward")
					.setException(ex);
		}
	}

	public void addAttachments(WmaDisplayMessage msg, int[] partnums)
			throws WmaException {
		for (int i = 0; i < partnums.length; i++) {
			WmaMessagePart part = msg.getMessagePart(partnums[i]);
			if (part != null) {
				buildBodyPart(part, false);
			}
		}
	}

	public Date getDate() {
		return null;
	}

	public Date getReceivedDate() {
		return null;
	}

	public Date getSentDate() {
		return null;
	}
	
	public void setSentDate(Date d) throws MessagingException {
		message.setSentDate(d);
	}

	public boolean isReceived() {
		return false;
	}

	public boolean isSent() {
		return false;
	}

	private void setBodyText() throws MessagingException {
		// set content either single or multipart
		if (isMultipart()) {
			// body represents the first part
			if (getBody() != null && getBody().length() > 0) {
				MimeBodyPart bodypart = new MimeBodyPart();
				bodypart.setContent(getBody(), getContentType());
				attachments.addBodyPart(bodypart, 0);
			}
			// set the content
			message.setContent(attachments);
		} else if (contentType.startsWith("text/html")) {
			message.setContent(getBody(), getContentType());
		} else {
			message.setText(getBody());
		}
	}

    /**
	 * Sends this instance via the standard convenience
	 * <tt>Transport.send()</tt>.
	 * <p>
	 * Asserts that a sender is set, and creates either a multipart or a
	 * singlepart <tt>Message</tt> from the data stored in this instance.
	 * 
	 * @throws WmaException
	 *             If there is no sender, or if sending fails.
	 */
	public void send(WmaSession session) throws WmaException {
		try {
			// Assert that a valid sender identity is set.
			if (getFrom() == null) {
				throw new WmaException("wma.composemessage.send.sendermissing");
			}
			// set body text
			setBodyText();
			// ensure that a sent date is set
			setSentDate(new Date());
			// transport the message
			session.getTransport().sendMessage(getMessage());
		} catch (SendFailedException sex) {
			throw new WmaException("wma.composemessage.send.failed")
					.setException(sex);
		} catch (MessagingException mex) {
			throw new WmaException("wma.composemessage.send.failed")
					.setException(mex);
		}
	}
	
	public void saveDraft(WmaStore store, Folder draft) throws WmaException {
		try {
			// Assert that a valid sender identity is set.
			if (getFrom() == null) {
				throw new WmaException("wma.composemessage.send.sendermissing");
			}
			// set body text
			setBodyText();
			// set draft flag
			message.setFlag(Flags.Flag.DRAFT, true);
			message.saveChanges();
			draft.open(Folder.READ_WRITE);
			if (isDraft()) {
				// existing draft will be updated
				Message draftmsg = draft.getMessage(getNumber());
				// mark old deleted
				draftmsg.setFlag(Flags.Flag.DELETED, true);
			}
			draft.appendMessages(new Message[] { message });
			draft.close(true);
			setNumber(draft.getMessageCount());
		} catch (MessagingException mex) {
			throw new WmaException("wma.composemessage.draft.failed")
					.setException(mex);
		} finally {
			try {
				if (draft != null && draft.isOpen()) {
					draft.close(false);
				}
			} catch (MessagingException mesx) {
				// don't care, the specs say it IS closed anyway
			}
		}
	}

	private void prepare(WmaDisplayMessage msg, boolean toall,
			WmaPreferences prefs) throws MessagingException {
		if (msg != null) {
			if (reply) {
				String from = getFrom().toString();
				if (toall) {
				} else {
					setTo(from);
				}
				setSubject(Configuration.getMessage("message.subject.reply",
						prefs.getLocale()));
			} else {
				setSubject(Configuration.getMessage("message.subject.forward",
						prefs.getLocale()));
			}
			setContentType(msg.getContentType());
			if (prefs.isAutoQuote()) {
			}
		}
	}

	public static WmaComposeMessage createMessage(WmaSession session)
			throws WmaException {
		return createMessage(session, null, false, false, false);
	}
	
	/**
	 * Creates a <tt>WmaComposeMessage</tt> instance.
	 * <p>
	 * This factory method should be used to create new instances of
	 * <tt>WmaComposeMessage</tt>, which are not a reply to an already existing
	 * message.
	 * 
	 * @param session
	 *            the webmail <tt>Session</tt> the message is created within.
	 * @return the newly created instance.
	 * @throws WmaException 
	 */	
	public static WmaComposeMessage createMessage(WmaSession session,
			WmaDisplayMessage msg, boolean reply, boolean forward, boolean toall)
			throws WmaException {
		try {
			WmaComposeMessage message = new WmaComposeMessage(new MimeMessage(
					session.getMailSession()));
			message.setReply(reply);
			message.setForward(forward);
			message.prepare(msg, toall, session.getPreferences());
			return message;
		} catch (MessagingException mex) {
			throw new WmaException("wma.composemessage.failedcreation")
					.setException(mex);
		}
	}

}
