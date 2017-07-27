package com.hs.mail.webmail.util.text;

import java.util.ArrayList;
import java.util.List;

public class ProcessingPipe implements Processor {
	
	private List<Processor> processors;

	public ProcessingPipe() {
		this.processors = new ArrayList<Processor>(5);
	}
	
	public void addProcessor(Processor processor) {
		processors.add(processor);
	}
	
	public String process(String type, String text) {
		String str = text;
		for (Processor processor : processors) {
			str = processor.process(type, str);
		}
		return str;
	}

}
