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
package com.hs.mail.imap.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanCreationException;

/**
 * Static factory to conceal the automatic choice of the data access object
 * implementation class.
 * 
 * @author Won Chul Doh
 * @since Mar 8, 2010
 * 
 */
public class DaoFactory {

	protected static DaoFactory instance   = null;

	protected static ACLDao     aclDao     = null;
	protected static MailboxDao mailboxDao = null;
	protected static MessageDao	messageDao = null;
	protected static SearchDao  searchDao  = null;
	protected static UserDao    userDao    = null;

	public static DaoFactory getInstance(DataSource dataSource) {
		if (null == instance) {
			instance = newInstance(dataSource);
			setDataSource(dataSource);
		}
		return instance;
	}
	
	public static ACLDao getACLDao() {
		return aclDao;
	}

	public static MailboxDao getMailboxDao() {
		return mailboxDao;
	}

	public static MessageDao getMessageDao() {
		return messageDao;
	}

	public static SearchDao getSearchDao() {
		return searchDao;
	}

	public static UserDao getUserDao() {
		return userDao;
	}

	private static DaoFactory newInstance(DataSource dataSource) {
		String databaseType = new PlatformUtils().determineDatabaseType(dataSource);
		if (PlatformUtils.ORACLE.equals(databaseType)) {
			return new OracleDaoFactory();
		}
		if (PlatformUtils.MYSQL.equals(databaseType)) {
			return new MySqlDaoFactory();
		}
		throw new BeanCreationException("daoFactory", "Unsupported database type.");
	}

	private static void setDataSource(DataSource dataSource) {
		aclDao.setDataSource(dataSource);
		mailboxDao.setDataSource(dataSource);
		messageDao.setDataSource(dataSource);
		searchDao.setDataSource(dataSource);
		userDao.setDataSource(dataSource);
	}

}
