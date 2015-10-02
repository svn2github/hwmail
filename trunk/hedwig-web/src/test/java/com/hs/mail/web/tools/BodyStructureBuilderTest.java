package com.hs.mail.web.tools;

import java.io.File;

import org.junit.Test;

public class BodyStructureBuilderTest {

	@Test
	public void test() {
		BodyStructureBuilder builder = new BodyStructureBuilder();
		builder.setDataDirectory(new File("D:\\hedwig-0.6\\data\\mail"));
		builder.build();
	}

}
