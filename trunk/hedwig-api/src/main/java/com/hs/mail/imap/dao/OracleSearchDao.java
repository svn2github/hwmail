package com.hs.mail.imap.dao;

import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.hs.mail.container.config.Config;
import com.hs.mail.imap.message.search.DateKey;

/**
 * 
 * @author Won Chul Doh
 * @since July 26, 2015
 *
 */
public class OracleSearchDao extends AnsiSearchDao {

	@Override
	protected JdbcTemplate getAdHocJdbcTemplate() {
		return getJdbcTemplate(Config.getUIDListFetchSize());
	}

	@Override
	protected SearchQuery getSearchQuery() {
		return sq;
	}

	static SearchQuery sq = new SearchQuery() {
		
		protected String dateQuery(List<Object> args, String field,
				DateKey key) {
			args.add(DateFormatUtils.ISO_DATE_FORMAT.format(key.getDate()));
			args.add(DateFormatUtils.ISO_DATE_FORMAT.getPattern());
			return String.format("%s %s TO_DATE(?, ?)", field, getOp(key.getComparison()));
		}

	};
	
}
