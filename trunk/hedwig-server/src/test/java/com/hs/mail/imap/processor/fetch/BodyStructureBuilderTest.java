package com.hs.mail.imap.processor.fetch;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.apache.james.mime4j.MimeException;
import org.junit.BeforeClass;
import org.junit.Test;

public class BodyStructureBuilderTest {

	private static EnvelopeBuilder envelopeBuilder;
	private static BodyStructureBuilder bodyStructureBuilder;
	
	@BeforeClass
	public static void oneTimeSetUp() {
		envelopeBuilder = new EnvelopeBuilder();
		bodyStructureBuilder = new BodyStructureBuilder(envelopeBuilder);
	}
	
	@Test
	public void testBuild() throws IOException, MimeException {
		InputStream input = getClass().getResourceAsStream("/321");
		MimeDescriptor descriptor = bodyStructureBuilder.build(input);
		assertNotNull(descriptor);
	}

}
