package com.hs.mail.imap.dao;

import static org.mockito.Mockito.mock;

public class MockDaoFactory extends DaoFactory {

	public static DaoFactory getInstance() {
		if (null == instance) {
			instance = new MockDaoFactory();
		}
		return instance;
	}

	private MockDaoFactory() {
		aclDao     = mock(ACLDao.class);
		mailboxDao = mock(MailboxDao.class);
		messageDao = mock(MessageDao.class);
		searchDao  = mock(SearchDao.class);
		userDao    = mock(UserDao.class);
	}

}
