package com.hs.mail.imap.processor.ext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import com.hs.mail.imap.message.thread.Threadable;

/**
 * This sorts a set of messages by some metric. The set of messages are already
 * arranged into a thread hierarchy; in that case, siblings are sorted while
 * leaving parent/child relationships intact.
 */
public class ThreadSorter {

	public Threadable sort(Threadable first) {
		if (first == null || first.getNext() == null)
			return first;

		Threadable dummy = first.makeDummy();
		dummy.setChild(first);

		sort(dummy, new Comparator<Threadable>() {
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
		});

		return dummy.getChild();
	}

	private void sort(Threadable parent, Comparator<Threadable> comp) {
		List<Threadable> list = new ArrayList<Threadable>();
		int count = 0;
		Enumeration<Threadable> e = parent.children();
		while (e.hasMoreElements()) {
			list.add(e.nextElement());
			count++;
		}

		if (count < 2) {
			return;
		}

		Collections.sort(list, comp);

	    // Flush new order of list into the Threadable.
		parent.setChild(list.get(0));
		list.add(null);
		for (int i = 0; i < count; i++) {
			list.get(i).setNext(list.get(i + 1));
		}

		// Repeat on the grandchildren.
		for (int i = 0; i < count; i++) {
			if (list.get(i).getChild() != null)
				sort(list.get(i), comp);
		}

		list = null;
	}
	
}
