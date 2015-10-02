<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form class="form-horizontal" method="post" 
	name="userForm" modelAttribute="userForm">

	<form:hidden path="ID" />
	
	<spring:bind path="aliasName">
		<div class="form-group">
			<label class="col-sm-2 control-label"><fmt:message key="alias.alias"/></label>
			<div class="col-sm-10">
				<form:input path="aliasName" showPassword="true" class="form-control" placeholder="Alias" />
			</div>
		</div>
	</spring:bind>

	<spring:bind path="userID">
		<div class="form-group ${status.error ? 'has-error' : ''}">
			<label class="col-sm-2 control-label"><fmt:message key="alias.redirect.to"/></label>
			<div class="col-sm-10">
				<form:input path="userID" class="form-control" placeholder="Forward" />
				<form:errors path="userID" class="control-label" />
			</div>
		</div>
	</spring:bind>

</form:form>
