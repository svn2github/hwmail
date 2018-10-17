package com.hs.mail.container.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public class HedwigVersion {

    private static final String VERSION = "0.7";

    private static final String IMPLEMENTATION;

    static {
        String impl = null;
		final Class<?> myClass = HedwigVersion.class; 
    	try {
    		InputStream input = myClass.getResourceAsStream("META-INF/MANIFEST.MF");
    		if (input != null) {
    			Properties props = new Properties();
    			try {
    				props.load(input);
    				impl = props.getProperty("Implementation-Version");
    			} finally {
    				IOUtils.closeQuietly(input);
    			}
    		}
    	} catch (IOException ioe) {
    		// Ignored
    	}
    	if (impl == null) {
    		IMPLEMENTATION = VERSION;
    	} else {
    		IMPLEMENTATION = impl;
    	}
    }

	private HedwigVersion() { // Not instantiable 
		super();
	}

    public static String getVERSION() {
        return IMPLEMENTATION;
    }

}
