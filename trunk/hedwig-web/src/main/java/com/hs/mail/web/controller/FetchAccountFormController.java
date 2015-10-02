package com.hs.mail.web.controller;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hs.mail.imap.user.User;
import com.hs.mail.imap.user.UserManager;
import com.hs.mail.web.tools.FetchAccount;
import com.hs.mail.web.tools.FetchMailer;

@Controller
public class FetchAccountFormController implements Validator {

	@Autowired
	private UserManager userManager;

	// Set a form validator
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(this);
	}

	@RequestMapping(value = "/domains/{domain}/accounts/{id}/fetch", method = RequestMethod.GET)
	public String showFetchAccountForm(@PathVariable("domain") String domain, 
			@PathVariable("id") long id,
			Model model) {
		FetchAccount fetch = new FetchAccount(id);
		model.addAttribute("fetchForm", fetch);
		return "fetch";
	}
	
	@RequestMapping(value = "/domains/{domain}/accounts/fetch", method = RequestMethod.POST)
	public String fetchAccount(@PathVariable("domain") String domain, 
			@ModelAttribute("fetchForm") @Validated FetchAccount fetch,
			BindingResult result) {
		if (result.hasErrors()) {
			return "fetch";
		}
		
		Store store = null;
		try {
			User user = userManager.getUser(fetch.getUserID());
			store = connect(user.getUserID(), user.getPassword());
			FetchMailer mailer = new FetchMailer(fetch, store.getFolder("INBOX"));
			mailer.fetch();
			return "ok";
		} catch (MessagingException e) {
			result.getModel().put("error", e);
			return "fetch";
		} finally {
			if (store != null) {
				try {
					store.close();
				} catch (MessagingException e) {
				}
			}
		}
	}
	
	private Store connect(String userName, String password)
			throws MessagingException {
		Session session = Session.getInstance(System.getProperties(), null);
		Store store = session.getStore("imap");
		store.connect("localhost", userName, password);
		return store;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return FetchAccount.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serverName", "field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required");
	}
	
}
