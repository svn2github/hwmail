package com.hs.mail.webmail.sieve;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.jsieve.parser.generated.ParseException;
import org.junit.Test;

public class SieveScriptUtilsTest {

	@Test
	public void test() throws Exception {
		File script = new File("D:/Konan/hedwig-0.6/data/usr/handysoft.co.kr/users/km/kklim/default.sieve");
		InputStream input = null;
		try {
			input = new FileInputStream(script);
			SieveScriptUtils.readScript(input);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

}
