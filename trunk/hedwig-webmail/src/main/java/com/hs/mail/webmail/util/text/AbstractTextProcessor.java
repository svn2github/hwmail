package com.hs.mail.webmail.util.text;


public abstract class AbstractTextProcessor implements Processor {

	public String process(String type, String text) {
		if (null == text || text.length() == 0 || !accept(type)) {
			return text;
		}
		return process(text);
	}

	abstract boolean accept(String type);
	
	abstract public String process(String text);

}
