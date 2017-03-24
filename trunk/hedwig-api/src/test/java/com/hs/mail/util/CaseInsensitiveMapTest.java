package com.hs.mail.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class CaseInsensitiveMapTest {

	@Test
	public void testCaseInsensitive() {
        Map<Object, String> map = new CaseInsensitiveMap<String, String>();
        map.put("One", "One");
        map.put("Two", "Two");
        assertEquals("One", (String) map.get("one"));
        assertEquals("One", (String) map.get("oNe"));
        map.put("two", "Three");
        assertEquals("Three", (String) map.get("Two"));
	}

	@Test
    public void testNullHandling() {
    	Map<Object, String> map = new CaseInsensitiveMap<String, String>();
        map.put("One", "One");
        map.put("Two", "Two");
        map.put(null, "Three");
        assertEquals("Three", (String) map.get(null));
        map.put(null, "Four");
        assertEquals("Four", (String) map.get(null));
        Set<Object> keys = map.keySet();
        assertTrue(keys.contains("one"));
        assertTrue(keys.contains("two"));
        assertTrue(keys.contains(null));
        assertTrue(keys.size() == 3);
    }

	@Test
    public void testPutAll() {
        Map<Object, String> map = new HashMap<Object, String>();
        map.put("One", "One");
        map.put("Two", "Two");
        map.put("one", "Three");
        map.put(null, "Four");
        map.put(new Integer(20), "Five");
        Map<Object, String> caseInsensitiveMap = new CaseInsensitiveMap<String, String>(map);
        assertTrue(caseInsensitiveMap.size() == 4);
        assertTrue(caseInsensitiveMap.containsKey("one"));
        assertTrue(caseInsensitiveMap.containsKey("two"));
        assertTrue(caseInsensitiveMap.containsKey(null));
        assertTrue(caseInsensitiveMap.containsKey(20));
        assertEquals(caseInsensitiveMap.get(null), "Four");
    } 
    
}
