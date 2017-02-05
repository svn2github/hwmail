package com.hs.mail.test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class TestUtil {

	public static String readResource(String resource, String charset)
			throws IOException {

		return IOUtils.toString(readResourceAsStream(resource), charset);
	}

	public static InputStream readResourceAsStream(String resource)
			throws IOException {

		return new BufferedInputStream(
				TestUtil.class.getResource(resource).openStream());
	}

}
