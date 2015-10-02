package com.hs.mail.webmail.controller;

import javax.mail.Flags;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hs.mail.webmail.WmaSession;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaFolder;
import com.hs.mail.webmail.model.WmaStore;
import com.hs.mail.webmail.model.impl.WmaComposeMessage;
import com.hs.mail.webmail.model.impl.WmaDisplayMessage;
import com.hs.mail.webmail.model.impl.WmaFolderImpl;
import com.hs.mail.webmail.util.RequestUtils;
import com.hs.mail.webmail.util.WmaUtils;

@Controller
@RequestMapping("/message")
public class MessageController {

	@RequestMapping(value = "display")
	public ModelAndView display(HttpSession httpsession,
			HttpServletRequest request) throws ServletException, WmaException {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		int number = RequestUtils.getParameterInt(request, "number");
		return doDisplayMessage(session, path, number);
	}
	
	@RequestMapping(value = "compose")
	public ModelAndView compose(HttpSession httpsession,
			HttpServletRequest request) throws ServletException, WmaException {
		WmaSession session = new WmaSession(httpsession);
		String to = RequestUtils.getParameter(request, "to", "");
		boolean reply = RequestUtils.getParameterBool(request, "reply", false);
		boolean forward = RequestUtils.getParameterBool(request, "forward",
				false);
		boolean toall = RequestUtils.getParameterBool(request, "toall", false);
		if (reply || forward) {
			String path = RequestUtils.getRequiredParameter(request, "path");
			int number = RequestUtils.getParameterInt(request, "number");
			String msgid = RequestUtils.getParameter(request, "msgid", null);
			return doComposeMessage(session, path, number, msgid, to, reply,
					forward, toall);
		} else {
			return doComposeMessage(session, null, -1, null, to, false, false,
					false);
		}
	}
	
	@RequestMapping(value = "move")
	public ModelAndView move(HttpSession httpsession, HttpServletRequest request)
			throws ServletException, WmaException {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		int[] numbers = RequestUtils.getParameterInts(request, "numbers");
		String destination = RequestUtils.getRequiredParameter(request,
				"destination");
		return doMoveMessages(session, path, numbers, destination);
	}

	@RequestMapping(value = "delete")
	public ModelAndView delete(HttpSession httpsession,
			HttpServletRequest request) throws ServletException, WmaException {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		int[] numbers = RequestUtils.getParameterInts(request, "numbers");
		boolean purge = RequestUtils.getParameterBool(request, "purge", false);
		return doDeleteMessages(session, path, numbers, purge);
	}

	@RequestMapping(value = "setflag")
	public ModelAndView setflag(HttpSession httpsession,
			HttpServletRequest request) throws ServletException, WmaException {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		int[] numbers = RequestUtils.getParameterInts(request, "numbers", ',');
		String flag = RequestUtils.getRequiredParameter(request, "flag");
		boolean set = RequestUtils.getParameterBool(request, "set", true);
		return doSetFlagMessages(session, path, numbers, flag, set);
	}

	@RequestMapping(value = "displaymime")
	public void displaymime(HttpSession httpsession,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, WmaException {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		int number = RequestUtils.getParameterInt(request, "number");
		doDisplayMime(response, session, path, number);
	}
	
	private ModelAndView doDisplayMessage(WmaSession session, String path,
			int number) throws WmaException {
		WmaStore store = session.getWmaStore();
		WmaFolder folder = WmaFolderImpl.createLight(store.getFolder(path));
		WmaDisplayMessage message = folder.getWmaMessage(number);
		ModelAndView mav = new ModelAndView("message");
		mav.addObject("message", message);
		return mav;
	}
	
	private ModelAndView doComposeMessage(WmaSession session, String path,
			int number, String msgid, String to, boolean reply,
			boolean forward, boolean toall) throws WmaException {
		WmaStore store = session.getWmaStore();
		WmaDisplayMessage actualmsg = null;
		if (reply || forward) {
			WmaFolder folder = WmaFolderImpl.createLight(store.getFolder(path));
			actualmsg = folder.getWmaMessage(number, msgid);
		}
		WmaComposeMessage message = WmaComposeMessage.createMessage(session,
				actualmsg, reply, forward, toall);
		ModelAndView mav = new ModelAndView("compose");
		mav.addObject("message", message);
		return mav;
	}
	
	private ModelAndView doMoveMessages(WmaSession session, String path,
			int[] numbers, String destination) throws WmaException {
		WmaStore store = session.getWmaStore();
		WmaFolder folder = WmaFolderImpl.createLight(store.getFolder(path));
		folder.copyMessages(store, numbers, destination, true);
		return new ModelAndView("jsonView", "data", Boolean.TRUE);
	}
	
	private ModelAndView doDeleteMessages(WmaSession session, String path,
			int[] numbers, boolean purge) throws WmaException {
		WmaStore store = session.getWmaStore();
		WmaFolder folder = WmaFolderImpl.createLight(store.getFolder(path));
		folder.deleteMessages(store, numbers, purge);
		return new ModelAndView("jsonView", "data", Boolean.TRUE);
	}
	
	private ModelAndView doSetFlagMessages(WmaSession session, String path,
			int[] numbers, String flag, boolean set) throws WmaException {
		WmaStore store = session.getWmaStore();
		WmaFolder folder = WmaFolderImpl.createLight(store.getFolder(path));
		Flags flags = WmaUtils.getFlags(flag);
		folder.setFlagMessages(numbers, flags, set);
		return new ModelAndView("jsonView", "data", numbers);
	}
	
	private void doDisplayMime(HttpServletResponse response,
			WmaSession session, String path, int number) throws WmaException {
		WmaStore store = session.getWmaStore();
		WmaFolder folder = WmaFolderImpl.createLight(store.getFolder(path));
		folder.writeMimeMessage(response, number, "mime.txt");
	}
	
}
