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

/**
 * 
 * @author Won Chul Doh
 * @since April 12, 2018
 * 
 */
public class POP3Exception extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public static final POP3Exception INVALID_STATE = new POP3Exception("Command is not valid in this state");
	public static final POP3Exception INVALID_ARGS = new POP3Exception("Invalid arguments");
	
	/**
	 * Constructor for POP3Exception.
	 */
	public POP3Exception() {
		super();
	}

	/**
	 * Constructor for POP3Exception.
	 * 
	 * @param message
	 */
	public POP3Exception(String message) {
		super(message);
	}

	/**
	 * Constructor for POP3Exception.
	 * 
	 * @param message
	 * @param cause
	 */
	public POP3Exception(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for POP3Exception.
	 * 
	 * @param cause
	 */
	public POP3Exception(Throwable cause) {
		super(cause);
	}

}
