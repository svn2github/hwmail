package com.hs.mail.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class WildcardMatchTest {

	@Test
	public void test() {
		assertFalse(WildcardMatch.match("aa", "a"));
		assertTrue(WildcardMatch.match("aa", "aa"));
		assertFalse(WildcardMatch.match("aaa", "aa"));
		assertTrue(WildcardMatch.match("aa", "*"));
		assertTrue(WildcardMatch.match("aa", "a?"));
		assertTrue(WildcardMatch.match("ab", "?*"));
		assertFalse(WildcardMatch.match("aab", "c*a*b"));
		assertTrue(WildcardMatch.match("cab", "c*a*b"));
		assertTrue(WildcardMatch.match("cab", "ca*"));
	}

}
