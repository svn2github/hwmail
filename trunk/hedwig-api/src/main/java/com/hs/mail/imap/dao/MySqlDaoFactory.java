package com.hs.mail.imap.dao;


public class MySqlDaoFactory extends DaoFactory {

	public MySqlDaoFactory() {

		aclDao = new MySqlACLDao();
		
		mailboxDao = new MySqlMailboxDao();
		
		messageDao = new MySqlMessageDao();
		
		searchDao = new MySqlSearchDao();
		
		userDao = new MySqlUserDao();

	}
	
}
