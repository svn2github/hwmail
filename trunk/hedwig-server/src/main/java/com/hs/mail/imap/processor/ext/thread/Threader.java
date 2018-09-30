package com.hs.mail.imap.processor.ext.thread;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.hs.mail.imap.message.thread.Threadable;

public class Threader {
	private ThreadContainer rootNode;
	private Map<String, ThreadContainer> idTable;
	private int bogusIdCount = 0;

	public Threadable thread(Threadable threadableRoot) {
		if (threadableRoot == null) {
			return null;
		}
		
		idTable = new HashMap<String, ThreadContainer>();
		
		Enumeration<Threadable> e = threadableRoot.allElements();
		while (e.hasMoreElements()) {
			Threadable t = e.nextElement();
			if (!t.isDummy()) {
				buildContainer(t);
			}
		}
		
		rootNode = findRootSet();
		idTable.clear();
		idTable = null;
		
		pruneEmptyContainers(rootNode);
		
		rootNode.reverseChildren();
		
		gatherSubjects();
		
		if (rootNode.next != null)
			throw new Error("Root node has a next?" + rootNode);

		for (ThreadContainer r = rootNode.child; r != null; r = r.next) {
			if (r.threadable == null)
				r.threadable = r.child.threadable.makeDummy();
		}
		
		Threadable result = (rootNode.child == null)
                ? null
                : rootNode.child.threadable;

		rootNode.flush();
		rootNode = null;

		return result;
	}

	private void buildContainer(Threadable threadable) {
		String id = threadable.messageThreadID();
		ThreadContainer c = idTable.get(id);
		
		if (c != null) {
			// There is already a ThreadContainer in the table for this ID.
			if (c.threadable != null) {
				id = "<Bogus-ID:" + (bogusIdCount++) + ">";
				c = null;
			} else {
				c.threadable = threadable;
			}
		}
		
		if (c == null) {
			c = new ThreadContainer();
			c.threadable = threadable;
			idTable.put(id, c);
		}
		
		ThreadContainer parentRef = null;
		String[] refs = threadable.messageThreadReferences();
		if (ArrayUtils.isNotEmpty(refs)) {
			for (String ref : refs) {
				ThreadContainer r = idTable.get(ref);
				if (r == null) {
					r = new ThreadContainer();
					idTable.put(ref, r);
				}
				
				if (parentRef != null 
						&& r.parent == null 
						&& parentRef != r
						&& !parentRef.findChild(r)) {
					r.parent = parentRef;
					r.next = parentRef.child;
					parentRef.child = r;
				}
				parentRef = r;
			}
		}
		
		if (parentRef != null 
				&& (parentRef == c || c.findChild(parentRef))) {
			parentRef = null;
		}
		
		if (c.parent != null) {
			ThreadContainer rest, prev;
			for (prev = null, rest = c.parent.child; 
				rest != null; 
				prev = rest, rest = rest.next) {
				if (rest == c) {
					break;
				}
			}
			if (rest == null) {
				throw new Error("Cannot find " + c + " in parent " + c.parent);
			}
			
			if (prev == null)
				c.parent.child = c.next;
			else
				prev.next = c.next;
			
			c.next = null;
			c.parent = null;
		}
		
		if (parentRef != null) {
			c.parent = parentRef;
			c.next = parentRef.child;
			parentRef.child = c;
		}
	}

	private ThreadContainer findRootSet() {
		ThreadContainer root = new ThreadContainer();
		for (ThreadContainer c : idTable.values()) {
			if (c.parent == null) {
				if (c.next != null)
					throw new Error("c.next is " + c.next.toString());
				c.next = root.child;
				root.child = c;
			}
		}
		return root;
	}

	private void pruneEmptyContainers(ThreadContainer parent) {
		ThreadContainer container, prev, next;
	    for (prev = null, container = parent.child, next = container.next;
	    	container != null;
	    	prev = container, container = next,
	        next = (container == null ? null : container.next)) {
			if (container.threadable == null && container.child == null) {
				if (prev == null)
					parent.child = container.next;
				else
					prev.next = container.next;
		        container = prev;
			} else if (container.threadable == null
					&& container.child != null
					&& (container.parent != null
						|| container.child.next == null)) {
		        ThreadContainer tail;
		        ThreadContainer kids = container.child;

		        if (prev == null)
		            parent.child = kids;
		          else
		            prev.next = kids;

				for (tail = kids; tail.next != null; tail = tail.next) {
					tail.parent = container.parent;
				}

		        tail.parent = container.parent;
		        tail.next = container.next;

		        next = kids;
		        
		        container = prev;

			} else if (container.child != null) {
				pruneEmptyContainers(container);
			}
	    }
	}

	private void gatherSubjects() {
		int count = 0;
		for (ThreadContainer c = rootNode.child; c != null; c = c.next) {
			count++;
		}
		
		Map<String, ThreadContainer> subjTable 
				= new HashMap<String, ThreadContainer>((int) (count * 1.2), (float) 0.9);
		
		count = 0;
		for (ThreadContainer c = rootNode.child; c != null; c = c.next) {
			Threadable threadable = c.threadable;
			
			if (threadable == null)
				threadable = c.child.threadable;
			
			String subj = threadable.simplifiedSubject();
			if (StringUtils.isEmpty(subj))
				continue;
			
			ThreadContainer old = subjTable.get(subj);
			if (old == null 
					|| (c.threadable == null && old.threadable != null)
					|| ((old.threadable != null
							&& old.threadable.subjectIsReply()
							&& c.threadable != null
							&& !c.threadable.subjectIsReply()))) {
				subjTable.put(subj, c);
				count++;
			}
		}
		
		if (count == 0) {
			return;
		}

	    ThreadContainer prev, c, rest;
	    for (prev = null, c = rootNode.child, rest = c.next;
	    	c != null;
	    	prev = c, c = rest, rest = (rest == null ? null : rest.next)) {
	    	Threadable threadable = c.threadable;
	    	if (threadable == null)
	    		threadable = c.child.threadable;
	    	
	    	String subj = threadable.simplifiedSubject();
			if (StringUtils.isEmpty(subj))
				continue;

			ThreadContainer old = subjTable.get(subj);
			if (old == c)
				continue;
			
			if (prev == null)
				rootNode.child = c.next;
			else
				prev.next = c.next;
			c.next = null;
			
			if (old.threadable == null && c.threadable == null) {
				ThreadContainer tail;
				for (tail = old.child; 
					tail != null && tail.next != null;
					tail = tail.next)
				; // DO NOTHING
				tail.next = c.child;
				for (tail = c.child; tail != null; tail = tail.next)
					tail.parent = old;
				c.child = null;

			} else if (old.threadable == null
					|| (c.threadable != null 
							&& c.threadable.subjectIsReply()
							&& !old.threadable.subjectIsReply())) {
				c.parent = old;
				c.next = old.child;
				old.child = c;

			} else {
		        ThreadContainer newc = new ThreadContainer();
		        newc.threadable = old.threadable;

				newc.child = old.child;
				for (ThreadContainer tail = newc.child; tail != null; tail = tail.next)
					tail.parent = newc;

				old.threadable = null;
				old.child = null;

		        c.parent = old;
		        newc.parent = old;

		        old.child = c;
		        c.next = newc;

			}
			
			c = prev; 
	    }
	    
	    subjTable.clear();
	    subjTable = null;
	}

}
