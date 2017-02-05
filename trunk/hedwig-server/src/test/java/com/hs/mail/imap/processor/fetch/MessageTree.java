package com.hs.mail.imap.processor.fetch;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.MimeTokenStream;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class MessageTree {
	
    private static final String INDENT_STRING = "    ";

	private MimeTokenStream parser;

	public MessageTree() {
		parser = MimeParser.createDefaultMimeParser();
	}
	
	public void print(InputStream is) {
		try {
			parser.setRecursionMode(MimeTokenStream.M_RECURSE);
			parser.parse(is);
			verbosePrint(System.out, 0);
		} catch (Exception ex) {
		}
	}
	
	private void verbosePrint(final PrintStream out, int depth) throws IOException, MimeException {
		for (int state = parser.next(); 
				state != MimeTokenStream.T_END_OF_STREAM; 
				state = parser.next()) {
			switch (state) {
				case MimeTokenStream.T_START_MULTIPART :
				case MimeTokenStream.T_START_BODYPART :
					printIndent(out, depth);
					out.println(MimeTokenStream.stateToString(state));
					verbosePrint(out, depth + 1);
					break;
				case MimeTokenStream.T_END_MULTIPART :
				case MimeTokenStream.T_END_BODYPART :
					printIndent(out, depth - 1);
					out.println(MimeTokenStream.stateToString(state));
					return;
				case MimeTokenStream.T_BODY :
					printIndent(out, depth);
					out.println(MimeTokenStream.stateToString(state));
					break;
			}
		}
	}
	
    private static void printIndent(final PrintStream out, final int indent) {
        for (int i = 0; i < indent; i++) {
            out.print(INDENT_STRING);
        }
    }

	public static void main(String[] args) throws IOException {
		Resource normal = new ClassPathResource("/nestedMultipart.eml");
		new MessageTree().print(normal.getInputStream());
	}

}
