<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="form-control form-table" style="height: 220px; overflow-y: auto;">
	<table id="acl-table" class="table table-condensed">
		<thead>
			<tr>
				<th>Email</th>
				<th>Access Level</th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${not empty acl}">
				<c:forEach var="ace" items="${acl.entries}">
					<tr>
						<td><c:out value="${ace.identifier}"/></td>
						<td><c:out value="${ace.rights}"/></td>
					</tr>
				</c:forEach>
			</c:if>
			<tr id="ace-template" class="hidden">
				<td></td>
				<td></td>
			</tr>
		</tbody>
	</table>
</div>
<form name="access-control" style="margin-top: 20px;">
	<div class="form-group">
	</div>
	<div class="form-group">
		<label>Email address</label>
		<div class="form-inline">
			<div class="form-group">
				<input type="text" class="form-control" id="identifier" name="identifier" placeholder="Email"/>
			</div>
			<button type="button" id="acl-add" class="btn btn-default">Add</button>
			<button type="button" id="acl-remove" class="btn btn-default">Remove</button>
		</div>
	</div>
	<div class="form-group">
		<label>Access rights</label>
		<div class="form-group">
			<label class="checkbox-inline col-sm-3">
	  			<input type="checkbox" name="right" value="l" checked> Lookup
			</label>
			<label class="checkbox-inline col-sm-3">
	  			<input type="checkbox" name="right" value="i"> Insert
			</label>
			<label class="checkbox-inline col-sm-3">
	  			<input type="checkbox" name="right" value="s"> Set Seen Flag
			</label>
			<label class="checkbox-inline col-sm-3">
	  			<input type="checkbox" name="right" value="r" checked> Read
			</label>
			<label class="checkbox-inline col-sm-3">
	  			<input type="checkbox" name="right" value="k"> Create
			</label>
			<label class="checkbox-inline col-sm-3">
	  			<input type="checkbox" name="right" value="a"> Administer
			</label>
			<label class="checkbox-inline col-sm-3">
	  			<input type="checkbox" name="right" value="w"> Write
			</label>
			<label class="checkbox-inline col-sm-3">
	  			<input type="checkbox" name="right" value="xte"> Delete
			</label>
			<label class="checkbox-inline col-sm-3">
	  			<input type="checkbox" name="right" value="p"> Post
			</label>
			<div class="clearfix"></div>
		</div>
	</div>
</form>

<script>
$(function() {
	rights = {};

	$('#acl-table').on('click', 'tbody > tr', function() {
		var row = $(this);
		if (!row.hasClass('selected')) {
			row.parent().find('tr.selected').removeClass('selected');
			row.addClass('selected');

			var identifier = row.find('td:eq(0)').text();
			var right = row.find('td:eq(1)').text();
			$('#identifier').val(identifier);
			$('form[name=access-control]').find('input[name=right]').each(function() {
				$(this).prop('checked', right.indexOf($(this).val()) != -1);
			});
		}
	});

    $('#acl-add').on('click', function() {
    	var identifier = $.trim($('#identifier').val());
    	if (identifier == '') {
    		$('#identifier').closest('.form-group').addClass('has-error');
    		return;
    	}

		$('#identifier').closest('.form-group').removeClass('has-error');
    	
    	var right = '';
    	$('form[name=access-control]').find('input[name=right]:checked').each(function() {
    		right += $(this).val();
    	});

    	var found = false;
    	$('#acl-table').find('tbody > tr').each(function() {
    		// if already exist
    		if ($(this).find('td:eq(0)').text() == identifier) {
    			found = true;
    			var row = $(this);
    			row.parent().find('tr.selected').removeClass('selected');
    			row.find('td:eq(1)').text(right);
    			row.addClass('selected');
    			return false;
    		}
    	});
    	
    	if (!found) {
	    	var row = $('#ace-template').clone().removeClass('hidden').addClass('selected').removeAttr('id');
	    	row.find('td:eq(0)').text(identifier);
	    	row.find('td:eq(1)').text(right);
	    	$('#ace-template').parent().find('tr.selected').removeClass('selected').end().append(row);
    	}

    	rights[identifier] = right;
    });

    $('#acl-remove').on('click', function() {
    	var row = $('#acl-table').find('tr.selected');
    	if (row.length) {
    		if (row.next().length) row.next().trigger('click');
    		else if (row.prev().length) row.prev().trigger('click');
    		
    		rights[row.find('td:eq(0)').text()] = '';
    		row.remove();
    	}
    });

});
</script>