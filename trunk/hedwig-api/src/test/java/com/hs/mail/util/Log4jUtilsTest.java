package com.hs.mail.util;

import org.apache.log4j.Logger;
import org.junit.Test;

public class Log4jUtilsTest {

	private Logger logger = Logger.getLogger(Log4jUtilsTest.class);
	
	@Test
	public void test() {
		Log4jUtils.addAppender(logger, null, "%m%n");
		logger.debug("Can you see me?");
	}

}
