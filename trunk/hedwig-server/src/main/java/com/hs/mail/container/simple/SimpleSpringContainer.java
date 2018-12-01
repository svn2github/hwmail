package com.hs.mail.container.simple;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

@SuppressWarnings("static-access")
public class SimpleSpringContainer {
	
	static Logger console = LoggerFactory.getLogger("console");

	private static final String DEFAULT_CONFIG_LOCATION = "../conf/applicationContext.xml";

	private static final Options OPTS = new Options();
	static {
		OPTS.addOption(OptionBuilder.withArgName("file")
				.hasArg()
				.withDescription("Configuration file path")
				.create("c"));
	}

	protected String[] configLocations;

	protected Object applicationContext;

	public SimpleSpringContainer(String[] configLocations) {
		super();
		this.configLocations = configLocations;
	}

	public Object createFileSystemXmlApplicationContext(String[] configLocations)
			throws Exception {
		Object applicationContext = new FileSystemXmlApplicationContext(
				configLocations, false);
		MethodUtils.invokeMethod(applicationContext, "refresh");
		return applicationContext;
	}
	
	public void start() throws Exception {
		this.applicationContext = createFileSystemXmlApplicationContext(this.configLocations);
		SpringContainerShutdownHook hook = new SpringContainerShutdownHook(this);
		Runtime.getRuntime().addShutdownHook(hook);
	}

    public void forceShutdown() {
		try {
			MethodUtils.invokeMethod(this.applicationContext, "stop");
			MethodUtils.invokeMethod(this.applicationContext, "close");
		} catch (Exception e) {
		}
	}

    public static void main(String[] args) {
		try {
			CommandLine line = new PosixParser().parse(OPTS, args);
			String configLocation = line.getOptionValue("c", DEFAULT_CONFIG_LOCATION);
			System.setProperty("app.home", new File(configLocation)
					.getParentFile().getParent());
			SimpleSpringContainer container = new SimpleSpringContainer(
					new String[] { configLocation });
			container.start();
		} catch (ParseException e) {
			printHelp();
		} catch (Exception e) {
			console.error("FATAL", e);
		}
	}

    private static void printHelp() {
		HelpFormatter hf = new HelpFormatter();
		String runProgram = "java " + SimpleSpringContainer.class.getName()
				+ " [options]";
		hf.printHelp(runProgram, OPTS);
		System.exit(0);
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

}
