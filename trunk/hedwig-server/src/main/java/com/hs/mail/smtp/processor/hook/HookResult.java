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

public class HookResult {

	public static final HookResult DUNNO = new HookResult(HookReturnCode.DUNNO);
	public static final HookResult OK = new HookResult(HookReturnCode.OK);
	
	private final int result;
	private final String message;

	public HookResult(int result, String message) {
		this.result = result;
		this.message = message;
	}

	public HookResult(int result) {
		this(result, null);
	}

	public int getResult() {
		return result;
	}

	public String getMessage() {
		return message;
	}
	
	public static HookResult reject(String message) {
		return new HookResult(HookReturnCode.REJECT, message);
	}
	
}
