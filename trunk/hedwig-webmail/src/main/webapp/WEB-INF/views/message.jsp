<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%-- Import taglibs --%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<div class="btn-toolbar">
  <div class="btn-group">
    <a class="btn"><fmt:message key="menu.reply"/></a>
    <a class="btn"><fmt:message key="menu.forward"/></a>
  </div>
  <div class="input-append input-prepend pull-right">
    <a class="btn"><i class="icon-chevron-up"></i></a>
    <a class="btn"><i class="icon-chevron-down"></i></a>
    <a id="close" class="btn"><i class="icon-th-list"></i></a>
  </div>
</div><!--/.btn-toolbar-->
<div class="page-header">
  <h4><c:out value="${message.subject}"/></h4>
  <div class="media">
    <a href="#" class="pull-left">
      <img class="media-object" data-src="js/holder.js/64x64" />
    </a>
    <div class="media-body">
      <div><c:out value="${message.from}"/> <fmt:formatDate value="${message.receivedDate}" pattern="yyyy.MM.dd HH:mm"/></div>
    </div>
  </div>
  <hr/>
  <div>
<c:out value="${message.body}" escapeXml="false"/>
  </div>
</div>