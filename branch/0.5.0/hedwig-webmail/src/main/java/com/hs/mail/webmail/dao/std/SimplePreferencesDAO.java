package com.hs.mail.webmail.dao.std;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;

import org.apache.commons.io.IOUtils;

import com.hs.mail.webmail.config.Configuration;
import com.hs.mail.webmail.dao.PreferencesDAO;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaPreferences;
import com.hs.mail.webmail.util.MD5;

public class SimplePreferencesDAO implements PreferencesDAO {

	public WmaPreferences getPreferences(String identity) throws WmaException {
		String filename = getFilename(identity);
		ObjectInputStream in = null;
		try {
			FileInputStream fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			return (WmaPreferences) in.readObject();
		} catch (FileNotFoundException e) {
			return null;
		} catch (Exception e) {
			throw new WmaException("wma.plugin.std").setException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	private static String getFilename(String identity) {
		return Configuration.getProperty("wma.data.path") + File.separator
				+ MD5.hash(identity);
	}
	
}
