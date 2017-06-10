/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.hs.mail.smtp.processor.hook;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.hs.mail.container.config.Config;
import com.hs.mail.exception.ConfigException;

public class HookFactory {
	
	@SuppressWarnings("unchecked")
	public static <T extends Hook> List<T> getHooks(Class<T> type, String name,
			String defaultValue) throws ConfigException {
		List<T> hooks = null;
		String restrictions = Config.getProperty(name, defaultValue);
		if (StringUtils.isNotBlank(restrictions)) {
			String[] array = StringUtils.split(restrictions, ",");
			hooks = new ArrayList<T>(array.length);
			for (String restriction : array) {
				String[] tokens = StringUtils.split(restriction);
				if (ArrayUtils.isNotEmpty(tokens)) {
					Hook hook = getHook(name, tokens);
					if (type.isInstance(hook)) {
						hooks.add((T) hook);
					} else {
						throw new ConfigException(
								"Unsupported " + name + " '" + tokens[0] + "'");
					}
				}
			}
		}
		return hooks;
	}
	
	private static Hook getHook(String name, String[] tokens)
			throws ConfigException {
		if ("check_recipient_access".equals(tokens[0])) {
			return new AccessTableHook(tokens.length > 1
					? tokens[1]
					: Config.replaceByProperties("${app.home}/conf/recipient_access"));
		} else if ("check_sender_access".equals(tokens[0])) {
			return new AccessTableHook(tokens.length > 1
					? tokens[1]
					: Config.replaceByProperties("${app.home}/conf/sender_access"));
		} else if ("permit_mynetworks".equals(tokens[0])) {
			return new RemoteAddrInNetwork();
		} else if ("permit_sasl_authenticated".equals(tokens[0])) {
			return new AuthRequired();
		} else if ("reject".equals(tokens[0])) {
			return new RejectHook("smtpd_relay_restrictions".equals(name));
		} else if ("reject_rbl_clients".equals(tokens[0])) {
			return new DNSRBLHandler(ArrayUtils.remove(tokens, 0));
		} else if ("reject_unlisted_recipient".equals(tokens[0])) {
			return new ValidRcptHook();
		} else {
			throw new ConfigException(
					"Unsupported " + name + " '" + tokens[0] + "'");
		}
	}
	
}
