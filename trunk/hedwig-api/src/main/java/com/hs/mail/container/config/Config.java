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
package com.hs.mail.container.config;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.hs.mail.container.server.SSLContextFactory;
import com.hs.mail.imap.ImapConstants;
import com.hs.mail.imap.mailbox.Mailbox;
import com.hs.mail.util.InetAddressMatcher;

/**
 * Provides a number of properties to the server.
 * 
 * @author Won Chul Doh
 * @since Jun 3, 2010
 */
public class Config implements InitializingBean {
	
	static Logger logger = LoggerFactory.getLogger(Config.class);

	public static final String ZIPFILE_EXTENSION = "zip";
	public static final String MDCPOSTFIX = "__";
	
	private static final String DEF_CACHE_FIELDS = "Bcc,Cc,Date,From,In-Reply-To,Message-ID,Reply-To,Sender,Subject,To";

	private static DecimalFormat formatter = new DecimalFormat("0000000000");

	private static Properties properties;
	private static String authScheme;
	private static File dataDirectory;
	private static File tempDirectory;
	private static File spoolDirectory;
	private static Set<String> defaultCacheFields;
	private static long defaultQuota;
	private static int uidlistFetchSize = 1000;
	private static String[] domains;
	private static String[] mydestinations;
	private static String hostName;
	private static String helloName;
	private static String[] namespaces;
	private static InetAddressMatcher authorizedNetworks;
	private static String postmaster; 
	private static long maxMessageSize;
	private static int smtpdSoftErrorLimit;
	private static int smtpdHardErrorLimit;
	private static int smtpdErrorSleepTime;
	private static boolean saslAuthEnabled;
	private static SSLContext context;
	private static boolean initData = true;

	public void setInitData(boolean initData) {
		Config.initData = initData;
	}

	public static Properties getProperties() {
		return properties;
	}
	
	public void setProperties(Properties properties) {
		Config.properties = properties;
	}

	public static String getAuthScheme() {
		return authScheme;
	}

	public static File getDataDirectory() {
		return dataDirectory;
	}

	public static String getSubDirectory(Date date, long physmessageid) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return new StringBuilder()
						.append("mail")
						.append(File.separator)
						.append(cal.get(GregorianCalendar.YEAR))
						.append(File.separator)
						.append(cal.get(GregorianCalendar.MONTH) + 1)
						.append(File.separator)
						.append(cal.get(GregorianCalendar.DAY_OF_MONTH))
						.append(File.separator)
						.append(Integer.parseInt(
								formatter.format(physmessageid)
									.substring(5, 8)))
						.toString();
	}
	
	public static File getDataFile(Date date, long physmessageid) throws IOException {
		File directory = new File(dataDirectory, getSubDirectory(date, physmessageid));
		FileUtils.forceMkdir(directory);
		File zipped = new File(directory, Long.toString(physmessageid)
				+ FilenameUtils.EXTENSION_SEPARATOR_STR + ZIPFILE_EXTENSION);
		return (zipped.exists()) ? zipped : new File(directory, Long
				.toString(physmessageid));
	}
	
	public static File getMimeDescriptorFile(Date date, long physmessageid) {
		File directory = new File(dataDirectory, getSubDirectory(date, physmessageid));
		return new File(directory, Long.toString(physmessageid) + MDCPOSTFIX);
	}

	public static File getTempDirectory() {
		return tempDirectory;
	}

	public static File getSpoolDirectory() {
		return spoolDirectory;
	}
	
	public static void setSpoolDirectory(File dir) {
		spoolDirectory = dir;
	}

	public static File getSnapshotDirectory() {
		return new File(spoolDirectory, "snapshot");
	}
	
	public static Set<String> getDefaultCacheFields() {
		return defaultCacheFields;
	}

	public static long getDefaultQuota() {
		return defaultQuota;
	}

	public static int getUIDListFetchSize() {
		return uidlistFetchSize;
	}

	public static String[] getDomains() {
		return domains;
	}
	
	public static String getDefaultDomain() {
		return domains[0];
	}
	
	public static boolean isLocal(String domain) {
		return ArrayUtils.contains(domains, domain)
				|| ArrayUtils.contains(mydestinations, domain);
	}
	
	public static boolean isMyDestination(String destination) {
		return ArrayUtils.contains(mydestinations, destination);
	}

	public static String getHostName() {
		return hostName;
	}

	public static String getHelloName() {
		return helloName;
	}
	
	public static String[] getSharedNamespaces() {
		return namespaces;
	}
	
	public static InetAddressMatcher getAuthorizedNetworks() {
		return authorizedNetworks;
	}

	public static String getPostmaster() {
		return postmaster;
	}
	
	public static long getMaxMessageSize() {
		return maxMessageSize;
	}
	
	public static int getSmtpdSoftErrorLimit() {
		return smtpdSoftErrorLimit;
	}

	public static int getSmtpdHardErrorLimit() {
		return smtpdHardErrorLimit;
	}

	public static int getSmtpdErrorSleepTime() {
		return smtpdErrorSleepTime;
	}

	public static boolean isSaslAuthEnabled() {
		return saslAuthEnabled;
	}
	
	public static SSLContext getSSLContext() {
		return context;
	}

	public static String getProperty(String key, String defaultValue) {
		return replaceByProperties(properties.getProperty(key, defaultValue));
	}
	
	public static File getFileProperty(String key, String file)
			throws IOException {
		return new File(getProperty(key, file));
	}

	public static long getNumberProperty(String key, long defaultValue) {
		try {
			String value = getProperty(key, Long.toString(defaultValue));
			return Long.valueOf(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public static boolean getBooleanProperty(String key, boolean defaultValue) {
		String value = getProperty(key, Boolean.toString(defaultValue));
		return "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
	}

	public void afterPropertiesSet() throws Exception {
		dataDirectory = getFileProperty("data_directory", "${app.home}"
				+ File.separator + "data");
		logger.info("Data directory:  {}", dataDirectory.getCanonicalPath());
		
		tempDirectory = getFileProperty("temp_directory", "${app.home}"
				+ File.separator + "temp");
		logger.info("Temp directory:  {}", tempDirectory.getCanonicalPath());
		
		spoolDirectory = getFileProperty("queue_directory", "${app.home}"
				+ File.separator + "spool");
		logger.info("Spool directory: {}", spoolDirectory.getCanonicalPath());
	
		authScheme = getProperty("auth_scheme", null);
		logger.info("Authentication scheme is {}",
				((authScheme != null) ? authScheme : "not specified"));
		
		/**
		 * IMAP related parameters
		 */
		String fields = getProperty("default_cache_fields", DEF_CACHE_FIELDS);
		defaultCacheFields = buildDefaultCacheFields(StringUtils.split(fields, ','));
		
		long quota = getNumberProperty("default_quota", 0);
		defaultQuota = quota * 1024 * 1024;
		logger.info("Default quota is {}MB", quota);
		
		uidlistFetchSize = (int) getNumberProperty("uidlist_fetch_size", 1000);
		
		hostName = getProperty("myhostname", null);
		if (null == hostName) {
			try {
				hostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException ex) {
				hostName = "localhost";
			}
		}
		logger.info("Local host is {}", hostName);

		String domain = getProperty("mydomain", null);
		if (null == domain) {
			domains = new String[] { StringUtils.substringAfter(hostName, ".") };
		} else {
			domains = StringUtils.stripAll(StringUtils.split(domain, ","));
		}
		for (int i = 0; i < domains.length; i++) {
			logger.info("Handling mail for {}", domains[i]);
		}
		
		String destination = getProperty("mydestination", null);
		if (null == destination) {
			mydestinations = "localhost".equals(hostName)
					? new String[] { hostName, "localhost." + domains[0] }
					: new String[] { hostName, "localhost." + domains[0], "localhost" };
		} else {
			mydestinations = StringUtils.stripAll(StringUtils.split(destination, ","));
		}

		namespaces = StringUtils.split(getProperty("namespaces", null), ",");
		if (namespaces != null) {
			for (int i = 0; i < namespaces.length; i++) {
				namespaces[i] = StringUtils.prependIfMissing(namespaces[i],
						ImapConstants.SHARED_PREFIX);
				namespaces[i] = StringUtils.appendIfMissing(namespaces[i],
						Mailbox.folderSeparator);
			}
		}

		/*
		 * SMTP related parameters
		 */
		String networks = getProperty("mynetworks", null);
		if (networks == null) {
			networks = InetAddressMatcher.getCidrSignature();
		}
		authorizedNetworks = new InetAddressMatcher(networks);
		logger.info("SMTP relaying is allowded to {}", networks);
		
		smtpdSoftErrorLimit = (int) getNumberProperty("smtpd_soft_error_limit", 10);
		smtpdHardErrorLimit = (int) getNumberProperty("smtpd_hard_error_limit", 20);
		smtpdErrorSleepTime = (int) getNumberProperty("smtpd_error_sleep_time", 1);
		
		helloName = getProperty("smtp_helo_name", hostName);
		
		postmaster = getProperty("postmaster", "postmaster");
		if (postmaster.indexOf('@') < 0) {
			String domainName = null;
			for (int i = 0; i < domains.length; i++) {
				String serverName = domains[i].toLowerCase(Locale.US);
				if (!"localhost".equals(serverName)) {
					domainName = serverName;
				}
			}
			postmaster = postmaster + "@"
					+ (domainName != null ? domainName : hostName);
		}
		logger.info("Postmaster address is {}", postmaster);
		
		maxMessageSize = getNumberProperty("message_size_limit", 10240000);
		logger.info("Maximum message size is {}", maxMessageSize);
		
		saslAuthEnabled = getBooleanProperty("smtp_sasl_auth_enable", false);

		if (initData) {
			FileUtils.forceMkdir(dataDirectory);
			FileUtils.forceMkdir(tempDirectory);
			FileUtils.forceMkdir(spoolDirectory);
			FileUtils.forceMkdir(getSnapshotDirectory());
			buildSSLContext();
			verbosePrint();
		}
	}
	
	private void verbosePrint() {
		try {
			System.out.println("Using HEDWIG_HOME: " + System.getProperty("app.home"));
			System.out.println("Using JAVA_HOME:   " + SystemUtils.JAVA_HOME);
			System.out.println();
		} catch (Exception ignore) {
		}
	}
	
	private Set<String> buildDefaultCacheFields(String[] fields) {
		Set<String> result = new HashSet<String>();
		for (int i = 0; i < fields.length; i++) {
			result.add(fields[i]);
		}
		return result;
	}
	
	private void buildSSLContext() throws IOException {
		if (context == null) {
			String keyStore = getProperty("tls_keystore", null);
			if (keyStore != null) {
				String keyStorePassword = getProperty("tls_keypass", "");
				String certificatePassword = getProperty("tls_storepass", "");
				context = SSLContextFactory.createContext(keyStore,
						keyStorePassword, certificatePassword);
			}
		}
	}
	
	public static String replaceByProperties(String source) {
		if (null == source)
			return null;
		int end;
		int start = source.indexOf("${");
		while (-1 != start) {
			end = source.indexOf("}", start + 2);
			if (end > 0) {
				String propName = source.substring(start + 2, end);
				String propValue = System.getProperty(propName);
				if (null != propValue) {
					int propValueLen = propValue.length();
					source = new StringBuffer(source.length())
							.append(source.substring(0, start))
							.append(propValue)
							.append(source.substring(end + 1)).toString();
					start = source.indexOf("${", start + propValueLen);
				} else {
					start = source.indexOf("${", end + 1);
				}
			} else {
				break;
			}
		}
		return source;
	}
	
}
