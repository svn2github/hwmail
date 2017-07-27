package com.hs.mail.webmail.util.text;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class XssDefendProcessor extends AbstractTextProcessor {

	boolean accept(String type) {
		return "text/html".equals(type);
	}

	public String process(String text) {
		return Jsoup.clean(text, Whitelist.relaxed());
	}

}
