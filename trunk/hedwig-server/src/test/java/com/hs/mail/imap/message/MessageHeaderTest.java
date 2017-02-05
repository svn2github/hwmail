package com.hs.mail.imap.message;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

import com.hs.mail.test.TestUtil;

public class MessageHeaderTest {

	@Test
	public void test() throws Exception {
		InputStream is = TestUtil.readResourceAsStream("/quotedPrintableWithAttach.eml");
		MessageHeader header = new MessageHeader(is);
		assertNotNull(header);
		assertEquals(header.getSubject(), "연(HB)20110922-002 Failed to load IMAP envelope 에 대한 문의입니다.");
		assertEquals(header.getFrom().getName(), "김준기");
		assertEquals(header.getFrom().getAddress(), "highbase@secuace.co.kr");
	}

}
