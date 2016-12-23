<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="wrapper-content">
	<div class="page-header">
		<h2><fmt:message key="message.list.public.folders"/> <small>${namespace}</small></h2>
		<div class="page-tools">
			<button id="create" class="btn btn-default btn-sm"><fmt:message key="menu.new"/></button>
			<button id="delete" class="btn btn-default btn-sm"><fmt:message key="menu.delete"/></button>
			<button id="acl" class="btn btn-default btn-sm"><fmt:message key="menu.edit.acl"/></button>
		</div>
	</div>
	<form id="public-folder-form">
		<table id="public-folder-table" class="table table-hover table-condensed">
			<thead>
				<tr>
					<th><input type="checkbox"/></th>
					<th><fmt:message key="public.folder.name"/></th>
					<th>&nbsp;</th>
				</tr>
			</thead>
			<tbody>
			  <c:if test="${not empty folders}">
			   <c:forEach var="folder" items="${folders}">
				<tr>
					<td><input type="checkbox" name="ID" value="<c:out value='${folder.mailboxID}'/>"/></td>
					<td><a href="#"><c:out value="${folder.name}"/></a></td>
					<td>&nbsp;</td>
				</tr>
			   </c:forEach>
			  </c:if>
			</tbody>
		</table>
	</form>
</div>

<script>
var rights = {};

$(function() {

	$('#create').on('click', function() {
    	eModal.ajax({
    		url: $('#main-form').attr('action') + '/add',
    		title: 'Add Public Folder',
    		buttons: [
				{ text: 'Close', style: 'default', close: true },
				{ text: 'OK', style: 'primary', close: false, click: function() {
						$('.modal-body').load($('#main-form').attr('action'), $('form[name=userForm]').serializeArray());
					} 
				}
    		]
    	});
    });

	$('#delete').on('click', function() {
		var $checked = $('input[name=ID]:checked');
		if ($checked.length > 0) {
	    	$.post($('#main-form').attr('action') + '/delete',
	    		$('#public-folder-form').serializeArray(), function(data) {
	    			$('#main-contents').load($('#main-form').attr('action'));
	    		});
    	} else return eModal.alert('Select folders!');
	});

    $('#acl').on('click', function() {
		var $checked = $('input[name=ID]:checked');
		if ($checked.length != 1) {
			return eModal.alert('Select a folder!');
		}
		var id = $checked.val(),
		    url = $('#main-form').attr('action') + '/' + id + '/acl' 
		eModal.ajax({
			url: url,
			title: 'Access Control List',
			buttons: [
				{ text: 'Close', style: 'default', close: true },
				{ text: 'OK', style: 'primary', close: false, click: function() {
						$.post(url, rights).done(function(data) {
							console.log(data);
						});
						console.log(rights);
						eModal.close();
						rights = {};
					} 
				}
			]
		});
    });

});
</script>