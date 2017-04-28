package com.hs.mail.io;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class DotStuffingInputStreamTest {

	private final static char[] DATA_TERMINATOR = { '\r', '\n', '.', '\r', '\n' };
	
	@Test
	public void testMain() throws IOException {
		String data = ".This is a test\r\nof the thing.\r\nWe should not have much trouble.\r\n.doubled?\r\nor not?\n.doubled\nor not?\r\n.\r\nExtra stuffs\n\n\r\r\r\n";
		ByteArrayInputStream bin = new ByteArrayInputStream(data.getBytes());
		InputStream in = new CharTerminatedInputStream(bin, DATA_TERMINATOR);
		in = new DotStuffingInputStream(in);
		ByteArrayOutputStream bout = new ByteArrayOutputStream(); 
		IOUtils.copy(in, bout);
		String expected = ".This is a test\r\nof the thing.\r\nWe should not have much trouble.\r\ndoubled?\r\nor not?\n.doubled\nor not?";
		assertEquals(expected, bout.toString());
	}

}
