<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="wrapper-content">
	<div class="page-header">
		<h2><fmt:message key="message.list.accounts"/> <small>${domain}</small></h2>
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
			<button id="import" class="btn btn-default btn-sm"><fmt:message key="menu.import"/></button>
			<button id="fetch" class="btn btn-default btn-sm"><fmt:message key="menu.fetch"/></button>
		</div>
	</div>
	<form id="account-form">
		<input type="hidden" id="page" value="<c:out value='${pager.pageNumber}' />"/>
		<table id="account-table" class="table table-hover table-condensed">
			<thead>
				<tr>
					<th><input type="checkbox"/></th>
					<th><fmt:message key="account.address"/></th>
					<th><fmt:message key="account.quota"/></th>
					<th>&nbsp;</th>
				</tr>
			</thead>
			<tbody>
			  <c:if test="${not empty users}">
			   <c:forEach var="user" items="${users}">
				<tr>
					<td><input type="checkbox" name="ID" value="<c:out value='${user.ID}'/>"/></td>
					<td><a href="#"><c:out value="${user.userID}"/></a></td>
					<td><c:out value="${user.quota}"/> MB</td>
					<td>&nbsp;</td>
				</tr>
			   </c:forEach>
			  </c:if>
			  <c:forEach begin="${fn:length(users) + 1}" end="${pager.pageSize}">
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

    var buttons = [
			{ text: 'Close', style: 'default', close: true },
			{ text: 'OK', style: 'primary', close: false, click: saveOrUpdateAccount }
    ];

    function saveOrUpdateAccount() {
    	$('.modal-body').load($('#main-form').attr('action'),
    		$('form[name=userForm]').serializeArray());
    }

    function fetchAccount() {
    	$('.modal-body').load($('#main-form').attr('action') + '/fetch',
    		$('form[name=fetchForm]').serializeArray());
    }

    $('#account-table').on('click', 'a', function() {
    	var uid = $(this).parents('tr:first').find('input[name=ID]').val();
    	eModal.ajax({
    		url: $('#main-form').attr('action') + '/' + uid + '/update',
    		title: 'Update Account',
    		buttons: buttons
    	});
    });

    $('#create').on('click', function() {
    	eModal.ajax({
    		url: $('#main-form').attr('action') + '/add',
    		title: 'Add Account',
    		buttons: buttons
    	});
    });

    $('#delete').on('click', function() {
		var $checked = $('input[name=ID]:checked');
		if ($checked.length > 0) {
	    	$.post($('#main-form').attr('action') + '/delete',
	    		$('#account-form').serializeArray(), function() {
	    			gotopage($('#page').val());
	    		});
    	} else return eModal.alert('Select accounts!');
    });

	$('#fetch').on('click', function() {
		var $checked = $('input[name=ID]:checked');
		if ($checked.length != 1) {
			return eModal.alert('Select an account!');
		}
		eModal.ajax({
			url: $('#main-form').attr('action') + '/' + $checked.val() + '/fetch',
			title: '<fmt:message key="fetch.account"/>',
			buttons: [
				{ text: 'Close', style: 'default', close: true },
				{ text: 'OK', style: 'primary', close: false, click: fetchAccount }
			]
		});
	});

});
</script>