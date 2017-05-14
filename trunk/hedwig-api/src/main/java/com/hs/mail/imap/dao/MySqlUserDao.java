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
package com.hs.mail.imap.dao;


/**
 * 
 * @author Won Chul Doh
 * @since Mar 23, 2010
 *
 */
public class MySqlUserDao extends AnsiUserDao {

	public long getQuotaUsage(long ownerID, long mailboxID) {
		if (mailboxID != 0) {
			String sql = "SELECT IFNULL(SUM(rfcsize), 0) FROM hw_message m, hw_physmessage p WHERE m.mailboxid = ?  AND m.physmessageid=p.physmessageid";
			return queryForLong(sql, new Object[] { new Long(mailboxID) });
		} else {
			String sql = "SELECT IFNULL(SUM(rfcsize), 0) FROM hw_mailbox b, hw_message m, hw_physmessage p WHERE b.ownerid=? AND b.mailboxid=m.mailboxid AND m.physmessageid=p.physmessageid";
			return queryForLong(sql, new Object[] { new Long(ownerID) });
		}
	}

}
