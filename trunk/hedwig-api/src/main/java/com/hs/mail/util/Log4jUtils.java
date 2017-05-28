/*
 * Copyright 2010 the original author or authors.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.hs.mail.util;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * 
 * @author Won Chul Doh
 * @since May 28, 2017
 *
 */
public class Log4jUtils {

	public static void addAppender(Logger logger, String filename, String pattern) {
    	if (filename == null) {
    		ConsoleAppender console = new ConsoleAppender(); // create appender
    		console.setLayout(new PatternLayout(pattern));
    		console.setThreshold(Level.DEBUG);
    		console.activateOptions();
    		logger.addAppender(console);
    	} else {
    		FileAppender fa = new FileAppender();
    		fa.setFile(filename);
    		fa.setAppend(true);
    		fa.setLayout(new PatternLayout(pattern));
    		fa.setThreshold(Level.DEBUG);
    		fa.activateOptions();    		
    		logger.addAppender(fa);
    	}
	}
	
}
