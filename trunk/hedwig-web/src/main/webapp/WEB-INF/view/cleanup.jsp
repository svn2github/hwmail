<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="wrapper-content">
	<div class="page-header">
		<h2>Cleanup</h2>
	</div>
	<form>
		<div class="form-group">
			<label for="delete-orphans">Delete orphan messages</label>
			<div>
				<button id="delete-orphans" class="btn btn-default">Start</button>
			</div>
		</div>
		<div class="form-group">
			<label>Delete header values</label>
			<table id="headernames-table" class="table table-hover table-condensed">
				<tbody>
				  <c:if test="${not empty counts}">
				   <c:forEach var="count" items="${counts}" varStatus="status">
					<tr>
						<td><button class="btn btn-default btn-xs" value="<c:out value='${count.HEADERNAMEID}'/>"><i class="fa fa-trash"></i></button></td>
						<td><c:out value="${count.HEADERNAME}"/></td>
						<td align="right"><fmt:formatNumber type="number" maxFractionDigits="3" value="${count.COUNT}"/></td>
					</tr>
				   </c:forEach>
				  </c:if>
				</tbody>
			</table>
		</div>
	</form>
</div>

<script>
$(function() {
	$('#delete-orphans').on('click', function() {
		var that = $(this);
		that.prop('disabled', true);
		$.post('utils/cleanup/messages')
			.always(function() {
				that.prop('disabled', false);
			});
	});
	$('#headernames-table').on('click', 'button', function(e) {
		var that = $(this);
    	$.post('utils/cleanup/header/', + that.val(), 
	    	function() {
				that.closest('tr').remove();
	    	});
		return false;
	});
});
</script>