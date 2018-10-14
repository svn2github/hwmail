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
package com.hs.mail.imap.processor.ext.thread;

import java.util.Comparator;
import java.util.List;

import com.hs.mail.imap.message.thread.Threadable;

/**
 * 
 * @author Won Chul Doh
 * @since Oct 14, 2018
 *
 */
public abstract class AbstractThreader implements Threader {

	Threadable flush(List<Threadable> threadables) {
		Threadable first = null, last = null;
		for (Threadable threadable : threadables) {
			if (first == null)
				first = threadable;
			else
				last.setNext(threadable);
			last = threadable;
		}
		last.setNext(null);
		return first;
	}	

	Comparator<Threadable> makeComparator() {
		return new Comparator<Threadable>() {
			// All collation is in ascending order. Earlier dates collate before
			// later dates.
			public int compare(Threadable t1, Threadable t2) {
				if (t1.getUID() == t2.getUID())
					return 0;
				else if (t1.getUID() < t2.getUID())
					return -1;
				else
					return 1;
			}
		};
	}

}
