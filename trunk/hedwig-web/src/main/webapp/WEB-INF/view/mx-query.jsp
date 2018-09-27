<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="wrapper-content">
	<div class="page-header">
		<h2>MX-query</h2>
	</div>
	<form>
		<div class="form-group">
			<label for="domain">Domain</label>
			<input type="text" class="form-control" id="domain" style="width: 300px;" />
		</div>
		<div class="form-group">
			<button id="resolve" class="btn btn-default">Resolve</button>
		</div>
		<div class="form-group">
			<label>Mail servers</label>
			<table id="addresses" class="table table-hover table-condensed">
				<thead>
					<tr>
						<th>IP Address</th>
						<th>Host name</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
	</form>
</div>

<script>
$(function() {
	$('#resolve').on('click', function() {
		var that = $(this),
		    domain = $('#domain').val();
		that.prop('disabled', true);
		  $.post('utils/mx-query/resolve', { domain: domain }, function(data) {
		  		var html = '';
		  		$('#addresses > tbody').empty();
		  		$.each(data, function(index, value) {
		  			html += '<tr><td>' + value.hostAddress + '</td>'
		  				  + '<td>' + value.hostName + '</td></tr>';
		  		});
		  		$('#addresses > tbody').html(html);
	  			that.prop('disabled', false);
			});
	});
});
</script>