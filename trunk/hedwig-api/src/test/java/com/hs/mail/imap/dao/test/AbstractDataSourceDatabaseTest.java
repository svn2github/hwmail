package com.hs.mail.imap.dao.test;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang3.ArrayUtils;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;


public abstract class AbstractDataSourceDatabaseTest {
	
	@Autowired
	DataSource dataSource;

	protected void setUpDatabase(String[] resources) throws Exception {
		if (ArrayUtils.isNotEmpty(resources)) {
			IDatabaseConnection connection = getConnection(dataSource);
			try {
				for (String resource : resources) {
					DatabaseOperation.CLEAN_INSERT.execute(
							connection,
							new XmlDataSet(new ClassPathResource(resource)
									.getInputStream()));
				}
			} finally {
				releaseConnection(connection);
			}
		}
	}
	
	protected IDatabaseConnection getConnection(DataSource dataSource)
			throws SQLException {
		return new DatabaseDataSourceConnection(dataSource);
	}
	
	protected void releaseConnection(IDatabaseConnection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
			}
		}
	}
	
}
