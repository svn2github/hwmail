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
 * 
 * @author Won Chul Doh
 * @since Apr 8, 2010
 *
 */
abstract class SearchQuery {
	
	String toQuery(long mailboxID, CompositeKey key, boolean and) {
		StringBuilder sql = new StringBuilder(
				"SELECT messageid FROM hw_message m, hw_physmessage p WHERE m.mailboxid = ")
				.append(mailboxID).append(
						" AND m.physmessageid = p.physmessageid");
		List<SearchKey> keys = key.getSearchKeys();
		String c = condition(keys, and);
		if (c != null) {
			sql.append(" AND ").append(c);
		}
		return sql.toString();
	}
	
	String toQuery(long mailboxID, String headername, boolean emptyValue) {
		if (emptyValue) {
			return String
					.format("SELECT messageid FROM hw_message m JOIN hw_physmessage p ON m.physmessageid = p.physmessageid JOIN hw_headervalue v ON v.physmessageid = p.physmessageid JOIN hw_headername n ON v.headernameid = n.headernameid WHERE mailboxid = %d AND headername = '%s'",
							mailboxID, headername);
		} else {
			return String
					.format("SELECT m.messageid, v.headervalue FROM hw_message m JOIN hw_physmessage p ON m.physmessageid = p.physmessageid JOIN hw_headervalue v ON v.physmessageid = p.physmessageid JOIN hw_headername n ON v.headernameid = n.headernameid WHERE mailboxid = %d AND headername = '%s'",
							mailboxID, headername);
		}
	}

	String toQuery(long mailboxID, KeywordKey key) {
		return String
				.format(
						"SELECT m.messageid FROM hw_message m, hw_keyword k WHERE m.mailboxid = %d AND m.messageid = k.messageid AND k.keyword = '%s'",
						mailboxID, key.getPattern());
	}

	private String condition(List<SearchKey> keys, boolean and) {
		String[] array = new String[keys.size()];
		String s = null;
		int i = 0;
		for (SearchKey k : keys) {
			if ((s = toQuery(k)) != null) {
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

	private String toQuery(SearchKey key) {
		if (key instanceof FlagKey) {
			return flagQuery((FlagKey) key);
		} else if (key instanceof FromStringKey) {
			return stringQuery("fromaddr", (FromStringKey) key);
		} else if (key instanceof InternalDateKey) {
			return dateQuery("internaldate", (InternalDateKey) key);
		} else if (key instanceof SentDateKey) {
			return dateQuery("sentdate", (SentDateKey) key);
		} else if (key instanceof SizeKey) {
			return numberQuery("rfcsize", (SizeKey) key);
		} else if (key instanceof SubjectKey) {
			return stringQuery("subject", ((SubjectKey) key));
		} else { // AllKey
			return null;
		}
	}

	private String flagQuery(FlagKey key) {
		String s = FlagUtils.getFlagColumnName(key.getFlag());
		if (StringUtils.isNotEmpty(s)) {
			String v = key.isSet() ? "Y" : "N";
			return new StringBuilder(s).append("='").append(v).append("'")
					.toString();
		} else {
			return null;
		}
	}
	
	private String stringQuery(String field, StringKey key) {
		return String.format("%s LIKE '%%%s%%'", field, key.getPattern());
	}
	
	private String numberQuery(String field, IntegerComparisonKey key) {
		return String.format("%s %s %d", field, getOp(key.getComparison()), key
				.getNumber());
	}

	abstract protected String dateQuery(String field, DateKey key);
	
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
