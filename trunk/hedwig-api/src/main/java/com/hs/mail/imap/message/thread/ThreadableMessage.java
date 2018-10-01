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
package com.hs.mail.imap.message.thread;

import java.util.Enumeration;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.hs.mail.imap.ImapConstants;

/**
 * 
 * @author Won Chul Doh
 * @since Sep 30, 2018
 *
 */
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
