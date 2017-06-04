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

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * Convenient super class for JDBC-based data access objects.
 * 
 * @author Won Chul Doh
 * @since Feb 11, 2010
 *
 */
abstract class AbstractDao extends JdbcDaoSupport {
	
	/**
	 * Create an return a new instance of JdbcTemplate with given fetch size.
	 * Because a DAO that extends JdbcDaoSupport every call to getJdbcTemplate()
	 * returns the same shared JdbcTemplate instance with same fetch size value.
	 * 
	 * @param fetchSize
	 *            the new fetch size
	 * @return Ad hoc JdbcTemplate instance
	 */
	protected JdbcTemplate getJdbcTemplate(int fetchSize) {
		JdbcTemplate jt = new JdbcTemplate(getDataSource());
		jt.setFetchSize(fetchSize);
		return jt;
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a list of
	 * arguments to bind to the query, mapping a single result row to a Java
	 * object via a RowMapper.
	 * 
	 * @param sql
	 *            SQL query to execute
	 * @param args
	 *            arguments to bind to the query (leaving it to the
	 *            PreparedStatement to guess the corresponding SQL type)
	 * @param rowMapper
	 *            object that will map one object per row
	 * @return the single mapped object or null if the query does not return any
	 *         row
	 * @throws DataAccessException
	 *             if the query fails
	 */
	protected <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
		try {
			return getJdbcTemplate().queryForObject(sql, args, rowMapper);
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}
	}
	
	protected <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws DataAccessException {
		try {
			return getJdbcTemplate().queryForObject(sql, requiredType, args);
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}
	}	

	/**
	 * Query given SQL to create a prepared statement from SQL and a list of
	 * arguments to bind to the query, resulting in a long value. The query is
	 * expected to be a single row/single column query that results in a long
	 * value.
	 * 
	 * @param sql
	 *            SQL query to execute
	 * @param args
	 *            arguments to bind to the query (leaving it to the
	 *            PreparedStatement to guess the corresponding SQL type)
	 * @return the long value, or 0 in case of SQL NULL or if the query does not
	 *         return any row
	 * @throws DataAccessException
	 *             if query fails
	 */
	protected long queryForLong(String sql, Object[] args) throws DataAccessException {
		try {
			return getJdbcTemplate().queryForObject(sql, args, Long.class);
		} catch (EmptyResultDataAccessException ex) {
			return 0;
		}
	}

	/**
	 * Query given SQL to create a prepared statement from SQL and a list of
	 * arguments to bind to the query, resulting in an int value. The query is
	 * expected to be a single row/single column query that results in an int
	 * value.
	 * 
	 * @param sql
	 *            SQL query to execute
	 * @param args
	 *            arguments to bind to the query (leaving it to the
	 *            PreparedStatement to guess the corresponding SQL type)
	 * @return the int value, or 0 in case of SQL NULL or if the query does not
	 * @throws DataAccessException
	 *             if query fails
	 */
	protected int queryForInt(String sql, Object[] args) throws DataAccessException {
		try {
			return getJdbcTemplate().queryForObject(sql, args, Integer.class);
		} catch (EmptyResultDataAccessException ex) {
			return 0;
		}
	}
	
	protected int update(String[] sqls, Object... args) throws DataAccessException {
		int rows = 0;
		for (String sql : sqls) {
			rows += getJdbcTemplate().update(sql, args);
		}
		return rows;
	}

	protected static String escape(String param) {
		return StringUtils.replace(StringUtils.replace(param, "_", "\\_"), "%",
				"\\%");
	}
	
}
