package com.hs.mail.webmail.controller;

import javax.mail.Flags;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hs.mail.webmail.WmaSession;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaFolder;
import com.hs.mail.webmail.model.WmaStore;
import com.hs.mail.webmail.model.impl.HwFolder;
import com.hs.mail.webmail.model.impl.WmaComposeMessage;
import com.hs.mail.webmail.model.impl.WmaDisplayMessage;
import com.hs.mail.webmail.util.RequestUtils;
import com.hs.mail.webmail.util.WmaUtils;

@Controller
public class MessageController {
	
	@RequestMapping(value = "/message", method = RequestMethod.GET)
	public String message(@RequestParam(value = "uid") long uid,
			Model model, HttpSession httpsession, HttpServletRequest request)
			throws Exception {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		boolean print = RequestUtils.getParameterBool(request, "print", false);
		WmaStore store = session.getWmaStore();
		WmaFolder folder = store.getWmaFolder(path);
		WmaDisplayMessage message = folder.getWmaMessageByUID(uid);
		model.addAttribute("message", message);
		return print ? "printmsg" : "message";
	}
	
	@RequestMapping(value = "/message/uid", method = RequestMethod.GET)
	@ResponseBody
	public long uid(@RequestParam(value = "number") int number,
			@RequestParam(value = "offset") int offset,
			HttpSession httpsession, HttpServletRequest request)
			throws Exception {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		WmaStore store = session.getWmaStore();
		WmaFolder folder = store.getWmaFolder(path);
		return folder.getUID(number, offset);
	}
	
	@RequestMapping(value = "/message/raw", method = RequestMethod.GET)
	public void rawmessage(@RequestParam(value = "uid") int uid, 
			HttpSession httpsession, HttpServletRequest request, 
			HttpServletResponse response) 
			throws Exception {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		WmaStore store = session.getWmaStore();
		WmaFolder folder = store.getWmaFolder(path);
		folder.writeMimeMessage(response, uid, "mime.txt");
	}
	
	@RequestMapping(value = "/message/compose", method = RequestMethod.GET)
	public String compose(@RequestParam(value = "draft", defaultValue = "false") boolean draft,
			@RequestParam(value = "reply", defaultValue = "false") boolean reply,
			@RequestParam(value = "replyall", defaultValue = "false") boolean replyall,
			@RequestParam(value = "forward", defaultValue = "false") boolean forward, 
			Model model, HttpSession httpsession, HttpServletRequest request) 
			throws Exception {
		WmaSession session = new WmaSession(httpsession);
		String to = RequestUtils.getParameter(request, "to");
		if (draft || reply || replyall || forward) {
			String path = RequestUtils.getParameter(request, "path");
			long uid = RequestUtils.getParameterLong(request, "uid");
			return compose(session, path, uid, to, draft, reply, replyall, forward, model);
		} else {
			return compose(session, null, -1, to, false, false, false, false, model);
		}
	}

	@RequestMapping(value = "/message/delete", method = RequestMethod.POST)
	@ResponseBody
	public boolean delete(@RequestParam(value = "uids") long[] uids,
			@RequestParam(value = "purge", defaultValue = "false") boolean purge,
			HttpSession httpsession, HttpServletRequest request)
			throws Exception {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		WmaStore store = session.getWmaStore();
		WmaFolder folder = store.getWmaFolder(path);
		folder.deleteMessages(store, uids, purge);
		return true;
	}
	
	@RequestMapping(value = "/message/move", method = RequestMethod.POST)
	@ResponseBody
	public boolean move(@RequestParam(value = "uids") long[] uids, 
			HttpSession httpsession, HttpServletRequest request) 
			throws Exception {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		String destfolder = RequestUtils.getRequiredParameter(request, "destfolder");
		WmaStore store = session.getWmaStore();
		WmaFolder folder = store.getWmaFolder(path);
		folder.moveMessages(store, uids, destfolder);
		return true;
	}

	@RequestMapping(value = "/message/setflag")
	@ResponseBody
	public long[] setflag(@RequestParam(value = "uids") long[] uids, 
			@RequestParam(value = "flag") String flag,
			@RequestParam(value = "set", defaultValue = "true") boolean set,
			HttpSession httpsession, HttpServletRequest request)
			throws Exception {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		WmaStore store = session.getWmaStore();
		WmaFolder folder = store.getWmaFolder(path);
		Flags flags = WmaUtils.getFlags(flag);
		folder.setFlagMessages(uids, flags, set);
		return uids;
	}
	
	@RequestMapping(value = "/message/part")
	public void part(@RequestParam(value = "uid") long uid,
			@RequestParam(value = "part") int part,
			HttpSession httpsession, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		WmaStore store = session.getWmaStore();
		WmaFolder folder = store.getWmaFolder(path);
		folder.writeMessagePart(response, uid, part);
	}
	
	@RequestMapping(value = "/message/revoke")
	public void revoke(@RequestParam(value = "uids") long[] uids,
			HttpSession httpsession, HttpServletRequest request)
			throws Exception {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		WmaStore store = session.getWmaStore();
		HwFolder folder = HwFolder.createLight(store.getFolder(path));
		folder.revokeByUID(uids[0]);
	}
	
	private String compose(WmaSession session, String path, long uid, String to, 
			boolean draft, boolean reply, boolean replyall, boolean forward, Model model) 
			throws WmaException {
		WmaStore store = session.getWmaStore();
		WmaComposeMessage message = null;
		if (draft || reply || replyall || forward) {
			WmaFolder folder = store.getWmaFolder(path);
			WmaDisplayMessage actualmsg = folder.getWmaMessageByUID(uid);
			message = forward 
					? WmaComposeMessage.createForward(session, actualmsg)
					: reply ? WmaComposeMessage.createReply(session, actualmsg, replyall)
							: WmaComposeMessage.createDraft(session, actualmsg);
			model.addAttribute("actualmsg", actualmsg);
		} else {
			message = WmaComposeMessage.createMessage(session);
		}
		model.addAttribute("message", message);
		model.addAttribute("prefs", session.getPreferences());
		return "compose";
	}

}
