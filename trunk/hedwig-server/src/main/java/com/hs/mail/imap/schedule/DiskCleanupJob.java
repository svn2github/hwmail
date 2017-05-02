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
package com.hs.mail.imap.schedule;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.container.config.Config;
import com.hs.mail.imap.mailbox.MailboxManager;

/**
 * 
 * @author Won Chul Doh
 * @since Sep 28, 2010
 *
 */
public class DiskCleanupJob {
	
	static Logger logger = LoggerFactory.getLogger(DiskCleanupJob.class);

	private MailboxManager mailboxManager;

	public void setMailboxManager(MailboxManager mailboxManager) {
		this.mailboxManager = mailboxManager;
	}
	
	public void execute() {
		logger.debug("Starting disk cleanup job.");

		// Delete physical messages which are not referenced by any mailbox
		// message.
		mailboxManager.deleteOrphanMessages();
		
		String prop = Config.getProperty("stop_cron_after", "2h");
		Date stopAt = ScheduleUtils.getTimeAfter(prop, DateUtils.addHours(new Date(), 2));
		if ((prop = Config.getProperty("expunge_after", null)) != null) {
			new MessageExpunger(mailboxManager).expunge(prop, stopAt.getTime());
		}
		
		if ((prop = Config.getProperty("compress_after", null)) != null) {
			new MessageCompressor().compress(prop, stopAt.getTime());
		}
	}
	
}
