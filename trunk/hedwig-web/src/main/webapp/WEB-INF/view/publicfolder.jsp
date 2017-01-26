<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form method="post" name="userForm" modelAttribute="userForm">

	<form:hidden path="mailboxID" />
	<form:hidden path="aliasID" />
	<form:hidden path="namespace" />
	
	<spring:bind path="name">
		<div class="form-group ${status.error ? 'has-error' : ''}">
			<label for="name">Name*</label>
			<form:input path="name" class="form-control" placeholder="Name" />
			<form:errors path="name" class="control-label" />
		</div>
		<div class="form-group">
			<label for="submissionAddress">Submission address</label>
			<form:input path="submissionAddress" class="form-control" placeholder="Submission address" />
			<p class="help-block">
Mail sent to this address will route directly into this folder 
(note: sender must have 'post' access)
			</p>
		</div>
	</spring:bind>

</form:form>
