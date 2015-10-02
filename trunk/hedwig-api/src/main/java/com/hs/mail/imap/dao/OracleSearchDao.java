package com.hs.mail.imap.dao;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.hs.mail.imap.message.search.DateKey;

/**
 * 
 * @author Won Chul Doh
 * @since July 26, 2015
 *
 */
public class OracleSearchDao extends AnsiSearchDao {

	@Override
	protected SearchQuery getSearchQuery() {
		return sq;
	}

	static SearchQuery sq = new SearchQuery() {
		
		protected String dateQuery(String field, DateKey key) {
			return String.format("%s %s TO_DATE('%s', '%s')", field,
					getOp(key.getComparison()),
					DateFormatUtils.ISO_DATE_FORMAT.format(key.getDate()),
					DateFormatUtils.ISO_DATE_FORMAT.getPattern());
		}

	};
	
}
