package com.hs.mail.imap.message;

import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.codec.DecoderUtil;
import org.junit.Test;

public class DecodeTest {

	@Test
	public void testMain() {
		String[] strings = {
				"=?EUC-KR?Q?=BF=AC(HB)20110805-003\r\n[hedwig]_SMTP=C1=D7=B4=C2=B9=AE=C1=A6?=",
				"=?utf-8?B?UmU6Tm8gU3ViamVjdA==?=" };
		for (String encoded : strings) {
			String decoded = DecoderUtil.decodeEncodedWords(encoded, DecodeMonitor.SILENT);
			System.out.println(decoded);
		}
	}

}
