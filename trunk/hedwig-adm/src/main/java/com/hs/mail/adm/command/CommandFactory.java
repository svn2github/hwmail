package com.hs.mail.adm.command;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

	public static Command getCommand(String token) throws Exception {
		Class<? extends Command> clazz = commandMap.get(token);
		if (clazz != null) {
			return clazz.newInstance();
		}
		return null;
	}

	private static Map<String, Class<? extends Command>> commandMap = new HashMap<String, Class<? extends Command>>();
	static {
		commandMap.put("expunge",    ExpungeCommand.class);
		commandMap.put("help",       HelpCommand.class);
		commandMap.put("mynetworks", MyNetworksCommand.class);
		commandMap.put("purge",      PurgeCommand.class);
		commandMap.put("search",     SearchCommand.class);
	}
	
}
