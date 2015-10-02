<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%-- Import taglibs --%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<link rel="stylesheet" type="text/css" href="css/elrte.min.css"></link>
<script src="js/elrte.min.js"></script>
<script>
$(function() {
  $("input[name=senddate]").datepicker();
  $("#att").MultiFile({
    list:'#att-list',STRING:{remove:'<i class="icon-remove"></i>'}
  });
  $("#elrte").elrte({toolbar:'compact'});
});
</script>
<div class="btn-toolbar">
  <div class="btn-group">
    <a class="btn"><fmt:message key="menu.sendmail"/></a>
    <a class="btn"><fmt:message key="menu.savedraft"/></a>
  </div>
  <div class="input-append input-prepend pull-right">
    <a id="close" class="btn"><i class="icon-th-list"></i></a>
  </div>
</div><!--/.btn-toolbar-->
<div class="page-header">
  <form name="compose-form" class="form-horizontal">
    <div class="control-group">
      <label class="control-label" for="subject"><fmt:message key="message.subject"/></label>
      <div class="controls">
        <input id="subject" type="text" class="span9"/>
      </div>
    </div>
    <div class="control-group">
      <label class="control-label" for="to"><fmt:message key="message.to"/></label>
      <div class="controls controls-row">
        <input type="text" class="span2"/>
        <input id="to" type="text" class="span7"/>
        <a href="#" class="btn btn-link"><i class="icon-plus"></i></a>
      </div>
    </div>
    <div class="control-group">
      <label class="control-label">Options</label>
      <div class="controls">
        <label class="checkbox inline"><input type="checkbox"><fmt:message key="message.secure"/></label>
        <label class="checkbox inline"><input type="checkbox"><fmt:message key="message.urgent"/></label>
        <label class="checkbox inline"><input type="checkbox"><fmt:message key="preferences.autoarchive"/></label>
        <label class="checkbox inline"><input type="checkbox"><fmt:message key="message.defersend"/></label>
        <input class="input-small inline" type="text" name="senddate" readonly="readonly" value="2013-04-20" />
        <select class="input-mini inline">
          <option>12</option>
        </select>
        <select class="input-mini inline">
          <option>00</option>
        </select>
      </div>
    </div>
    <div class="control-group">
      <label class="control-label">Attach</label>
      <div class="controls">
        <span class="btn btn-small btn-file"><i class="icon-plus"></i>
        <input type="file" id="att" class="multi" /></span>
        <span id="att-list"></span>
      </div>
    </div>
    <div>
      <textarea id="elrte" style="width: 100%; height: 300px;" placeholder="Enter text ..."><p></p></textarea>
    </div>
  </form>
</div>