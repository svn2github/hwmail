<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="wrapper-content">
	<div class="page-header">
		<h2><fmt:message key="message.list.aliases"/> <small>${domain}</small></h2>
		<div class="page-tools">
			<form class="pull-right page-search">
				<div class="input-group">
					<input type="text" class="form-control input-sm" name="search" placeholder="Search"/>
					<div class="input-group-btn">
	                	<button type="submit" class="btn btn-sm btn-primary">Search</button>
	            	</div>
				</div>
			</form>
			<button id="create" class="btn btn-default btn-sm"><fmt:message key="menu.new"/></button>
			<button id="delete" class="btn btn-default btn-sm"><fmt:message key="menu.delete"/></button>
		</div>
	</div>
	<form id="alias-form">
		<input type="hidden" id="page" value="<c:out value='${pager.pageNumber}' />"/>
		<table id="alias-table" class="table table-hover table-condensed">
			<thead>
				<tr>
					<th><input type="checkbox"/></th>
					<th><fmt:message key="alias.alias"/></th>
					<th><fmt:message key="alias.redirect.to"/></th>
					<th>&nbsp;</th>
				</tr>
			</thead>
			<tbody>
			  <c:if test="${not empty aliases}">
			   <c:forEach var="alias" items="${aliases}">
				<tr>
					<td><input type="checkbox" name="IDs" value="<c:out value='${alias.ID}'/>"/></td>
					<td><a href="#"><c:out value="${alias.alias}"/></a></td>
					<td><c:out value="${alias.userID}"/></td>
					<td>&nbsp;</td>
				</tr>
			   </c:forEach>
			  </c:if>
			  <c:forEach begin="${fn:length(aliases) + 1}" end="${pager.pageSize}">
			  	<tr>
			  		<td colspan="4">&nbsp;</td>
			  	</tr>
			  </c:forEach>
			</tbody>
		</table>
	</form>
	<div class="text-center">
		<ul class="pagination" id="pagination"></ul>
	</div>
</div>

<script>
$(function() {
    $('#pagination').pagination(<c:out value="${pager.itemCount}" />, {
    	items_per_page: <c:out value="${pager.pageSize}" />,
        current_page: $('#page').val(),
        callback: gotopage
    });
});
</script>