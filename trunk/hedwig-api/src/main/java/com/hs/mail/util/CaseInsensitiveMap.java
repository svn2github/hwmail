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

import java.util.HashMap;
import java.util.Map;

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
public class CaseInsensitiveMap<K, V> extends HashMap<Object, V> {

	private static final long serialVersionUID = -2917003437358308877L;

	public CaseInsensitiveMap() {
		super();
	}

	public CaseInsensitiveMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public CaseInsensitiveMap(int initialCapacity) {
		super(initialCapacity);
	}

	public CaseInsensitiveMap(Map<? extends Object, ? extends V> m) {
		super.putAll(m);
	}

	public V get(Object key) {
		if (key instanceof String) {
			return super.get(new CaseInsensitiveString((String) key));
		}
		return super.get(key);
	}

	public V put(Object key, V value) {
		if (key instanceof String) {
			return super.put(new CaseInsensitiveString((String) key), value);
		}
		return super.put(key, value);
	}

	public boolean containsKey(Object key) {
		if (key instanceof String) {
			return super.containsKey(new CaseInsensitiveString((String) key));
		}
		return super.containsKey(key);
	}

	public V remove(Object key) {
		if (key instanceof String) {
			return super.remove(new CaseInsensitiveString((String) key));
		}
		return super.remove(key);
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
