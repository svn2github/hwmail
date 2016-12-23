package com.hs.mail.web.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.mailbox.MailboxACL;
import com.hs.mail.imap.mailbox.MailboxACL.EditMode;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.user.UserManager;

@Controller
public class AccessControlListFormController {
	
	@Autowired
	private MailboxManager mailboxManager;
	
	@Autowired
	private UserManager userManager;

	/**
	 * Show access control list form
	 */
	@RequestMapping(value = "/settings/namespaces/{namespace}/{mailboxID}/acl", method = RequestMethod.GET)
	public String showAccessControlListForm(
			@PathVariable("namespace") String namespace,
			@PathVariable("mailboxID") long mailboxID, Model model) {
		MailboxACL acl = mailboxManager.getACL(mailboxID);
		model.addAttribute("acl", acl);
		return "acl";
	}
	
	@RequestMapping(value = "/settings/namespaces/{namespace}/{mailboxID}/acl", method = RequestMethod.POST)
	public String[] saveAccessControlList(
			@PathVariable("namespace") String namespace,
			@PathVariable("mailboxID") long mailboxID, WebRequest request)
			throws Exception {
		List<String> errors = new ArrayList<String>();
		Iterator<String> it = request.getParameterNames();
		while (it.hasNext()) {
			String identifier = it.next();
			long userID = ImapConstants.ANYONE.equals(identifier)
					? ImapConstants.ANYONE_ID
					: userManager.getUserID(userManager.toAddress(identifier));
			if (userID != 0 || ImapConstants.ANYONE.equals(identifier)) {
				mailboxManager.setACL(userID, mailboxID, EditMode.REPLACE,
						request.getParameter(identifier));
			} else {
				errors.add(identifier);
			}
		}
		return errors.toArray(new String[errors.size()]);
	}
	
}
