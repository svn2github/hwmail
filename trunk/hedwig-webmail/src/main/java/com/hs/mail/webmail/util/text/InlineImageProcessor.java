package com.hs.mail.webmail.util.text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.hs.mail.webmail.config.Configuration;

public class InlineImageProcessor extends AbstractTextProcessor {
	
	// logging
	private static Logger log = Logger.getLogger(InlineImageProcessor.class);

	private Pattern pattern = null;
	
	public InlineImageProcessor() {
		this.pattern = Pattern.compile(URL_PATTERN, Pattern.DOTALL);
	}

	boolean accept(String type) {
		return "text/html".equals(type);
	}

	public String process(String text) {
		String src = StringUtils.replace(Configuration.local.get(), "?",
				"/cid?") + "&cid=";
		StringBuffer buf = new StringBuffer();
		Matcher m = pattern.matcher(text);
		try {
			while (m.find()) {
				m.appendReplacement(buf,
						text.substring(m.start(), m.start(1)) + src
								+ URLEncoder.encode(m.group(2), "UTF-8")
								+ text.substring(m.end(2), m.end()));
			}
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		}
		return m.appendTail(buf).toString();
	}

	private static final String URL_PATTERN = "<\\s*img\\s+[^>]*src\\s*=\\s*['|\"](cid:)([^'\">]+)['|\"].*?>";

}
