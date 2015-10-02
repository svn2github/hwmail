<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="container-fluid error-page">
	<h1>500</h1>
	<h3>Internal Server Error</h3>
	<div class="error-desc">
<c:if test="${not empty error}">
		<c:out value="${error.message}"/>
</c:if>
	</div>
</div>