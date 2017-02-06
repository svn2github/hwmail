package com.hs.mail.imap.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanCreationException;

public class HwDaoFactory extends DaoFactory {

	protected static HwUserDao hwUserDao = null;
	
	public static DaoFactory getInstance(String jdbcConnectionUrl,
			DataSource dataSource) {
		if (null == instance) {
			instance = DaoFactory.getInstance(jdbcConnectionUrl, dataSource);
			newInstance(jdbcConnectionUrl);
			setDataSource(dataSource);
		}
		return instance;
	}
	
	public static HwUserDao getHwUserDao() {
		return hwUserDao;
	}
	
	private static void newInstance(String jdbcConnectionUrl) {
		String databaseType = new PlatformUtils().determineDatabaseType(jdbcConnectionUrl);
		if (PlatformUtils.ORACLE.equals(databaseType)) {
			hwUserDao = new OracleHwUserDao();
		} else if (PlatformUtils.MYSQL.equals(databaseType)) {
			hwUserDao = new MySqlHwUserDao();
		} else {
			throw new BeanCreationException("Database for '" + jdbcConnectionUrl + "' is not supported.");
		}
	}
	
	private static void setDataSource(DataSource dataSource) {
		hwUserDao.setDataSource(dataSource);
	}
	
}
