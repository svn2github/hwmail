package com.hs.mail.sieve;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.jsieve.ConfigurationManager;
import org.apache.jsieve.SieveFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class SieveTest {
	
	private static SieveFactory  factory = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ConfigurationManager config = new ConfigurationManager();
		factory = config.build();
	}

	@Test
	public void testParser() throws Exception {
		InputStream is = null;
		try {
			Resource script = new ClassPathResource("/anyof.sieve");
			is = script.getInputStream();
			factory.parse(is);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

}
