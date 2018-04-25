/*
 * Copyright 2018 the original author or authors.
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
package com.hs.mail.pop3;

import java.util.List;

import com.hs.mail.container.server.socket.TcpTransport;
import com.hs.mail.imap.message.MessageMetaData;

/**
 * 
 * @author Won Chul Doh
 * @since April 11, 2018
 * 
 */
public class POP3Session {

	public enum State {
		/**
		 * RFC1939 <code>4. The AUTHORIZATION State</code> 
		 */
		AUTHORIZATION("AUTHORIZATION"), 

		/**
		 * RFC1939 <code>5. The TRANSACTION State</code> 
		 */
		TRANSACTION("TRANSACTION"), 

		/**
		 * RFC1939 <code>6. The UPDATE State</code> 
		 */
		UPDATE("UPDATE");

		/** To aid debugging */
		private final String name;

		private State(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

    private TcpTransport transport;
	private State state = State.AUTHORIZATION;
	private String user = null;
	private long mailboxID = -1;
	private int errorCount = 0;
	private List<MessageMetaData> uidList;
	private List<Long> deletedUidList;

	public POP3Session(TcpTransport trans) {
		this.transport = trans;
	}
	
    public TcpTransport getTransport() {
		return transport;
	}
	
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public long getMailboxID() {
		return mailboxID;
	}

	public void setMailboxID(long mailboxID) {
		this.mailboxID = mailboxID;
	}

	public int incErrorCount() {
		errorCount++;
		return errorCount;
	}

	public List<MessageMetaData> getUidList() {
		return uidList;
	}

	public void setUidList(List<MessageMetaData> uidList) {
		this.uidList = uidList;
	}

	public List<Long> getDeletedUidList() {
		return deletedUidList;
	}

	public void setDeletedUidList(List<Long> deletedUidList) {
		this.deletedUidList = deletedUidList;
	}

	public void writeResponse(String response) {
		transport.println(response);
	}

}
