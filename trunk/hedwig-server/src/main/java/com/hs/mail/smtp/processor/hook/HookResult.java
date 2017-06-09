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
