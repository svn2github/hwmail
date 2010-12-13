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
package com.hs.mail.container.simple;

import java.io.File;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.log4j.Logger;

/**
 * Simple spring component container
 * 
 * @author Won Chul Doh
 * @since Jan 11, 2010
 */
public class SimpleSpringContainer {

	private static Logger logger = Logger.getLogger(SimpleSpringContainer.class);
	
	private static final String IMPL_CLASS_NAME = "org.springframework.context.support.FileSystemXmlApplicationContext";
	
	protected String[] configLocations;
	
	protected Object applicationContext;

	public SimpleSpringContainer(String[] configLocations) {
		super();
		this.configLocations = configLocations;
	}

 	public Object createFileSystemXmlApplicationContext(String[] configLocations)
			throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Class klass = classLoader.loadClass(IMPL_CLASS_NAME);

		Object[] args = new Object[] { configLocations, new Boolean(false) };
		Object applicationContext = ConstructorUtils.invokeConstructor(klass, args);
		MethodUtils.invokeMethod(applicationContext, "refresh", null);

		return applicationContext;
	}

	public void start() throws Exception {
		this.applicationContext = createFileSystemXmlApplicationContext(this.configLocations);
		SpringContainerShutdownHook hook = new SpringContainerShutdownHook(this);
		Runtime.getRuntime().addShutdownHook(hook);
	}

    public void forceShutdown() {
		try {
			MethodUtils.invokeMethod(this.applicationContext, "stop", null);
			MethodUtils.invokeMethod(this.applicationContext, "close", null);
		} catch (Exception e) {
		}
	}

    /**
	 * @param args
	 */
	public static void main(String[] args) {
		String configLocation = (args.length > 0) ? args[0].trim()
				: "conf/applicationContext.xml";

		try {
			String[] configLocations = new String[1];
			configLocations[0] = new File(configLocation).getCanonicalPath();
			System.setProperty("app.home", new File(configLocations[0]).getParentFile().getParent());
			SimpleSpringContainer container = new SimpleSpringContainer(
					configLocations);
			container.start();
		} catch (Exception e) {
			String errMsg = (e.getMessage() != null) ? e.getMessage() : e.getCause().getMessage();
			System.err.println(errMsg);
			logger.fatal(errMsg, e);
		}
	}
}

final class SpringContainerShutdownHook extends Thread {
	private SimpleSpringContainer container;

	protected SpringContainerShutdownHook(SimpleSpringContainer container) {
		this.container = container;
	}

	public void run() {
		this.container.forceShutdown();
	}
}
