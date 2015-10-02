package com.hs.mail.web.tools;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.hs.mail.container.config.Config;

public class BodyStructureBuilder {
	
	private File dataDirectory;
	
	public void setDataDirectory(File dataDirectory) {
		this.dataDirectory = dataDirectory;
	}

	public int build() {
		if (dataDirectory == null) {
			setDataDirectory(Config.getDataDirectory());
		}
		Stack<File> stack = new Stack<File>();
		stack.push(dataDirectory);
		return build(stack);
	}
	
	private int build(Stack<File> stack) {
		int count = 0;
		while (!stack.isEmpty()) {
			File dir = stack.pop();
			File[] found = dir.listFiles();
			if (ArrayUtils.isNotEmpty(found)) {
				for (File file : found) {
					if (file.isDirectory()) {
						stack.push(file);
					} else {
						if (isMimeDescriptorFile(file)) {
							try {
								FileUtils.forceDelete(file);
								System.out.println(file.getAbsolutePath());
								count++;
							} catch (IOException e) {
							}
						}
					}
				}
			}
		}
		return count;
	}
	
	private static boolean isMimeDescriptorFile(File file) {
		String name = file.getName();
		if (name.endsWith("__") && StringUtils.isNumeric(name.substring(0, name.length() - 2))) {
			return true;
		}
		return false;
	}
	
}
