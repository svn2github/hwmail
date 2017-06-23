package com.hs.mail.util;

public class WildcardMatch {

	public static boolean match(String s, String p) {
		if (s == null)
			return p == null;
		if (p == null)
			return false;
		
		int n = s.length();
		int m = p.length();
		
		int i = 0;
		int j = 0;
		int star = -1;
		int sp = 0;
		
		while (i < n) {
			while (j < m && p.charAt(j) == '*') {
				star = j++;
				sp = i;
			}
			if (j == m || (p.charAt(j) != s.charAt(i) && p.charAt(j) != '?')) {
				if (star < 0)
					return false;
				j = star + 1;
				i = ++sp;
			} else {
				i++;
				j++;
			}
		}
		while (j < m && p.charAt(j) == '*')
			j++;
		return j == m;
	}

}
