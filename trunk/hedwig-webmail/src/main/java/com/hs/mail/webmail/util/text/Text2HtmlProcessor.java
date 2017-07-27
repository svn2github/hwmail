package com.hs.mail.webmail.util.text;

import java.io.IOException;
import java.io.Writer;

public class Text2HtmlProcessor extends AbstractTextProcessor {

	boolean accept(String type) {
		return "text/plain".equals(type);
	}

	public String process(String text) {
		try {
			StringPrintWriter writer = new StringPrintWriter(text.length() * 2);
			process(writer, text);
			return writer.getString();
		} catch (IOException ioe) {
			return null;
		}
	}

	private static String hex(char ch) {
		return Integer.toHexString(ch).toUpperCase();
	}
	
	private void process(Writer out, String str) throws IOException {
		if (null == out) {
			throw new IllegalArgumentException("The writer must not be null");
		}
		if (null == str) {
			return;
		}
		int sz;
		sz = str.length();
		for (int i = 0; i < sz; i++) {
			char ch = str.charAt(i);
			
			if (ch <= '>') {
				switch (ch) {
				case '\r':
					break;
				case '\n':
					out.write("<br/>");
					out.write("\n");
					break;
				case '\t':
					out.write("&nbsp;&nbsp;&nbsp;&nbsp;");
					break;
				case '\\':
					out.write("\\\\");
					break;
				case ' ':
					out.write("&nbsp;");
					break;
				case '"':
					out.write("&#034;");
					break;
				case '&':
					out.write("&amp;");
					break;
				case '\'':
					out.write("&#039");
					break;
				case '<':
					out.write("&lt;");
					break;
				case '>':
					out.write("&gt;");
					break;
				default:
					if (ch <= 0xf) {
						out.write("\\u000" + hex(ch));
					} else if (ch <= 32) {
						out.write("\\u00" + hex(ch));
					} else {
						out.write(ch);
					}
					break;
				}
			} else {
				out.write(ch);
			}
		}
	}

}
