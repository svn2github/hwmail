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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.comparators.ComparatorChain;

import com.hs.mail.imap.message.thread.Threadable;

/**
 * Implementation of RFC 5256 ORDEREDSUBJECT threading algorithm.
 * 
 * @author Won Chul Doh
 * @since Oct 14, 2018
 *
 */
public class OrderedSubjectThreader extends AbstractThreader {

	@SuppressWarnings("unchecked")
	public Threadable thread(List<Threadable> threadables) {

		Comparator<Threadable> comparator1 = new Comparator<Threadable>() {
			public int compare(Threadable t1, Threadable t2) {
				String s1 = t1.simplifiedSubject();
				String s2 = t2.simplifiedSubject();
				if (s1 == null) {
					return -1;
				}
				if (s2 == null) {
					return 1;
				}
				return s1.compareToIgnoreCase(s2);
			}
		};
		Comparator<Threadable> comparator2 = makeComparator();
		
		ComparatorChain chain = new ComparatorChain();  
		chain.addComparator(comparator1);
		chain.addComparator(comparator2);
		// Sort messages by base subject and then by the sent data (UID).
		Collections.sort(threadables, chain);

		List<Threadable> root = new ArrayList<Threadable>();
		Threadable prev = null;
		boolean first = true;
		for (Threadable threadable : threadables) {
			if (prev != null) {
				// Split messages with the same base subject text. 
				if (comparator1.compare(prev, threadable) == 0) {
					if (first) {
						prev.setChild(threadable);
						first = false;
					} else {
						prev.setNext(threadable);
					}
				} else {
					first = true;
				}
			}
			// The top level or "root" in ORDEREDSUBJECT threading
			// contains the first message of every thread.
			if (first)
				root.add(threadable);

			prev = threadable;
		}
		if (first && prev != null) {
			root.add(prev);
		}

		// Finally, the threads are sorted by the sent date of the first message
		// in the thread.
		Collections.sort(root, comparator2);
		return flush(root);
	}

}
