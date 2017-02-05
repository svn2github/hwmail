package com.hs.mail.imap.processor.fetch;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.james.mime4j.MimeException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.hs.mail.util.FileUtils;

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
		Resource normal = new ClassPathResource("/simpleMail.eml");
		MimeDescriptor descriptorNormal = bodyStructureBuilder.build(normal.getInputStream());
		
		File zipped = File.createTempFile("hwm", ".zip");
		FileUtils.compress(normal.getFile(), zipped);
		MimeDescriptor descriptorZipped = bodyStructureBuilder
				.build(new GZIPInputStream(new FileInputStream(zipped)));
		zipped.delete();
			
		assertNotNull(descriptorNormal);
		assertTrue(EqualsBuilder.reflectionEquals(descriptorNormal, descriptorZipped));
		assertTrue(descriptorNormal.getBodyOctets() == 1930
				&& descriptorNormal.getLines() == 25
				&& "text".equals(descriptorNormal.getType())
				&& "html".equals(descriptorNormal.getSubType()));
	}

}
