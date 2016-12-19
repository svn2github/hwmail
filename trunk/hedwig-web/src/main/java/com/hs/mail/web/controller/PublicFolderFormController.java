package com.hs.mail.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.context.request.WebRequest;

import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.web.model.PublicFolderWrapper;

@Controller
public class PublicFolderFormController implements Validator {

	@Autowired
	private MailboxManager mailboxManager;
	
	// Set a form validator
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(this);
	}

	/**
	 * Show add alias form
	 */
	@RequestMapping(value = "/settings/namespaces/{namespace}/add", method = RequestMethod.GET)
	public String showAddPublicFolderForm(
			@PathVariable("namespace") String namespace, Model model) {
		PublicFolderWrapper pf = new PublicFolderWrapper(namespace);
		model.addAttribute("userForm", pf);
		return "publicfolder";
	}

	/**
	 * Save or update account
	 */
	@RequestMapping(value = "/settings/namespaces/{namespace}", method = RequestMethod.POST)
	public String savePublicFolder(@PathVariable("namespace") String namespace,
			@ModelAttribute("userForm") @Validated PublicFolderWrapper pf,
			BindingResult result, WebRequest request) {
		if (result.hasErrors()) {
			return "publicfolder";
		}

		try {
			doSubmitAction(request, pf);
			return "ok";
		} catch (DataIntegrityViolationException e) {
			result.rejectValue("name", "address.alreay.exist");
			return "publicfolder";
		}
	}

	protected void doSubmitAction(WebRequest request, PublicFolderWrapper pf)
			throws DataIntegrityViolationException {
		String mailboxName = pf.getFullName();
		mailboxManager.createMailbox(0, mailboxName);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return PublicFolderWrapper.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name",
				"field.required");
	}

}
