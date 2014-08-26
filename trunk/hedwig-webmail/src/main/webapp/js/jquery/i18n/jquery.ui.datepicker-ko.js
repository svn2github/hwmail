/* Korean initialisation for the jQuery calendar extension. */
/* Written by DaeKwon Kang (ncrash.dk@gmail.com). */
jQuery(function($){
	$.datepicker.regional['ko'] = {
		closeText: '닫기',
		buttonText: '날짜선택',
		yearText: ' 년',
		prevText: '이전달',
		nextText: '다음달',
		currentText: '오늘',
		monthNames: ['1 월','2 월','3 월','4 월','5 월','6 월','7 월','8 월','9 월','10 월','11 월','12 월'],
		monthNamesShort: ['1 월','2 월','3 월','4 월','5 월','6 월','7 월','8 월','9 월','10 월','11 월','12 월'],
		dayNames: ['일','월','화','수','목','금','토'],
		dayNamesShort: ['일','월','화','수','목','금','토'],
		dayNamesMin: ['일','월','화','수','목','금','토'],
		weekHeader: '주',
		dateFormat: 'yy.mm.dd',
		firstDay: 0,
		isRTL: false,
		showMonthAfterYear: true
	};
	$.datepicker.setDefaults($.datepicker.regional['ko']);
});