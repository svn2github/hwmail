package com.hs.mail.imap.processor.fetch;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.james.mime4j.stream.MimeConfig;
import org.apache.james.mime4j.stream.MimeTokenStream;
import org.apache.james.mime4j.stream.RecursionMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class MessageTree {
	
    private static final String INDENT_STRING = "    ";

	private MimeTokenStream parser;

	public MessageTree() {
		MimeConfig config = MimeConfig.custom().setMaxLineLen(-1).setMaxHeaderLen(-1).build();
		parser = new MimeTokenStream(config, new DefaultBodyDescriptorBuilder());
	}
	
	public void print(InputStream is) {
		try {
			parser.setRecursionMode(RecursionMode.M_RECURSE);
			parser.parse(is);
			verbosePrint(System.out, 0);
		} catch (Exception ex) {
		}
	}
	
	private void verbosePrint(final PrintStream out, int depth) throws IOException, MimeException {
		EntityState state = parser.next();
		while (state != EntityState.T_END_OF_STREAM) {
			switch (state) {
			case T_START_MULTIPART :
			case T_START_BODYPART :
				printIndent(out, depth);
				out.println(MimeTokenStream.stateToString(state));
				verbosePrint(out, depth + 1);
				break;
			case T_END_MULTIPART :
			case T_END_BODYPART :
				printIndent(out, depth - 1);
				out.println(MimeTokenStream.stateToString(state));
				return;
			case T_BODY :
				printIndent(out, depth);
				out.println(MimeTokenStream.stateToString(state));
				break;
			default:
				break;
			}
			state = parser.next();
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
