package com.hs.mail.imap.message.thread;

import java.util.Enumeration;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.hs.mail.imap.ImapConstants;

public class ThreadableMessage implements Threadable {

	public static final String[] WANTED_FIELDS = new String[] {
			ImapConstants.RFC822_SUBJECT,
			ImapConstants.RFC822_MESSAGE_ID, 
			ImapConstants.RFC822_IN_REPLY_TO,
			ImapConstants.RFC822_REFERENCES };

	private long uid;
	private ThreadableMeta meta;
	private Threadable child; 
	private Threadable sibling; 
	
	public ThreadableMessage(long uid, Map<String, String> header) {
		this(uid, new ThreadableMeta(header));
	}
	
	ThreadableMessage(long uid, ThreadableMeta meta) {
		this.uid = uid;
		this.meta = meta;
	}

	@Override
	public Enumeration<Threadable> allElements() {
		return new AllEnumerator(this);
	}

	@Override
	public void setUID(long uid) {
		this.uid = uid;
	}

	@Override
	public long getUID() {
		return uid;
	}

	@Override
	public String messageThreadID() {
		return meta.messageThreadID();
	}

	@Override
	public String[] messageThreadReferences() {
		return meta.messageThreadReferences();
	}

	@Override
	public String simplifiedSubject() {
		return meta.simplifiedSubject();
	}

	@Override
	public boolean subjectIsReply() {
		return meta.subjectIsReply();
	}

	@Override
	public Threadable getNext() {
		return sibling;
	}

	@Override
	public void setNext(Threadable next) {
		sibling = next;
	}

	@Override
	public Threadable getChild() {
		return child;
	}
	
	@Override
	public void setChild(Threadable kid) {
		child = kid;
	}

	@Override
	public Threadable makeDummy() {
		return new DummyThreadable(uid, meta);
	}

	@Override
	public boolean isDummy() {
		return false;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(meta,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

	class AllEnumerator implements Enumeration<Threadable>  {
		Threadable tail;
		Enumeration<Threadable> kids;
		
		AllEnumerator(Threadable thread) {
			tail = thread;
		}
		
		@Override
		public Threadable nextElement() {
			if (kids != null) {
				Threadable result = kids.nextElement();
				if (!kids.hasMoreElements()) {
					kids = null;
				}
				return result;

			} else if (tail != null) {
				Threadable result = tail;
				if (tail.getChild() != null) {
					kids = new AllEnumerator(tail.getChild());
				}
				tail = tail.getNext();
				return result;

			} else {
				throw new NoSuchElementException();
			}
		}
		
		@Override
		public boolean hasMoreElements() {
			if (tail != null)
				return true;
			else if (kids != null)
				return kids.hasMoreElements();
			else
				return false;
		}

	}

}
