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
import javax.security.auth.login.LoginException;

import com.hs.mail.smtp.message.MailAddress;

/**
 * 
 * @author Won Chul Doh
 * @since Jun 24, 2010
 *
 */
public interface UserManager {

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
	public long login(String username, String password) throws LoginException;
	
	public long getUserID(String address);
	
	public User getUserByAddress(String address);

	public List<Alias> expandAlias(String alias);
	
	public Quota getQuota(long ownerID, long mailboxID, String quotaRoot);

	public void setQuota(final long ownerID, final Quota quota);
	
	public File getUserHome(MailAddress user);

	public String toAddress(String user);
	
}
