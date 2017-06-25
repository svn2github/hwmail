package com.hs.mail.imap.message;

import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.codec.DecoderUtil;
import org.junit.Test;

public class DecodeTest {

	@Test
	public void testMain() {
		String[] strings = {
				"=?EUC-KR?Q?=BF=AC(HB)20110805-003[hedwig]_SMTP=C1=D7=B4=C2=B9=AE=C1=A6?=",
				"=?utf-8?B?UmU6Tm8gU3ViamVjdA==?=",
				"=?EUC-KR?Q?=BF=AC(HB)20110916-003_[hed?= wig] 기 문의사항 처리현황 및 재문의",
				"=?EUC-KR?Q?=BF=AC(HB)20110922-002_Failed_to_l?= oad IMAP envelope 에 대한 문의입니다.",
				"JS1(HB)20120308-003 [회 =?EUC-KR?Q?=C0=C7]_3/8_=B8=DE=C0=CF=BC=BA=B4=C9?=  문제 등 - 회의록",
				"[IRIS] [01]연구소_사업지 =?UTF-8?Q?=EC=9B=90=EC=8B=A4_>_PO=5F2013.12.13?=",
				"[ZEUS] Commented: (SGBOARD-4588) 15 =?UTF-8?Q?=EB=85=84_=ED=95=98=EB=B0=98?= =?UTF-8?Q?=EA=B8=B0_=EC=A0=95=EB=B3=B4=EC=9D=B8?= =?UTF-8?Q?=ED=94=84=EB=9D=BC_=EB=8F=84=EC=9E=85_=EC=82=AC=EC=97=85_?= =?UTF-8?Q?=EC=9A=94=EA=B5=AC=EC=82=AC=ED=95=AD?=  검토 및 인력 준비 요청"};
		for (String encoded : strings) {
			String decoded = DecoderUtil.decodeEncodedWords(encoded, DecodeMonitor.SILENT);
			System.out.println(decoded);
		}
	}

}
