package com.hs.mail.imap.dao.test;

import static org.junit.Assert.*;

import org.apache.james.mime4j.dom.address.Mailbox;
import org.apache.james.mime4j.field.address.LenientAddressParser;
import org.junit.BeforeClass;
import org.junit.Test;

public class AddressParserTest {
	
	static private LenientAddressParser parser;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		parser = LenientAddressParser.DEFAULT;
	}

	@Test
	public void testParseMailbox() {
		Mailbox mailbox1 = parser.parseMailbox("\"Jhon Doe\" <jdoe@example.net>");
		assertEquals("Jhon Doe", mailbox1.getName());
		assertEquals("jdoe@example.net", mailbox1.getAddress());

        Mailbox mailbox2 = parser.parseMailbox("Mary Smith <mary@example.net>");
        assertEquals("Mary Smith", mailbox2.getName());
        assertEquals("mary@example.net", mailbox2.getAddress());

		Mailbox mailbox3 = parser.parseMailbox("john.doe@acme.org");
        assertNull(mailbox3.getName());
        assertEquals("john.doe@acme.org", mailbox3.getAddress());

		Mailbox mailbox4 = parser.parseMailbox("\"john.doe@acme.org\"");
        assertNull(mailbox4.getName());
        assertEquals("john.doe@acme.org", mailbox4.getAddress());
	}

}
