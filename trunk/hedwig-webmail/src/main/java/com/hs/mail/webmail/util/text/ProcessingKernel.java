package com.hs.mail.webmail.util.text;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

public class ProcessingKernel implements InitializingBean {
	
	private Properties properties;

	private Map<String, Processor> processors;

	private Map<String, ProcessingPipe> pipes;

	public void setProperties(Properties props) {
		this.properties = props;
	}

	public Processor getProcessor(String name) {
		return processors.get(name);
	}

	public ProcessingPipe getProcessingPipe(String name) {
		return pipes.get(name);
	}

    @Override
	public void afterPropertiesSet() throws Exception {
		if (properties != null) {
			loadProcessors();
			loadProcessingPipes();
		}
	}

	private void loadProcessors() throws Exception {
		String[] names = StringUtils.split(properties.getProperty("processors"), ",");
		if (ArrayUtils.isEmpty(names)) {
			throw new Exception("Properties don't specify any processors.");
		}

		processors = new HashMap<String, Processor>();
		for (String name : names) {
			String className = properties.getProperty("processor." + name + ".class");
			Processor processor = (Processor) Class.forName(className).newInstance();
			processors.put(name, processor);
		}
	}

	private void loadProcessingPipes() {
		String[] tokens = StringUtils.split(properties.getProperty("pipes"), ",");
		pipes = new HashMap<String, ProcessingPipe>();
		if (ArrayUtils.isEmpty(tokens)) {
			return;
		}

		for (String token : tokens) {
			ProcessingPipe pipe = new ProcessingPipe();
			String sequence = properties.getProperty("pipe." + token + ".sequence");
			String[] names = StringUtils.split(sequence, ",");
			for (String name : names) {
				Processor processor = getProcessor(name);
				if (processor != null) {
					pipe.addProcessor(processor);
				}
			}
			pipes.put(token, pipe);
		}		
	}

}
