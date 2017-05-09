package com.hs.mail.adm.search;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.search.ComparisonTerm;

import org.apache.commons.lang3.StringUtils;

import com.hs.mail.imap.message.SequenceRange;
import com.hs.mail.imap.message.search.AllKey;
import com.hs.mail.imap.message.search.AndKey;
import com.hs.mail.imap.message.search.FlagKey;
import com.hs.mail.imap.message.search.FromStringKey;
import com.hs.mail.imap.message.search.HeaderKey;
import com.hs.mail.imap.message.search.InternalDateKey;
import com.hs.mail.imap.message.search.KeywordKey;
import com.hs.mail.imap.message.search.NotKey;
import com.hs.mail.imap.message.search.OrKey;
import com.hs.mail.imap.message.search.RecipientStringKey;
import com.hs.mail.imap.message.search.SearchKey;
import com.hs.mail.imap.message.search.SearchKeyList;
import com.hs.mail.imap.message.search.SentDateKey;
import com.hs.mail.imap.message.search.SequenceKey;
import com.hs.mail.imap.message.search.SizeKey;
import com.hs.mail.imap.message.search.SubjectKey;
import com.hs.mail.imap.message.search.TextKey;

public class SearchKeyBuilder {

	private static final DateFormat DATE_FORMAT0;
	static {
		DATE_FORMAT0 = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
	}

	public SearchKey buildKey(List<String> tokens) throws Exception {
		if (tokens.isEmpty()) {
			return new AllKey();
		}
		SearchKeyList searchKey = new AndKey();
		while (!tokens.isEmpty()) {
			searchKey.addKey(decodeSearchKey(tokens));
		}
		removeAllKey(searchKey);
		if (searchKey.size() == 1) {
			return searchKey.getSearchKeys().get(0);
		} else {
			return searchKey;
		}
	}

	private void removeAllKey(SearchKeyList searchKey) {
		if (searchKey.size() > 1) {
			searchKey.getSearchKeys().remove(new AllKey());
		}
	}
	
	private SearchKey decodeSearchKey(List<String> tokens)
			throws Exception {
		String token = tokens.remove(0);
		SearchKey key = null;
		if ("ALL".equals(token)) {
			key = new AllKey();
		} else if ("ANSWERED".equals(token)) {
			key = new FlagKey(Flags.Flag.ANSWERED, true);
		} else if ("BCC".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			key = new RecipientStringKey(Message.RecipientType.BCC, tokens.remove(0));
		} else if ("BEFORE".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			key = new InternalDateKey(ComparisonTerm.LT,
					parseDate(tokens.remove(0)));
		} else if ("BODY".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			key = new TextKey(tokens.remove(0), false);
		} else if ("CC".equals(token)) {
			key = new RecipientStringKey(Message.RecipientType.CC, tokens.remove(0));
		} else if ("DELETED".equals(token)) {
			key = new FlagKey(Flags.Flag.DELETED, true);
		} else if ("FLAGGED".equals(token)) {
			key = new FlagKey(Flags.Flag.FLAGGED, true);
		} else if ("FROM".equals(token)) {
			key = new FromStringKey(tokens.remove(0));
		} else if ("KEYWORD".equals(token)) {
			key = new KeywordKey(tokens.remove(0), true);
		} else if ("NEW".equals(token)) {
			key = new AndKey(new FlagKey(Flags.Flag.RECENT, true),
					new FlagKey(Flags.Flag.SEEN, false));
		} else if ("OLD".equals(token)) {
			key = new FlagKey(Flags.Flag.RECENT, false);
		} else if ("ON".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			key = new InternalDateKey(ComparisonTerm.EQ, parseDate(tokens.remove(0)));
		} else if ("RECENT".equals(token)) {
			key = new FlagKey(Flags.Flag.RECENT, true);
		} else if ("SEEN".equals(token)) {
			key = new FlagKey(Flags.Flag.SEEN, true);
		} else if ("SINCE".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			key = new InternalDateKey(ComparisonTerm.GE,
					parseDate(tokens.remove(0)));
		} else if ("SUBJECT".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			key = new SubjectKey(tokens.remove(0));
		} else if ("TEXT".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			key = new TextKey(tokens.remove(0));
		} else if ("TO".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			key = new RecipientStringKey(Message.RecipientType.TO, tokens.remove(0));
		} else if ("UNANSWERED".equals(token)) {
			key = new FlagKey(Flags.Flag.ANSWERED, false);
		} else if ("UNDELETED".equals(token)) {
			key = new FlagKey(Flags.Flag.DELETED, false);
		} else if ("UNFLAGGED".equals(token)) {
			key = new FlagKey(Flags.Flag.FLAGGED, false);
		} else if ("UNKEYWORD".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			key = new KeywordKey(tokens.remove(0), false);
		} else if ("UNSEEN".equals(token)) {
			key = new FlagKey(Flags.Flag.SEEN, false);
		} else if ("DRAFT".equals(token)) {
			key = new FlagKey(Flags.Flag.DRAFT, true);
		} else if ("HEADER".equals(token)) {
			rejectMissingArgs(tokens, token, 2);
			String headerName = tokens.remove(0);
			key = new HeaderKey(headerName, tokens.remove(0));
		} else if ("LARGER".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			key = new SizeKey(ComparisonTerm.GT, Integer.parseInt(tokens.remove(0)));
		} else if ("NOT".equals(token)) {
			key = new NotKey(decodeSearchKey(tokens));
		} else if ("OR".equals(token)) {
			rejectMissingArgs(tokens, token, 2);
			SearchKey k1 = decodeSearchKey(tokens);
			SearchKey k2 = decodeSearchKey(tokens);
			key = new OrKey(k1, k2);
		} else if ("SENTBEFORE".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			key = new SentDateKey(ComparisonTerm.LT, parseDate(tokens.remove(0)));
		} else if ("SENTON".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			key = new SentDateKey(ComparisonTerm.EQ, parseDate(tokens.remove(0)));
		} else if ("SENTSINCE".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			key = new SentDateKey(ComparisonTerm.GE, parseDate(tokens.remove(0)));
		} else if ("SMALLER".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			key = new SizeKey(ComparisonTerm.LT, Integer.parseInt(tokens.remove(0)));
		} else if ("UID".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			SequenceRange[] sequenceSet = parseSequenceSet(tokens.remove(0));
			key = new SequenceKey(sequenceSet, true);
		} else if ("UNDRAFT".equals(token)) {
			key = new FlagKey(Flags.Flag.DRAFT, false);
		} else if ("\\(".equals(token)) {
			rejectMissingArgs(tokens, token, 1);
			key = new AndKey();
			while (!"\\)".equals(token = tokens.remove(0))) {
				((AndKey) key).addKey(decodeSearchKey(tokens));
			}
		} else {
			throw new Exception("Unexpected token: " + token);
		}
		return key;
	}
	
	private static void rejectMissingArgs(List<String> tokens, String token,
			int minSize) throws Exception {
		if (tokens.size() < minSize) {
			throw new Exception("Missing arguments for " + token);
		}
	}

	private static Date parseDate(String date) throws ParseException {
		DateFormat df = DATE_FORMAT0;
		return df.parse(date);
	}

	private static SequenceRange[] parseSequenceSet(String value) {
		List<SequenceRange> rangeList = new ArrayList<SequenceRange>();
		String[] tokens = StringUtils.split(value, ",");
		for (String token : tokens) {
			rangeList.add(parseSeqRange(token));
		}
		return (SequenceRange[]) rangeList.toArray(new SequenceRange[rangeList
				.size()]);
	}

	private static SequenceRange parseSeqRange(String range) {
		int pos = range.indexOf(':');
		if (pos == -1) {
			long value = parseSeqNumber(range);
			return new SequenceRange(value);
		} else {
			long min = parseSeqNumber(range.substring(0, pos));
			long max = parseSeqNumber(range.substring(pos + 1));
			return new SequenceRange(Math.min(min, max), Math.max(min, max));
		}
	}
	
	private static long parseSeqNumber(String value) {
		if (value.length() == 1 && value.charAt(0) == '*') {
			return Long.MAX_VALUE;
		}
		return Long.parseLong(value);
	}

}
