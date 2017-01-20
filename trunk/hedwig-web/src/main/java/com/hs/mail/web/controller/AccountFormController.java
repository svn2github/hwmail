/*
 * Copyright 2010 the original author or authors.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.hs.mail.web.controller;

import javax.mail.internet.ParseException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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

import com.hs.mail.imap.user.User;
import com.hs.mail.web.WebSession;
import com.hs.mail.web.model.AccountWrapper;
import com.hs.mail.web.service.HwUserManager;
import com.hs.mail.web.util.MailUtils;

/**
 * 
 * @author Won Chul Doh
 * @since Sep 1, 2010
 *
 */
@Controller
public class AccountFormController implements Validator {

	@Autowired
	private HwUserManager userManager;

	// Set a form validator
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(this);
	}

	/**
	 * Show add account form
	 */
	@RequestMapping(value = "/domains/{domain}/accounts/add", method = RequestMethod.GET)
	public String showAddAccountForm(@PathVariable("domain") String domain,
			Model model) {
		AccountWrapper account = new AccountWrapper();
		account.setDomain(domain);
		model.addAttribute("userForm", account);
		return "account";
	}

	/**
	 * Show update account form
	 */
	@RequestMapping(value = "/domains/{domain}/accounts/{id}/update", method = RequestMethod.GET)
	public String showUpdateAccountForm(@PathVariable("domain") String domain,
			@PathVariable("id") long id, Model model) {
		AccountWrapper account = new AccountWrapper(userManager.getUser(id));
		account.setSize(userManager.getQuotaUsage(id));
		model.addAttribute("userForm", account);
		return "account";
	}

	/**
	 * Save or update account
	 */
	@RequestMapping(value = "/domains/{domain}/accounts", method = RequestMethod.POST)
	public String saveOrUpdateAccount(@PathVariable("domain") String domain,
			@ModelAttribute("userForm") @Validated AccountWrapper account,
			BindingResult result, WebRequest request) {
		if (result.hasErrors()) {
			return "account";
		}

		try {
			doSubmitAction(request, account);
			return "ok";
		} catch (DataIntegrityViolationException e) {
			result.rejectValue("localPart", "address.alreay.exist");
			return "account";
		}
	}

	/**
	 * Delete accounts
	 */
	@RequestMapping(value = "/domains/{domain}/accounts/delete", method = RequestMethod.POST)
	@ResponseBody
	public void deleteAccounts(@PathVariable("domain") String domain,
			@RequestParam(value = "ID") long[] idarray, WebRequest request) {
		if (ArrayUtils.isNotEmpty(idarray)) {
			request.removeAttribute(domain + WebSession.ACCOUNT_COUNT,
					WebRequest.SCOPE_SESSION);
			for (long id : idarray) {
				userManager.deleteUser(id);
			}
		}
	}

	protected void doSubmitAction(WebRequest request, AccountWrapper account)
			throws DataIntegrityViolationException {
		User user = AccountWrapper.createUser(account);
		if (user.getID() == 0) {
			request.removeAttribute(account.getDomain()
					+ WebSession.ACCOUNT_COUNT, WebRequest.SCOPE_SESSION);
			userManager.addUser(user);
		} else {
			userManager.updateUser(user);
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return AccountWrapper.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AccountWrapper account = (AccountWrapper) target;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "localPart",
				"field.required");
		if (StringUtils.isNoneEmpty(account.getForwardTo())) {
			try {
				MailUtils.validateAddress(account.getForwardTo());
			} catch (ParseException e) {
				errors.rejectValue("forwardTo", "invalid.address");
			}
		}
	}

}
