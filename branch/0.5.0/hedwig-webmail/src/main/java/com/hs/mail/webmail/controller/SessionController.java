package com.hs.mail.webmail.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hs.mail.webmail.WmaSession;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaFolder;
import com.hs.mail.webmail.model.WmaFolderList;
import com.hs.mail.webmail.model.WmaStore;
import com.hs.mail.webmail.model.impl.WmaFolderImpl;

@Controller
public class SessionController {

	/**
	 * Handles a user login to the WEB mail system.
	 */
	@RequestMapping("/login")
	public String login(HttpSession httpsession,
			@RequestParam("username") String username,
			@RequestParam("password") String password, Model model)
			throws WmaException {
		WmaSession session = new WmaSession(httpsession);
		// authenticate user
		WmaStore store = session.connect(username, password);
		// we have now a created store
		WmaFolder folder = WmaFolderImpl.createLight(store.getPersonalArchive()
				.getFolder());
		WmaFolderList.createSubfolderList(folder, true,
				store.getFolderSeparator());
		model.addAttribute("store", store);
		model.addAttribute("folder", folder);
		return "main";
	}

	/**
	 * Logs a user out of a WEB mail system, ending the session.
	 */
	@RequestMapping("/logout")
	public String logout(HttpSession httpsession) {
		try {
			return "login";
		} finally {
			WmaSession session = new WmaSession(httpsession);
			session.end();
		}
	}
	
}
