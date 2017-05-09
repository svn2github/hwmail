package com.hs.mail.adm.command;

public class CommandHelp {

	public static boolean isPrintHelp(String option) {
		return "-h".equals(option) || "-?".equals(option)
				|| "--help".equals(option);
	}

	public static void printHelp(String[] helpMsgs) {
		for (int i = 0; i < helpMsgs.length; i++) {
			System.out.println(helpMsgs[i]);
		}
		System.out.println();
	}

	public static void printHelp(String helpKey) {
		System.out.println(Messages.getString(helpKey));
	}
	
}
