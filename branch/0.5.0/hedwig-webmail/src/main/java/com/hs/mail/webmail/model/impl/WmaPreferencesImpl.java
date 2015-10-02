package com.hs.mail.webmail.model.impl;

import java.io.Serializable;
import java.util.Locale;

import com.hs.mail.webmail.config.Configuration;
import com.hs.mail.webmail.model.WmaPreferences;

public class WmaPreferencesImpl implements WmaPreferences, Serializable {

	private static final long serialVersionUID = 3577049312892807585L;

	// instance attributes
	protected String userIdentity;
	protected String username;

	// Auto features
	protected boolean autoQuote = true;
	protected boolean autoSign = false;
	protected boolean autoArchiveSent = false;

	protected Locale locale = Locale.getDefault();

	public WmaPreferencesImpl(String userIdentity) {
		setUserIdentity(userIdentity);
	}

	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isAutoQuote() {
		return autoQuote;
	}

	public void setAutoQuote(boolean doquote) {
		this.autoQuote = doquote;
	}

	public boolean isAutoSign() {
		return autoSign;
	}

	public void setAutoSign(boolean autoSign) {
		this.autoSign = autoSign;
	}

	public boolean isAutoArchiveSent() {
		return autoArchiveSent;
	}

	public void setAutoArchiveSent(boolean doarchive) {
		this.autoArchiveSent = doarchive;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getDraftFolder() {
		return Configuration.getMessage("preferences.draftfolder", locale);
	}

	public String getPersonalFolder() {
		return Configuration.getMessage("preferences.personalfolder", locale);
	}

	public String getSentMailArchive() {
		return Configuration.getMessage("preferences.sentmailarchive", locale);
	}

	public String getToSendFolder() {
		return Configuration.getMessage("preferences.tosendfolder", locale);
	}

	public String getTrashFolder() {
		return Configuration.getMessage("preferences.trashfolder", locale);
	}

}
