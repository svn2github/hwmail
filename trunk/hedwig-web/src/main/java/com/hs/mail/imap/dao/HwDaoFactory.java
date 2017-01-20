package com.hs.mail.imap.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanCreationException;

public class HwDaoFactory extends DaoFactory {

	protected static HwUserDao hwUserDao = null;
	
	public static DaoFactory getInstance(String databaseType,
			DataSource dataSource) {
		if (null == instance) {
			instance = DaoFactory.getInstance(databaseType, dataSource);
			newInstance(databaseType);
			setDataSource(dataSource);
		}
		return instance;
	}
	
	public static HwUserDao getHwUserDao() {
		return hwUserDao;
	}
	
	private static void newInstance(String databaseType) {
		if ("Oracle".equalsIgnoreCase(databaseType)) {
			hwUserDao = new OracleHwUserDao();
		} else if ("MySQL".equalsIgnoreCase(databaseType)) {
			hwUserDao = new MySqlHwUserDao();
		} else {
			throw new BeanCreationException("Database type " + databaseType + " is not supported.");
		}
	}
	
	private static void setDataSource(DataSource dataSource) {
		hwUserDao.setDataSource(dataSource);
	}
	
}
