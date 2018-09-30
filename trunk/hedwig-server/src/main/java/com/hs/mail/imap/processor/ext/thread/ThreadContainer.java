package com.hs.mail.imap.processor.ext.thread;

import com.hs.mail.imap.message.thread.Threadable;

/**
 * The ThreadContainer object is used to encapsulate a Threadable object 
 * (it holds some intermediate state used while threading.)
 *
 */
class ThreadContainer {
	Threadable threadable;
	ThreadContainer parent;
	ThreadContainer child;
	ThreadContainer next;

	/**
	 * Copy the ThreadContainer tree structure down into the underlying
	 * Threadable objects (that is, make the Threadable tree look like the
	 * ThreadContainer tree.)
	 */
	void flush() {
		if (parent != null && threadable == null)
			throw new Error("No threadable in " + this.toString());
		
		parent = null;

		if (threadable != null)
			threadable.setChild(child == null ? null : child.threadable);

		if (child != null) {
			child.flush();
			child = null;
		}

		if (threadable != null)
			threadable.setNext(next == null ? null : next.threadable);

		if (next != null) {
			next.flush();
			next = null;
		}
		
		threadable = null;
	}

	/**
	 * Returns true if child is under self's tree. This is used for detecting
	 * circularities in the references header.
	 */
	boolean findChild(ThreadContainer target) {
		if (child == null)
			return false;
		else if (child == target)
			return true;
		else
			return child.findChild(target);
	}

	/**
	 * Reverse the children.
	 */
	void reverseChildren() {
		if (child != null) {
			ThreadContainer kid, prev, rest;
			for (prev = null, kid = child, rest = kid.next; 
				kid != null; 
				prev = kid, kid = rest, rest = (rest == null ? null : rest.next)) {
				kid.next = prev;
			}
			child = prev;
			
			for (kid = child; kid != null; kid = kid.next) {
				kid.reverseChildren();
			}
		}
	}

}
