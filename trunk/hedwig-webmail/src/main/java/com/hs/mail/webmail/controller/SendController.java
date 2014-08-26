package com.hs.mail.webmail.controller;

import java.util.Date;
import java.util.Map;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.hs.mail.webmail.WmaSession;
import com.hs.mail.webmail.config.Configuration;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaStore;
import com.hs.mail.webmail.model.impl.WmaComposeMessage;
import com.hs.mail.webmail.model.impl.WmaDisplayMessage;
import com.hs.mail.webmail.model.impl.WmaMultipartFileAttach;
import com.hs.mail.webmail.util.RequestUtils;

@Controller
public class SendController {

	@RequestMapping("/send")
	public ModelAndView send(HttpSession httpsession, HttpServletRequest request)
			throws ServletException, WmaException {
		WmaSession session = new WmaSession(httpsession);
		// prepare parameters
		boolean savedraft = RequestUtils.getParameterBool(request, "savedraft",
				false);
		boolean autoarchivesent = RequestUtils.getParameterBool(request,
				"autoarchivesent", false);
		boolean defer = RequestUtils.getParameterBool(request, "defer", false);
		Date deliverytime = (defer) ? RequestUtils.getParameterDate(request,
				"deliverytime") : null;
		WmaComposeMessage msg = createWmaComposeMessage(session, request);
		if (savedraft) {
			// allow saving of message draft
			doSaveDraft(session, msg);
		} else if (deliverytime == null) {
			// now send message
			doSendMessage(session, request, msg, autoarchivesent);
		} else {
			// send message later
			doSendMessageLater(session, msg, deliverytime);
		}
		ModelAndView mav = new ModelAndView((savedraft) ? "saved" : "sent");
		mav.addObject("msg", msg);
		return mav;
	}
	
	private WmaComposeMessage createWmaComposeMessage(WmaSession session,
			HttpServletRequest request) throws WmaException {
		String encoding = RequestUtils.getParameter(request, "encoding", "UTF-8");
		boolean reply = RequestUtils.getParameterBool(request, "reply", false);
		boolean forward = RequestUtils.getParameterBool(request, "forward", false);
		boolean draft = RequestUtils.getParameterBool(request, "draft", false);
		boolean secure = RequestUtils.getParameterBool(request, "secure", false);
		int priority = RequestUtils.getParameterBool(request, "urgent", false) ? 1 : 3;

		String path = request.getParameter("path");
		int number = RequestUtils.getParameterInt(request, "number", -1);

		//retrieve all necessary parameters into local strings recipients
		String to = request.getParameter("to");
		String cc = request.getParameter("cc");
		String bcc = request.getParameter("bcc");
		String subject = request.getParameter("subject");
		String ct = RequestUtils.getParameter(request, "ct", "text/plain");
		String body = request.getParameter("body");
		
		WmaComposeMessage msg = WmaComposeMessage.createMessage(session);
		msg.setEncoding(encoding);
		msg.setDraft(draft);
		if (msg.isDraft()) {
			msg.setNumber(number);
		}
		msg.setSubject(subject);
		msg.setPriority(priority);
		msg.setReply(reply);
		msg.setForward(forward);
		msg.setSecure(secure);
		// body
		msg.setContentType(ct + "; charset=\"" + encoding + "\"");
		msg.setBody(body);
		// set sender identity
		msg.setFrom(session.getUserIdentity());
		// set all recipients
		try {
			if (StringUtils.isNotBlank(to)) {
				msg.setTo(to);
			}
			if (StringUtils.isNotBlank(cc)) {
				msg.setCC(cc);
			}
			if (StringUtils.isNotBlank(bcc)) {
				msg.setBCC(bcc);
			}
		} catch (MessagingException e) {
		}
		// Handle original message's attachments
		if (!StringUtils.isEmpty(path) && number != -1) {
			int[] partnums = RequestUtils.getParameterInts(request, "parts");
			if (partnums != null) {
				addAttachments(session, path, number, partnums, msg);
			}
		}
		// Handle attachments
		MultipartHttpServletRequest multi = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multi.getFileMap();
		for (int i = 0; i < fileMap.size(); i++) {
			MultipartFile mf = fileMap.get("att" + i);
			if (mf != null) {
				msg.addAttachment(new WmaMultipartFileAttach(mf, encoding));
			}
		}

		return msg;
	}
	
	private void addAttachments(WmaSession session, String path, int number,
			int[] partnums, WmaComposeMessage msg) {
		try {
			WmaStore store = session.getWmaStore();
			Folder folder = store.getFolder(path);
			folder.open(Folder.READ_ONLY);
			Message message = folder.getMessage(number);
			WmaDisplayMessage actualmsg = WmaDisplayMessage
					.createWmaDisplayMessage(message);
			msg.addAttachments(actualmsg, partnums);
			folder.close(false);
		} catch (Exception e) {
		}
	}
	
	private void doSaveDraft(WmaSession session, WmaComposeMessage message)
			throws WmaException {
		WmaStore store = session.getWmaStore();
		message.saveDraft(store, store.getDraftInfo().getFolder());
	}

	private void doSendMessage(WmaSession session, HttpServletRequest request,
			WmaComposeMessage message, boolean autoarchivesent)
			throws WmaException {
		if (autoarchivesent) {
			message.setNotifyURL(createNotifyURL(request));
			message.send(session);
			// Archive message if necessary
			WmaStore store = session.getWmaStore();
			store.archiveMail(store.getSentMailArchive().getFolder(),
					message.getMessage());
		} else {
			message.send(session);
		}
	}

	private void doSendMessageLater(WmaSession session,
			WmaComposeMessage message, Date deliverytime) throws WmaException {
		// TODO
	}
	
	private String createNotifyURL(HttpServletRequest request) {
		String s = Configuration.getProperty("notify.url");
		if (StringUtils.isNotEmpty(s)) {
			return s;
		} else {
			return new StringBuffer().append(request.getScheme()).append("://")
					.append(request.getServerName()).append(":")
					.append(request.getServerPort())
					.append(request.getContextPath()).append("/notify")
					.toString();
		}
	}
	
}
