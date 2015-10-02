package com.hs.mail.webmail.dao.std;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;

import com.hs.mail.webmail.config.Configuration;
import com.hs.mail.webmail.dao.PreferencesDAO;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaPreferences;
import com.hs.mail.webmail.util.MD5;

public class SimplePreferencesDAO implements PreferencesDAO {

	@Override
	public WmaPreferences getPreferences(String identity) throws WmaException {
		File file = getFile(identity);
		InputStream input = null;
		try {
			input = new FileInputStream(file);
			return SerializationUtils.deserialize(input);
		} catch (FileNotFoundException e) {
			return null;
		} catch (Exception e) {
			throw new WmaException("wma.prefs.load").setException(e);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	@Override
	public void savePreferences(WmaPreferences prefs) throws WmaException {
		File file = getFile(prefs.getUserIdentity());
		OutputStream output = null;
		try {
			FileUtils.forceMkdir(file.getParentFile());
			output = new FileOutputStream(file);
			SerializationUtils.serialize(prefs, output);
		} catch (Exception e) {
			throw new WmaException("wma.prefs.save").setException(e);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}
	
	private static File getFile(String identity) {
		return new File(Configuration.getUserHome(identity), MD5.hash(identity));
	}

}
