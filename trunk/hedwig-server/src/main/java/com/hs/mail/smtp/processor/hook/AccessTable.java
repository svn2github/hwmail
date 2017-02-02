package com.hs.mail.smtp.processor.hook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.hs.mail.smtp.message.MailAddress;

public class AccessTable {
	
	private static final String OK = "OK";
	private static final String REJECT = "REJECT";
	
    /**
     * The lists of rbl servers to be checked to limit spam
     */
    private String[] whitelist;
    private String[] blacklist;
	
	AccessTable(File config) throws IOException {
		readLines(config);
		Arrays.sort(whitelist);
		Arrays.sort(blacklist);
	}
	
	private void readLines(File config) throws IOException {
		BufferedReader reader = null;
		try {
			InputStream in = new FileInputStream(config);
			reader = new BufferedReader(new InputStreamReader(in));
			List<String> whitelist = new ArrayList<String>();
			List<String> blacklist = new ArrayList<String>();
			String line;
			while ((line = reader.readLine()) != null) {
				String str = line.trim();
				if (StringUtils.isNotBlank(str)) {
					char ch = line.charAt(0);
					if (ch != '#') {
						String[] tokens = StringUtils.split(line);
						String address = tokens[0];
						String action = (tokens.length < 2) ? REJECT : tokens[1];
						if (OK.equalsIgnoreCase(action)) {
							whitelist.add(address);
						} else if (REJECT.equalsIgnoreCase(action)) {
							blacklist.add(address);
						}
					}
				}
			}
			this.whitelist = whitelist.toArray(new String[whitelist.size()]);
			this.blacklist = blacklist.toArray(new String[blacklist.size()]);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
	
	public boolean isRestricted(MailAddress address) {
		if (ArrayUtils.isNotEmpty(whitelist)) {
			if (Arrays.binarySearch(whitelist, address.getMailbox()) >= 0
					|| Arrays.binarySearch(whitelist, address.getHost()) >= 0
					|| Arrays.binarySearch(whitelist, address.getUser() + "@") >= 0) {
				return false;
			}
		}
		if (ArrayUtils.isNotEmpty(blacklist)) {
			if (Arrays.binarySearch(blacklist, address.getMailbox()) >= 0
					|| Arrays.binarySearch(blacklist, address.getHost()) >= 0
					|| Arrays.binarySearch(blacklist, address.getUser() + "@") >= 0) {
				return true;
			}
		}
		return false;
	}
    
}
