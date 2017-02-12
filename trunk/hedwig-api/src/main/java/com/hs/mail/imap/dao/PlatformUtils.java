package com.hs.mail.imap.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * Utility functions for dealing with database platforms. 
 */
class PlatformUtils {

	static final String MYSQL       = "MySQL";
	static final String ORACLE      = "Oracle";

	/**
	 * Maps the sub-protocl part of a jdbc connection url to a OJB platform name.
	 */
	private Map<String, String> jdbcSubProtocolToPlatform = new HashMap<String, String>();

	/**
	 * Creates a new instance.
	 */
	PlatformUtils() {
		jdbcSubProtocolToPlatform.put("mysql", MYSQL);
		jdbcSubProtocolToPlatform.put("oracle:thin", ORACLE);
		jdbcSubProtocolToPlatform.put("oracle:oci8", ORACLE);
		jdbcSubProtocolToPlatform.put("oracle:dnldthin", ORACLE);
	}

	/**
	 * Tries to determine the database type for the given connection url. 
	 */
	String determineDatabaseType(DataSource dataSource) {
		String jdbcConnectionUrl = getURL(dataSource);
		if (jdbcConnectionUrl == null) {
			return null;
		}
		for (Entry<String, String> entry : jdbcSubProtocolToPlatform.entrySet()) {
			String subProtocol = "jdbc:" + entry.getKey() + ":";
			if (jdbcConnectionUrl.startsWith(subProtocol)) {
				return entry.getValue();
			}
		}
		return null;
	}

	String getURL(DataSource dataSource) {
		if (dataSource instanceof BasicDataSource) {
			return ((BasicDataSource) dataSource).getUrl();
		} else {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				DatabaseMetaData metaData = connection.getMetaData();
				return metaData.getURL();
			} catch (SQLException e) {
				return null;
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
						// we ignore this one
					}
				}
			}
		}
	}
	
}
