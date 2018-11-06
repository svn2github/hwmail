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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.mail.Flags;
import javax.mail.Flags.Flag;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Mapping between message's system flags and message table's column name
 * 
 * @author Won Chul Doh
 * @since Apr 27, 2010
 * 
 */
public class FlagUtils {
	
	private static final Flags EMPTY_FLAGS = new Flags();

	private static Flags.Flag[] flagArray = { Flags.Flag.SEEN,
			Flags.Flag.ANSWERED, Flags.Flag.DELETED, Flags.Flag.FLAGGED,
			Flags.Flag.RECENT, Flags.Flag.DRAFT };

	private static String[] attrArray = { "seen_flag", "answered_flag", 
			"deleted_flag", "flagged_flag", "recent_flag", "draft_flag" };

	public static boolean isEmpty(Flags flags) {
		return (flags == null || EMPTY_FLAGS.equals(flags));
	}
	
	public static Flags removeUnauthorizedFlags(Flags flags, String rights) {
		boolean d = flags.contains(Flag.DELETED);
		boolean s = flags.contains(Flag.SEEN);
		
		if (d && rights.indexOf('t') == -1)
			flags.remove(Flag.DELETED);
		if (s && rights.indexOf('s') == -1)
			flags.remove(Flag.SEEN);
		if (d || s || rights.indexOf('w') != -1)
			return flags;
		else
			return null;
	}

	static String getParam(Flags flags, Flags.Flag flag) {
		return (flags != null && flags.contains(flag)) ? "Y" : "N";
	}

	static String getFlagColumnName(Flags.Flag flag) {
		for (int i = 0; i < flagArray.length; i++) {
			if (flag == flagArray[i]) {
				return attrArray[i];
			}
		}
		return null;
	}

	static Flags getFlags(ResultSet rs) throws SQLException {
		Flags flags = new Flags();
		for (int i = 0; i < attrArray.length; i++) {
			if ("Y".equals(rs.getString(attrArray[i]))) {
				flags.add(flagArray[i]);
			}
		}
		return flags;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static String buildParams(Flags.Flag[] flags, boolean replace, boolean set,
			List params) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < flagArray.length; i++) {
			boolean contains = ArrayUtils.contains(flags, flagArray[i]);
			if (contains || replace) {
				sb.append(attrArray[i]).append("= ?,");
				params.add(replace ? (contains ? "Y" : "N")
						: (set ? "Y" : "N"));
			}
		}
		return StringUtils.stripEnd(sb.toString(), ",");
	}
	
}
