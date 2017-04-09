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
package com.hs.mail.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Useful Map that does not care about the case-sensitivity of keys when the key
 * value is a String. Other key types can be used.
 * 
 * String keys will be treated case insensitively, yet key case will be
 * retained. Non-string keys will work as they normally would.
 * 
 * @author Won Chul Doh
 * @since Mar 22, 2017
 *
 */
public class CaseInsensitiveMap<K, V> implements Map<K, V> {
	
	private final Map<Object, V> map;

	public CaseInsensitiveMap() {
		map = new HashMap<Object, V>();
	}

	public CaseInsensitiveMap(int initialCapacity) {
		map = new HashMap<Object, V>(initialCapacity);
	}

	public CaseInsensitiveMap(Map<K, V> m) {
		map = new HashMap<Object, V>();
		putAll(m);
	}
	
	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public V get(Object key) {
		if (key instanceof String) {
			return map.get(new CaseInsensitiveString((String) key));
		}
		return map.get(key);
	}

	public boolean containsKey(Object key) {
		if (key instanceof String) {
			return map.containsKey(new CaseInsensitiveString((String) key));
		}
		return map.containsKey(key);
	}

	public V put(K key, V value) {
		if (key instanceof String) {
			return map.put(new CaseInsensitiveString((String) key), value);
		}
		return map.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		if (m == null) {
			return;
		}
		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public V remove(Object key) {
		if (key instanceof String) {
			return map.remove(new CaseInsensitiveString((String) key));
		}
		return map.remove(key);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}
	
	/**
	 * Base Iterator. 
	 */
	private abstract class HashIterator<E> implements Iterator<E> {
		// The parent map's iterator.
		final Iterator<Entry<Object, V>> it;
		
		HashIterator(Map<Object, V> map) {
			it = map.entrySet().iterator();
		}
		
		public final boolean hasNext() {
			return it.hasNext();
		}

		@SuppressWarnings("unchecked")
		final Entry<K, V> nextEntry() {
			Entry<Object, V> e = it.next();
			Object o = e.getKey();
			return new SimpleEntry<K, V>(
					(K) ((o instanceof CaseInsensitiveString)
							? o.toString()
							: o),
					e.getValue());
		}
	}

	/**
	 * KeySet iterator. 
	 */
	private final class KeyIterator extends HashIterator<K> {
		KeyIterator(Map<Object, V> map) {
			super(map);
		}

		public K next() {
			return nextEntry().getKey();
		}
	}

	/**
	 * EntrySet iterator.
	 */
	private final class EntryIterator extends HashIterator<Map.Entry<K,V>> {
		EntryIterator(Map<Object, V> map) {
			super(map);
		}

		public Map.Entry<K, V> next() {
			return nextEntry();
		}
	}
	
	Iterator<K> newKeyIterator() {
		return new KeyIterator(map);
	}
	
	Iterator<Map.Entry<K, V>> newEntryIterator() {
		return new EntryIterator(map);
	}

	public Set<K> keySet() {
		return new KeySet();
	}

	/**
	 * KeySet implementation. 
	 */
	private class KeySet extends AbstractSet<K> {
		
		public int size() {
			return map.size();
		}
		
		public Iterator<K> iterator() {
			return newKeyIterator();
		}

	}

	public Collection<V> values() {
		return map.values();
	}

	public Set<Map.Entry<K, V>> entrySet() {
		return new EntrySet();
	}

	/**
	 * EntrySet implementation. 
	 */
	private class EntrySet extends AbstractSet<Entry<K, V>> {

		public int size() {
			return map.size();
		}

		public Iterator<Map.Entry<K, V>> iterator() {
			return newEntryIterator();
		}

	}
	
	/**
	 * Internal class used to wrap String keys. This class ignores the case of
	 * Strings when they are compared.
	 */
	protected static final class CaseInsensitiveString {

		private final String string;
		
		public CaseInsensitiveString(String string) {
			this.string = string;
		}
		
		public String toString() {
			return string;
		}
		
		public int hashCode() {
			return (null == string) 
					? "null".hashCode() 
					: string.toLowerCase().hashCode();
		}

		public boolean equals(Object obj) {
			if (obj instanceof CaseInsensitiveString) {
				return string.equalsIgnoreCase(obj.toString());
			}
			if (obj instanceof String) {
				return string.equalsIgnoreCase((String) obj);
			}
			return super.equals(obj);
		}

	}

}
