package com.hs.mail.webmail.dao;

import java.util.List;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaPreferences;
import com.hs.mail.webmail.model.impl.WmaFetchAccount;

public interface PreferencesDAO {
	
	/**
	 * Loads the given user's preferences from the persistent store and returns
	 * them as a <tt>WmaPreferencesImpl</tt> instance.
	 * <p>
	 * If preferences for the given user do not exist on the store, the method
	 * returns <tt>null</tt>.
	 * 
	 * @param identity
	 *            a <tt>String</tt> representing the user (in a unique way).
	 * 
	 * @return the <tt>WmaPreferences</tt> instance.
	 * 
	 * @see com.hs.wmail.webmail.model.WmaPreferences#getUserIdentity()
	 */
	WmaPreferences getPreferences(String identity) throws WmaException;

	void savePreferences(WmaPreferences prefs) throws WmaException;

	List<WmaFetchAccount> getFetchAccounts(String identity) throws WmaException;

	void saveFetchAccounts(String identity, List<WmaFetchAccount> accounts)
			throws WmaException;

}
