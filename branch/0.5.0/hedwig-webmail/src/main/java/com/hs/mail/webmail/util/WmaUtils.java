package com.hs.mail.webmail.util;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Map;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;

public class WmaUtils {
	
	private static final String ISO_8859_1 = "ISO-8859-1";

	private WmaUtils() {
	}
	
	public static boolean isHangul(String text) {
		char[] ach = text.toCharArray();
		for (int i = 0; i < ach.length; i++) {
			if (ach[i] >= '\uAC00' && ach[i] <= '\uD7A3') // hangul
				return true;
		}
		return false;
	}
	
	private static String getEncoding(String text)
			throws UnsupportedEncodingException {
		String[] preferred = { "ISO-8859-1", "EUC-KR", "KSC5601" };
		for (int i = 0; i < preferred.length; i++) {
			if (isHangul(new String(text.getBytes(preferred[i])))) {
				return preferred[i];
			}
		}
		return ISO_8859_1;
	}
	
	private static String fixEncoding(String str) {
		// IBM-eucKR is database codeset of "euc-kr"
		if (str.indexOf("IBM-euc") > 0) return str.replaceAll("IBM-euc", "euc-");
		if (str.indexOf("ks_c_5601-1987") > 0) return str.replaceAll("ks_c_5601-1987", "euc-kr");
		if (str.indexOf("ks_c_5601") > 0) return str.replaceAll("ks_c_5601", "euc-kr");
		if (str.indexOf("iso-2022-kr") > 0) return str.replaceAll("iso-2022-kr", "euc-kr");
		if (str.indexOf("?==?") > 0) return StringUtils.replaceOnce(str, "?==?", "?= =?");
		return str;
	}

	/**
	 * Method that prepares the given <tt>String</tt> by decoding it through the
	 * <tt>MimeUtility</tt> and encoding it through the <tt>EntitiyHandler</tt>.
	 */
	public static String prepareString(String str) throws Exception {
		if (null == str) {
			return "";
		} else {
			try {
				int i = -1;
				if ((i = str.indexOf("=?")) > -1) {
					return MimeUtility
							.decodeText(fixEncoding(str.substring(i)));
				} else {
					return new String(str.getBytes(getEncoding(str)));
				}
			} catch (UnsupportedEncodingException skip) {
				// ignore
			}
			return str;
		}
	}
	
	public static String getHeader(Part p, String name)
			throws MessagingException {
		String[] headers = p.getHeader(name);
		return (headers != null) ? headers[0] : null;
	}
	
	public static String getHeader(Part p, String name, String defaultValue) {
		try {
			return getHeader(p, name);
		} catch (MessagingException e) {
			return defaultValue;
		}
	}
	
	public static int getPriority(Message msg) {
		try {
			String p = getHeader(msg, "X-Priority");
			if (p != null)
				return Integer.parseInt(p.substring(0, 1));
		} catch (Exception e) {
		}
		return 3; // 3 (Normal)
	}
	
	public static Flags getFlags(String flag) {
		return (Flags) flagMap.get(flag);
	}
	
	static private Map<String, Flags> flagMap = new Hashtable<String, Flags>();
	// declare supported message flags
	static {
		flagMap.put("answered", new Flags(Flags.Flag.ANSWERED));
		flagMap.put("deleted", new Flags(Flags.Flag.DELETED));
		flagMap.put("draft", new Flags(Flags.Flag.DRAFT));
		flagMap.put("flagged", new Flags(Flags.Flag.FLAGGED));
		flagMap.put("recent", new Flags(Flags.Flag.RECENT));
		flagMap.put("seen", new Flags(Flags.Flag.SEEN));
	}

}
