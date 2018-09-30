package com.hs.mail.util;

public class MessageIDGenerator {

	public static String generate(String host_name) {
		int radix = 36; // keep the strings short
		long time = System.currentTimeMillis(); // millisec resolution
		long salt = Double.doubleToLongBits(Math.random()); // 64 random bits

		StringBuffer out = new StringBuffer();
		out.append('<');
		out.append(Long.toString(Math.abs(time), radix));
		out.append('.');
		out.append(Long.toString(Math.abs(salt), radix));
		out.append('@');

		if (host_name != null) {
			out.append(host_name);
		} else {
			out.append((byte) 'h'); // domain part must begin with a letter
			salt = (Double.doubleToLongBits(Math.random()) & 0xFFFFFFFFL); // 32
																			// bits
			out.append(Long.toString(Math.abs(salt), radix));
		}
		out.append((byte) '>');
		return out.toString();
	}

}
