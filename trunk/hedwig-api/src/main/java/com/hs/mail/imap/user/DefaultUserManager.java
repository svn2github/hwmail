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
package com.hs.mail.imap.user;

import java.io.File;
import java.util.List;

import javax.mail.Quota;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.CredentialException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import com.hs.mail.container.config.Config;
import com.hs.mail.imap.dao.DaoFactory;
import com.hs.mail.security.login.BasicCallbackHandler;
import com.hs.mail.smtp.message.MailAddress;

/**
 * 
 * @author Won Chul Doh
 * @since Jun 24, 2010
 *
 */
public class DefaultUserManager implements UserManager {

	private TransactionTemplate transactionTemplate;
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		Assert.notNull(transactionManager, "The 'transactionManager' argument must not be null.");
		this.transactionTemplate = new TransactionTemplate(transactionManager);
	}
	
	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	
	/**
	 * Authenticate the given user against the given password. When
	 * authenticated, the ID of the user will be supplied.
	 * 
	 * @param username
	 *            user name
	 * @param password
	 *            password supplied
	 * @return id of the user when authenticated
	 * @throws LoginException
	 *             when the user does not exist or not authenticated
	 */
	public long login(String username, String password) throws LoginException {
		String address = toAddress(username); 
		User user = DaoFactory.getUserDao().getUserByAddress(address);
		if (user == null) {
			throw new AccountNotFoundException("Account for " + username
					+ " not found");
		}
		if (Config.getAuthScheme() != null) {
			CallbackHandler callbackHandler = new BasicCallbackHandler(address,
					password.toCharArray());
			LoginContext lc = new LoginContext(Config.getAuthScheme(),
					callbackHandler);
			lc.login();
		} else {
			if (!password.equals(user.getPassword())) {
				throw new CredentialException("Incorrect password for "
						+ username);
			}
		}
		return user.getID();
	}
	
	public long getUserID(String address) {
		return DaoFactory.getUserDao().getUserID(address);
	}
	
	public User getUserByAddress(String address) {
		return DaoFactory.getUserDao().getUserByAddress(address);
	}

	public List<Alias> expandAlias(String alias) {
		return DaoFactory.getUserDao().expandAlias(alias);
	}
	
	public Quota getQuota(long ownerID, long mailboxID, String quotaRoot) {
		Quota quota = DaoFactory.getUserDao().getQuota(ownerID, mailboxID,
				quotaRoot);
		if (quota.resources[0].limit == 0) {
			quota.resources[0].limit = Config.getDefaultQuota();
		}
		return quota;
	}

	public void setQuota(final long ownerID, final Quota quota) {
		getTransactionTemplate().execute(
				new TransactionCallbackWithoutResult() {
					public void doInTransactionWithoutResult(TransactionStatus status) {
						try {
							DaoFactory.getUserDao().setQuota(ownerID, quota);
						} catch (DataAccessException ex) {
							status.setRollbackOnly();
							throw ex;
						}
					}
				});
	}
	
	public File getUserHome(MailAddress user) {
		String str = user.getUser();
		StringBuilder sb = new StringBuilder(
				(user.getHost() != null) ? user.getHost() : Config.getDefaultDomain())
				.append(File.separator)
				.append("users")
				.append(File.separator)
				.append(str.charAt(0))
				.append(str.charAt(str.length() - 1))
				.append(File.separator)
				.append(str);
		return new File(Config.getDataDirectory(), sb.toString());
	}
	
	public String toAddress(String user) {
		if (user.indexOf('@') != -1)
			return user;
		else
			return new StringBuffer(user)
							.append('@')
							.append(Config.getDefaultDomain())
							.toString();
	}

}
