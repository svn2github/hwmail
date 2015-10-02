package com.hs.mail.web.controller;

import java.util.Arrays;
import java.util.List;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.hs.mail.container.config.Config;
import com.hs.mail.imap.user.Alias;
import com.hs.mail.imap.user.User;
import com.hs.mail.imap.user.UserManager;
import com.hs.mail.security.login.BasicCallbackHandler;
import com.hs.mail.web.WebSession;
import com.hs.mail.web.util.Pager;

@Controller
public class WebConsole {
	
	@Autowired
	private UserManager userManager;

	@RequestMapping("/login")
	public ModelAndView login(
			@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "facility", defaultValue = "PropertiesLogin") String facility,
			WebRequest request) {
		try {
			CallbackHandler callbackHandler = new BasicCallbackHandler(username, password.toCharArray());
			LoginContext lc = new LoginContext(facility, callbackHandler);
			lc.login();
			request.setAttribute(WebSession.LOGIN_CONTEXT, lc, WebRequest.SCOPE_SESSION);
			List<String> domains = Arrays.asList(Config.getDomains());
			return new ModelAndView("console", "domains", domains);
		} catch (LoginException ex) {
			return new ModelAndView("login", "error", ex);
		}
	}

	@RequestMapping("/logout")
	public String logout(WebRequest request) {
		try {
			LoginContext lc = (LoginContext) request.getAttribute(WebSession.LOGIN_CONTEXT, WebRequest.SCOPE_SESSION);
			lc.logout();
			request.removeAttribute(WebSession.LOGIN_CONTEXT, WebRequest.SCOPE_SESSION);
		} catch (LoginException e) {
		}
		return "login";
	}
	
	@RequestMapping(value = "/domains/{domain}/accounts")
	public ModelAndView accounts(
			@PathVariable("domain") String domain,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "pageSize", defaultValue = "12") int pageSize,
			WebRequest request) {
		Integer count = (Integer) request.getAttribute(domain + WebSession.ACCOUNT_COUNT, WebRequest.SCOPE_SESSION);
		if (count == null) {
			count = userManager.getUserCount(domain);
			request.setAttribute(domain + WebSession.ACCOUNT_COUNT, count, WebRequest.SCOPE_SESSION);
		}
		Pager pager = new Pager(page, pageSize, count, true);
		List<User> users = null;
		if (count > 0) {
			users = userManager.getUserList(domain, page + 1, pageSize);
		}
		ModelAndView mav = new ModelAndView("accounts");
		mav.addObject("domain", domain);
		mav.addObject("users", users);
		mav.addObject("pager", pager);
		return mav;
	}
	
	@RequestMapping(value = "/domains/{domain}/aliases")
	public ModelAndView aliases(
			@PathVariable("domain") String domain,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "pageSize", defaultValue = "12") int pageSize,
			WebRequest request) {
		Integer count = (Integer) request.getAttribute(domain + WebSession.ALIAS_COUNT, WebRequest.SCOPE_SESSION);
		if (count == null) {
			count = userManager.getAliasCount(domain);
			request.setAttribute(domain + WebSession.ALIAS_COUNT, count, WebRequest.SCOPE_SESSION);
		}
		Pager pager = new Pager(page, pageSize, count, true);
		List<Alias> aliases = null;
		if (count > 0) {
			aliases = userManager.getAliasList(domain, page + 1, pageSize);
		}
		ModelAndView mav = new ModelAndView("aliases");
		mav.addObject("domain", domain);
		mav.addObject("aliases", aliases);
		mav.addObject("pager", pager);
		return mav;
	}

}
