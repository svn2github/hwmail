package com.hs.mail.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.io.Writer;

import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.OnlyOnceErrorHandler;
import org.apache.log4j.spi.ErrorHandler;

public class RollingPrintStream extends PrintStream {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	protected String fileName = null;
	
	/**
	 * The default maximum file size is 10MB.
	 */
	protected long maxFileSize = 10 * 1024 * 1024;
	
	protected CountingQuietWriter qw;
	
	protected ErrorHandler errorHandler = new OnlyOnceErrorHandler();

	public RollingPrintStream(String fileName) throws IOException {
		super(new FileOutputStream(fileName, true));
		super.close();
		setFile(fileName, true);
	}

	@Override
	public void print(String s) {
		if (qw != null) {
			qw.write(s);
			qw.flush();
			long size = qw.getCount();
	        if (size >= maxFileSize) {
	        	rollOver();
	        }
		}
	}

	@Override
	public void println(String x) {
		print(x);
		print(LINE_SEPARATOR);
	}

	@Override
	public void close() {
		if (qw != null) {
			try {
				qw.close();
			} catch (IOException e) {
				if (e instanceof InterruptedIOException) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	public void setFile(String fileName, boolean append) throws IOException {
		reset();
		File file = new File(fileName);
		Writer fw = new FileWriter(file, append);
		this.setQWForFiles(fw);
		this.fileName = fileName;
		if (append) {
			qw.setCount(file.length());
		}
	}
	
	protected void setQWForFiles(Writer writer) {
		this.qw = new CountingQuietWriter(writer, errorHandler);
	}
	
	public synchronized void rollOver() {
		File target = new File(fileName + ".1");
		File file = new File(fileName);
		boolean renameSucceeded = true;
		if (target.exists()) {
			renameSucceeded = target.delete();
		}
		if (renameSucceeded) {
			close();	// keep windows happy.
			renameSucceeded = file.renameTo(target);
		}
		try {
			setFile(fileName, false);
		} catch (IOException e) {
			if (e instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
		}
	}
	
	protected void reset() {
		close();
		this.qw = null;
	}

}
