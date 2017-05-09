package com.hs.mail.adm.command;

import java.util.List;

public class HelpCommand implements Command {

	@Override
	public void execute(List<String> tokens) throws Exception {
		CommandHelp.printHelp("help.main");
	}

}
