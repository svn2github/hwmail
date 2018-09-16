package com.hs.mail.webmail.util;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeUtility;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hs.mail.webmail.test.TestUtil;

public class InlineImageProcessorTest {

	private static final String URL_PATTERN = 
			"<\\s*img\\s+[^>]*src\\s*=\\s*['|\"](cid:)([^'\">]+)['|\"].*?>";

	static Pattern pattern;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		pattern = Pattern.compile(URL_PATTERN, Pattern.DOTALL);
	}

	@Test
	public void test() throws Exception {
		InputStream input = TestUtil.readResourceAsStream("/inlineimage.html");
		InputStream src = MimeUtility.decode(input, "quoted-printable");
		String str = IOUtils.toString(src);
		StringBuffer buf = new StringBuffer();
		Matcher m = pattern.matcher(str);
		while (m.find()) {
			m.appendReplacement(buf, str.substring(m.start(), m.start(1)) 
					+ "<!--" + m.group(2) + "-->"
					+ str.substring(m.end(2), m.end()));
		}
		System.out.println(m.appendTail(buf).toString());
		
	}

}
