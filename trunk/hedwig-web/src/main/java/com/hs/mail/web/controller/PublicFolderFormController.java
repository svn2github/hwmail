package com.hs.mail.web.controller;

import org.apache.commons.lang3.ArrayUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import com.hs.mail.imap.ImapConstants;
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
	 * Show add public folder form
	 */
	@RequestMapping(value = "/settings/namespaces/{namespace}/add", method = RequestMethod.GET)
	public String showAddPublicFolderForm(
			@PathVariable("namespace") String namespace, Model model) {
		PublicFolderWrapper pf = new PublicFolderWrapper(namespace);
		model.addAttribute("userForm", pf);
		return "publicfolder";
	}

	/**
	 * Save public folder
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
	
	/**
	 * Delete public folders
	 */
	@RequestMapping(value = "/settings/namespaces/{namespace}/delete", method = RequestMethod.POST)
	@ResponseBody
	public void deletePublicFolders(
			@PathVariable("namespace") String namespace,
			@RequestParam(value = "ID") long[] idarray) {
		if (ArrayUtils.isNotEmpty(idarray)) {
			for (long id : idarray) {
				// TODO: check children exist
				mailboxManager.deleteMailbox(ImapConstants.ANYONE_ID, id, true);
			}
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
