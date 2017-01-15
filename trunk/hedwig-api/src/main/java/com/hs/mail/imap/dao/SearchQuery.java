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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hs.mail.imap.message.search.ComparisonKey;
import com.hs.mail.imap.message.search.CompositeKey;
import com.hs.mail.imap.message.search.DateKey;
import com.hs.mail.imap.message.search.FlagKey;
import com.hs.mail.imap.message.search.FromStringKey;
import com.hs.mail.imap.message.search.IntegerComparisonKey;
import com.hs.mail.imap.message.search.InternalDateKey;
import com.hs.mail.imap.message.search.KeywordKey;
import com.hs.mail.imap.message.search.SearchKey;
import com.hs.mail.imap.message.search.SentDateKey;
import com.hs.mail.imap.message.search.SizeKey;
import com.hs.mail.imap.message.search.StringKey;
import com.hs.mail.imap.message.search.SubjectKey;

/**
 * Helper class which builds a search query.
 * 
 * @author Won Chul Doh
 * @since Apr 8, 2010
 *
 */
abstract class SearchQuery {

	/**
	 * An individual query which contains SQL and its parameters   
	 *
	 * @since Jan 17, 2017
	 */
	public class Query {
	/** SQL query to execute */
	public String sql;
	/** arguments to bind to the query */
	public Object[] args;

	/**
	 * Construct a Query object with the given SQL, and arguments.
	 * 
	 * @param sql
	 *            the SQL query
	 * @param args
	 *            the binding arguments
	 */
	public Query(String sql, Object[] args) {
		this.sql = sql;
		this.args = args;
	}
	}	

	Query toQuery(long mailboxID, CompositeKey key, boolean and) {
		StringBuilder sql = new StringBuilder(
				"SELECT messageid FROM hw_message m, hw_physmessage p WHERE m.mailboxid = ? AND m.physmessageid = p.physmessageid");
		List<SearchKey> keys = key.getSearchKeys();
		List<Object> args = new ArrayList<Object>();
		args.add(mailboxID);
		String c = condition(args, keys, and);
		if (c != null) {
			sql.append(" AND ").append(c);
		}
		return new Query(sql.toString(), args.toArray());
	}
	
	Query toQuery(long mailboxID, String headername, boolean emptyValue) {
		if (emptyValue) {
			return new Query(
					"SELECT messageid FROM hw_message m JOIN hw_physmessage p ON m.physmessageid = p.physmessageid JOIN hw_headervalue v ON v.physmessageid = p.physmessageid JOIN hw_headername n ON v.headernameid = n.headernameid WHERE mailboxid = ? AND headername = ?",
					new Object[]{mailboxID, headername});
		} else {
			return new Query(
					"SELECT m.messageid, v.headervalue FROM hw_message m JOIN hw_physmessage p ON m.physmessageid = p.physmessageid JOIN hw_headervalue v ON v.physmessageid = p.physmessageid JOIN hw_headername n ON v.headernameid = n.headernameid WHERE mailboxid = ? AND headername = ?",
					new Object[]{mailboxID, headername});
		}
	}

	Query toQuery(long mailboxID, KeywordKey key) {
		return new Query(
				"SELECT m.messageid FROM hw_message m, hw_keyword k WHERE m.mailboxid = ? AND m.messageid = k.messageid AND k.keyword = ?",
				new Object[]{mailboxID, key.getPattern()});
	}

	private String condition(List<Object> args, List<SearchKey> keys,
			boolean and) {
		String[] array = new String[keys.size()];
		String s = null;
		int i = 0;
		for (SearchKey k : keys) {
			if ((s = toQuery(args, k)) != null) {
				array[i++] = s;
			}
		}
		if (i == 0) 
			return null;
		if (i == 1)
			return array[0];
		if (and)
			return StringUtils.join(array, " AND ", 0, i); 
		else
			return new StringBuilder("(")
					.append(StringUtils.join(array, " OR ", 0, i))
					.append(")")
					.toString();
	}

	private String toQuery(List<Object> args, SearchKey key) {
		if (key instanceof FlagKey) {
			return flagQuery(args, (FlagKey) key);
		} else if (key instanceof FromStringKey) {
			return stringQuery(args, "fromaddr", (FromStringKey) key);
		} else if (key instanceof InternalDateKey) {
			return dateQuery(args, "internaldate", (InternalDateKey) key);
		} else if (key instanceof SentDateKey) {
			return dateQuery(args, "sentdate", (SentDateKey) key);
		} else if (key instanceof SizeKey) {
			return numberQuery(args, "rfcsize", (SizeKey) key);
		} else if (key instanceof SubjectKey) {
			return stringQuery(args, "subject", ((SubjectKey) key));
		} else { // AllKey
			return null;
		}
	}

	private String flagQuery(List<Object> args, FlagKey key) {
		String s = FlagUtils.getFlagColumnName(key.getFlag());
		if (StringUtils.isNotEmpty(s)) {
			args.add(key.isSet() ? "Y" : "N");
			return new StringBuilder(s).append("= ?").toString();
		} else {
			return null;
		}
	}
	
	private String stringQuery(List<Object> args, String field, StringKey key) {
		args.add("%" + key.getPattern() + "%");
		return String.format("%s LIKE ?", field, key.getPattern());
	}
	
	private String numberQuery(List<Object> args, String field, IntegerComparisonKey key) {
		args.add(key.getNumber());
		return String.format("%s %s ?", field, getOp(key.getComparison()));
	}

	abstract protected String dateQuery(List<Object> args, String field, DateKey key);
	
	protected String getOp(int comparison) {
		switch (comparison) {
		case ComparisonKey.LE:
			return "<=";
		case ComparisonKey.LT:
			return "<";
		case ComparisonKey.EQ:
			return "=";
		case ComparisonKey.NE:
			return "!=";
		case ComparisonKey.GT:
			return ">";
		case ComparisonKey.GE:
		default:
			return ">=";
		}
	}
	
}
