package com.hs.mail.smtp.processor.hook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.hs.mail.smtp.message.MailAddress;

public class AccessTable {
	
    /**
     * The lists of patterns to be checked to limit spam
     */
    private Access[] accesstable;
    
    private Action defaultAction;

    AccessTable(File config) throws IOException {
    	this(config, Action.REJECT);
    }
    
	AccessTable(File config, Action defaultAction) throws IOException {
		this.defaultAction = defaultAction;
		readLines(config);
		Arrays.sort(accesstable, new Comparator<Access>() {
			@Override
			public int compare(Access o1, Access o2) {
				return o1.pattern.compareTo(o2.pattern);
			}
		});
	}
	
	private void readLines(File config) throws IOException {
		BufferedReader reader = null;
		try {
			InputStream in = new FileInputStream(config);
			reader = new BufferedReader(new InputStreamReader(in));
			List<Access> accesslist = new ArrayList<Access>();
			String line;
			while ((line = reader.readLine()) != null) {
				String str = line.trim();
				if (StringUtils.isNotBlank(str)) {
					char ch = line.charAt(0);
					if (ch != '#') {
						String[] tokens = StringUtils.split(line);
						String pattern = tokens[0];
						Action action = (tokens.length < 2)
								? defaultAction
								: Action.lookup(tokens[1]);
						accesslist.add(new Access(pattern.toLowerCase(), action));
					}
				}
			}
			this.accesstable = accesslist.toArray(new Access[accesslist.size()]);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
	
	public Action findAction(String... patterns) {
		for (String pattern : patterns) {
			int i = binarySearch(pattern);
			if (i >= 0) {
				return accesstable[i].action;
			}
		}
		return null;
	}
	
	public Action findAction(MailAddress address) {
		int i = -1;
		if (ArrayUtils.isNotEmpty(accesstable)) {
			if ((i = binarySearch(address.getMailbox())) < 0) {
				if ((i = binarySearch(address.getHost())) < 0) {
					i = binarySearch(address.getUser() + "@");
				}
			}
		}
		return (i >= 0) ? accesstable[i].action : null;
	}

	private int binarySearch(String pattern) {
		return Arrays.binarySearch(accesstable, StringUtils.lowerCase(pattern),
				new Comparator<Object>() {
					@Override
					public int compare(Object o1, Object o2) {
						return ((Access) o1).pattern.compareTo((String) o2);
					}
				});
	}
	
	public static enum Action {
		OK, REJECT, DISCARD, IGNORE;
		
		static public Action lookup(String s) {
			try {
				return valueOf(s);
			} catch (IllegalArgumentException e) {
				return IGNORE;
			}
		}
	}
	
	static class Access {
		String pattern;
		Action action;

		Access(String pattern, Action action) {
			this.pattern = pattern;
			this.action = action;
		}
	}
	
}
