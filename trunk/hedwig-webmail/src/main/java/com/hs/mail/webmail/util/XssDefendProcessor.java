package com.hs.mail.webmail.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import net.wimpi.text.AbstractProcessor;

public class XssDefendProcessor extends AbstractProcessor {

	@Override
	public String getName() {
		return "xssdefend";
	}

	@Override
	public String process(String str) {
		return Jsoup.clean(str, Whitelist.relaxed());
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
	
}
