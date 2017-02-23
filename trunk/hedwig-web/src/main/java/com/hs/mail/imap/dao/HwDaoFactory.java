package com.hs.mail.imap.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanCreationException;

public class HwDaoFactory extends DaoFactory {

	protected static HwUserDao hwUserDao = null;
	
	public static DaoFactory getInstance(DataSource dataSource) {
		if (null == instance) {
			instance = DaoFactory.getInstance(dataSource);
			newInstance(dataSource);
			setDataSource(dataSource);
		}
		return instance;
	}
	
	public static HwUserDao getHwUserDao() {
		return hwUserDao;
	}
	
	private static void newInstance(DataSource dataSource) {
		String databaseType = new PlatformUtils().determineDatabaseType(dataSource);
		if (PlatformUtils.ORACLE.equals(databaseType)) {
			hwUserDao = new OracleHwUserDao();
		} else if (PlatformUtils.MYSQL.equals(databaseType)) {
			hwUserDao = new MySqlHwUserDao();
		} else {
			throw new BeanCreationException("daoFactory", "Unsupported database type.");
		}
	}
	
	private static void setDataSource(DataSource dataSource) {
		hwUserDao.setDataSource(dataSource);
	}
	
}
