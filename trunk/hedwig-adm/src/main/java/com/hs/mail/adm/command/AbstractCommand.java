package com.hs.mail.adm.command;

import java.util.List;

import com.hs.mail.container.config.ComponentManager;
import com.hs.mail.imap.mailbox.MailboxManager;
import com.hs.mail.imap.user.UserManager;

public abstract class AbstractCommand implements Command {
	
	protected boolean isPrintHelp = false;
	
	protected boolean verbose = false;

	@Override
	public void execute(List<String> tokens) throws Exception {
		// Parse the options specified by "-"
		parseOptions(tokens);

		if (isPrintHelp) {
			// Print the help file of the task
			printHelp();
		} else {
			// Run the specified task
			runTask(tokens);
		}
	}

	protected void parseOptions(List<String> tokens) {
		if (!tokens.isEmpty()) {
			String token = tokens.remove(0);
			if (token.startsWith("-")) {
				// Token is an option
				handleOption(token, tokens);
			} else {
				// Push back to list of tokens
				tokens.add(0, token);
				return;
			}
		}
	}

	protected void handleOption(String token, List<String> tokens) {
		isPrintHelp = false;
		// If token is a help option
		if ("-h".equals(token) || "-?".equals(token) || "--help".equals(token)) {
			isPrintHelp = true;
			tokens.clear();
		} else if ("-v".equals(token) || "--verbose".equals(token)) {
			verbose = true;
		} else {
			System.out.println("Unrecognized option: " + token);
			isPrintHelp = true;
		}
	}
	
	protected abstract void runTask(List<String> tokens) throws Exception;

	protected void printHelp() {
	}

	protected MailboxManager getMailboxManager() {
		return ComponentManager.getBeanOfType(MailboxManager.class);
	}
	
	protected UserManager getUserManager() {
		return ComponentManager.getBeanOfType(UserManager.class);
	}
	
}
