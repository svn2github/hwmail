package com.hs.mail.smtp.processor.hook;

import com.hs.mail.container.config.ComponentManager;
import com.hs.mail.imap.user.UserManager;

abstract class AbstractHook {

	protected UserManager getUserManager() {
		return (UserManager) ComponentManager.getBean("userManager");
	}

}
