package com.hs.mail.imap.processor.fetch;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.hs.mail.test.TestUtil;

public class PartContentBuilderTest {

	private static PartContentBuilder partContentBuilder;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		partContentBuilder = new PartContentBuilder();
	}

	@Test
	public void test() throws Exception {
		String resource = "/nestedMultipart.eml";
		assertEquals("1.1.1 text/plain",  getMimeBodyContent(resource, new int[]{ 1, 1, 1 }));
		assertEquals("1.1.2 text/html",   getMimeBodyContent(resource, new int[]{ 1, 1, 2 }));
		assertEquals("1.2 image/jpeg",    getMimeBodyContent(resource, new int[]{ 1, 2 }));
		assertEquals("2 application/pdf", getMimeBodyContent(resource, new int[]{ 2 }));
		assertEquals("3 application/pdf", getMimeBodyContent(resource, new int[]{ 3 }));
		assertEquals("4 application/vnd", getMimeBodyContent(resource, new int[]{ 4 }));
	}

	String getMimeBodyContent(String resource, int[] path) throws Exception {
		InputStream is = TestUtil.readResourceAsStream(resource);
		partContentBuilder.build(is, path);
		byte[] bytes = partContentBuilder.getMimeBodyContent();
		return new String(bytes);
	}

}
