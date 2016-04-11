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

import com.hs.mail.imap.user.Alias;
import com.hs.mail.imap.user.User;
import com.hs.mail.imap.user.UserManager;
import com.hs.mail.web.WebSession;
import com.hs.mail.web.model.AliasWrapper;

/**
 * 
 * @author Won Chul Doh
 * @since Sep 1, 2010
 *
 */
@Controller
public class AliasFormController implements Validator {

	@Autowired
	private UserManager userManager;

	// Set a form validator
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(this);
	}

	/**
	 * Show add alias form
	 */
	@RequestMapping(value = "/domains/{domain}/aliases/add", method = RequestMethod.GET)
	public String showAddAliasForm(@PathVariable("domain") String domain,
			Model model) {
		AliasWrapper alias = new AliasWrapper();
		alias.setDomain(domain);
		model.addAttribute("userForm", alias);
		return "alias";
	}

	/**
	 * Show update alias form
	 */
	@RequestMapping(value = "/domains/{domain}/aliases/{id}/update", method = RequestMethod.GET)
	public String showUpdateAliasForm(@PathVariable("domain") String domain,
			@PathVariable("id") long id, Model model) {
		AliasWrapper alias = new AliasWrapper(userManager.getAlias(id));
		model.addAttribute("userForm", alias);
		return "alias";
	}

	/**
	 * Save or update account
	 */
	@RequestMapping(value = "/domains/{domain}/aliases", method = RequestMethod.POST)
	public String saveOrUpdateAlias(@PathVariable("domain") String domain,
			@ModelAttribute("userForm") @Validated AliasWrapper alias,
			BindingResult result, WebRequest request) {
		if (result.hasErrors()) {
			return "alias";
		}

		try {
			doSubmitAction(request, alias);
			return "ok";
		} catch (DataIntegrityViolationException e) {
			result.rejectValue("aliasName", "address.alreay.exist");
			return "alias";
		}
	}

	/**
	 * Delete aliases
	 */
	@RequestMapping(value = "/domains/{domain}/aliases/delete", method = RequestMethod.POST)
	@ResponseBody
	public void deleteAliases(@PathVariable("domain") String domain,
			@RequestParam(value = "ID") long[] idarray, WebRequest request) {
		if (ArrayUtils.isNotEmpty(idarray)) {
			request.removeAttribute(domain + WebSession.ALIAS_COUNT,
					WebRequest.SCOPE_SESSION);
			for (long id : idarray) {
				userManager.deleteAlias(id);
			}
		}
	}

	protected void doSubmitAction(WebRequest request, AliasWrapper wrapper)
			throws DataIntegrityViolationException {
		Alias alias = AliasWrapper.createAlias(wrapper);
		if (alias.getID() == 0) {
			request.removeAttribute(wrapper.getDomain()
					+ WebSession.ALIAS_COUNT, WebRequest.SCOPE_SESSION);
			userManager.addAlias(alias);
		} else {
			userManager.updateAlias(alias);
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return AliasWrapper.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AliasWrapper alias = (AliasWrapper) target;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "aliasName",
				"field.required");
		if (StringUtils.isBlank(alias.getUserID())) {
			errors.rejectValue("userID", "field.required");
		} else {
			User deliverTo = userManager.getUserByAddress(alias.getUserID());
			if (deliverTo == null) {
				errors.rejectValue("userID", "not.exist.address");
			} else {
				alias.setDeliverTo(deliverTo.getID());
			}
		}
	}

}
