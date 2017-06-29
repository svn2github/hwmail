package com.hs.mail.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LineReader extends InputStreamReader {

	@SuppressWarnings("serial")
	public class TerminationException extends IOException {
		public TerminationException(String s) {
			super(s);
		}
	}

	@SuppressWarnings("serial")
	public class LineLengthExceededException extends IOException {
		public LineLengthExceededException(String s) {
			super(s);
		}
	}

	private int maxLineLen = 2048;

	public LineReader(InputStream in) {
		super(in);
	}

	public String readLine() throws IOException {
		StringBuffer sb = new StringBuffer();
		int bytesRead = 0;
		while (bytesRead++ < maxLineLen) {
			int iRead = read();
			switch (iRead) {
			case '\r':
				iRead = read();
				if (iRead == '\n') {
					return sb.toString();
				}
				// fall through
			case '\n':
				// LF without a preceding CR
				throw new TerminationException("Bad line terminator");
			case -1:
				// premature EOF
				return null;
			default:
				sb.append((char) iRead);
			}
		}
		throw new LineLengthExceededException("Exceeded maximun line length");
	}

}
