package com.hs.mail.webmail.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import net.wimpi.text.AbstractProcessor;

public class Text2HtmlProcessor extends AbstractProcessor {

	@Override
	public String getName() {
		return "text2html";
	}

	@Override
	public String process(String str) {
		if (null == str) {
			return null;
		}
		try {
			StringPrintWriter writer = new StringPrintWriter(str.length() * 2);
			process(writer, str);
			return writer.getString();
		} catch (IOException ioe) {
			return null;
		}
	}

	@Override
	public InputStream process(InputStream in) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream(in.available());

		byte[] buffer = new byte[8192];
		int amount = 0;
		while ((amount = in.read(buffer)) >= 0) {
			bout.write(buffer, 0, amount);
		}
		return new ByteArrayInputStream(process(bout.toString()).getBytes());
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
