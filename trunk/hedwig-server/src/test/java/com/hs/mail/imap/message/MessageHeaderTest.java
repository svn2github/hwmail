package com.hs.mail.imap.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;

import com.hs.mail.test.TestUtil;

public class MessageHeaderTest {

	@Ignore
	public void test() throws IOException {
		File source = TestUtil.getResourceFile("/quotedPrintableWithAttach.eml");
		MessageHeader header = new MessageHeader(source);
		assertNotNull(header);
		assertEquals(header.getSubject(), "연(HB)20110922-002 Failed to load IMAP envelope 에 대한 문의입니다.");
		assertEquals(header.getFrom(), "김준기 <highbase@secuace.co.kr>");
		assertEquals(header.getFromAddress(), "highbase@secuace.co.kr");
	}

	@Ignore
	public void testBrokenSubject() throws Exception {
		MessageHeader header = newMessageHeader("/brokenSubject1.eml");
		assertEquals(header.getSubject(), "Re: [5/15]대성엘텍 PJT 기술지원 일일보고 - 배강연");
		assertEquals(header.getFrom(), "반재민 <xevi@handysoft.co.kr>");

		header = newMessageHeader("/brokenSubject2.eml");
		assertEquals(header.getSubject(), "[신청] 관리자 승인 신청 메일 확인용 설비 예약2");
		assertEquals(header.getFrom(), "피카츄 <pika@iris4.handysoft.co.kr>");

		header = newMessageHeader("/brokenSubject3.eml");
		assertEquals(header.getSubject(), "일정이 수정되었습니다-종일일정+메일알림(일간)_수정");
		assertEquals(header.getFrom(), "피카츄 <pika@iris4.handysoft.co.kr>");

		header = newMessageHeader("/brokenSubject4.eml");
		assertEquals(header.getSubject(), "[전달] ZETA ICS 추적성관리 시스템 구축 관련하여 업무협조 요청드립니??.");
		assertEquals(header.getFrom(), "이희우 <nonstop@dseltec.co.kr>");
		
		header = newMessageHeader("/brokenSubject5.eml");
		assertEquals(header.getSubject(), "Re: [검색 표준소스] UI 개선을 위한 설문조사를 실시하고자 합니다.");
		assertEquals(header.getFrom(), "youngho.kim <youngho.kim@konantech.com>");

		header = newMessageHeader("/asciiHeader.eml");
		assertEquals(header.getSubject(), "Please verify that we have the right address for you");
		assertEquals(header.getFrom(), "Apple <appleid@id.apple.com>");

		header = newMessageHeader("/notAsciiKorean.eml");
		assertEquals(header.getSubject(), "[다날] 휴대폰 결제 확인 메일입니다.");
		assertEquals(header.getFrom(), "다날<paymail@paymail.danal.co.kr>");

		header = newMessageHeader("/notAsciiMixed.eml");
		assertEquals(header.getSubject(), "Re: Big Data 관련 New Biz 아이디어 수집 논의 회의록(2013-12-01)");
		assertEquals(header.getFrom(), "이문기 <mklee@konantech.com>");
	}

	private static MessageHeader newMessageHeader(String resource)
			throws IOException {
		File source = TestUtil.getResourceFile(resource);
		return new MessageHeader(source);
	}

}
