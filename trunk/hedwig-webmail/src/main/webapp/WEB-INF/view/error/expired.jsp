<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="container-fluid error-page">
	<h1><i class="fa fa-times-circle"></i> 401</h1><strong>Session Expired</strong>
	<p>Your session has expired. Please login again to continue working.</p>
	<div>
		<button type="button" class="btn btn-success btn-xs" onclick="login();">Sign in</button>
	</div>
</div>
<script>
	function login() {
		var page = "logout";
		if (opener) {
			if (opener.top) opener.top.location.href = page;
			else opener.document.location.href = page;
			self.close();
			return;
		}
		if (top) top.location.href = page;
	}
</script>