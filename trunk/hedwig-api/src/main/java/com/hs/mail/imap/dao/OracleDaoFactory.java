package com.hs.mail.imap.dao;


public class OracleDaoFactory extends DaoFactory {

	public OracleDaoFactory() {

		aclDao = new OracleACLDao();

		mailboxDao = new OracleMailboxDao();

		messageDao = new OracleMessageDao();

		searchDao = new OracleSearchDao();

		userDao = new OracleUserDao();
	}

}
