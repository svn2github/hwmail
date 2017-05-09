package com.hs.mail.adm;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hs.mail.adm.command.Command;
import com.hs.mail.adm.command.CommandFactory;
import com.hs.mail.adm.command.CommandHelp;
import com.hs.mail.adm.command.HelpCommand;

public class Main {

	private static final String CONFIG_PATH = "applicationContext.xml";

	public static void main(String[] args) {
		setAppHome();

		configureLog();

		List<String> tokens = new LinkedList<String>(Arrays.asList(args));
		try {
			final Main app = getMain();
			app.runCommand(tokens);
			System.exit(0);
		} catch (Exception e) {
			System.err.println("Failed to execute main task. Reason: " + e);
			System.exit(1);
		}
	}

	private void runCommand(List<String> tokens) throws Exception {
		if (tokens.size() > 0) {
			String token = tokens.remove(0);
			if (!CommandHelp.isPrintHelp(token)) {
				Command command = CommandFactory.getCommand(token);
				if (command != null) {
					command.execute(tokens);
					return;
				}
			}
		}
		new HelpCommand().execute(tokens);
	}
	
	private static void setAppHome() {
		if (System.getProperty("app.home") == null) {
			File appHome = null;
			URL url = Main.class.getClassLoader().getResource("com/hs/mail/adm/Main.class");
			if (url != null) {
				try {
					JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
					url = jarConnection.getJarFileURL();
					URI baseURI = new URI(url.toString()).resolve("..");
					appHome = new File(baseURI).getCanonicalFile();
					System.setProperty("app.home", appHome.getAbsolutePath());
				} catch (Exception ignored) {
				}
			}

			if (appHome == null) {
				appHome = new File("../.");
				System.setProperty("app.home", appHome.getAbsolutePath());
			}
		}
	}

	private static void configureLog() {
		if (System.getProperty("log4j.configuration") != null) {
			PropertyConfigurator
					.configure(System.getProperty("log4j.configuration"));
		} else {
			BasicConfigurator.configure();
		}
	}

	@SuppressWarnings("resource")
	private static Main getMain() {
		final ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG_PATH);
		((AbstractApplicationContext) context).registerShutdownHook();
		return context.getBean(Main.class);
	}
	
}
