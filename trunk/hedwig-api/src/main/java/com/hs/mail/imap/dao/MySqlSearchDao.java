package com.hs.mail.imap.dao;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.hs.mail.imap.message.search.DateKey;

/**
 * 
 * @author Won Chul Doh
 * @since Apr 5, 2010
 *
 */
public class MySqlSearchDao extends AnsiSearchDao {

	@Override
	protected SearchQuery getSearchQuery() {
		return sq;
	}

	static SearchQuery sq = new SearchQuery() {

		protected String dateQuery(String field, DateKey key) {
			return String.format("DATE(%s) %s DATE('%s')", field, getOp(key
					.getComparison()), DateFormatUtils.ISO_DATE_FORMAT.format(key
					.getDate()));
		}

	};
	
}
