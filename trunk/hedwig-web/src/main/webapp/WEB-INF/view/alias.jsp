<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form class="form-horizontal" method="post" 
	name="userForm" modelAttribute="userForm">

	<form:hidden path="ID" />
	
	<spring:bind path="aliasName">
		<div class="form-group ${status.error ? 'has-error' : ''}">
			<label class="col-sm-2 control-label">Alias</label>
			<div class="col-sm-10">
				<div class="input-group">
					<form:input path="aliasName" showPassword="true" class="form-control" placeholder="Alias" />
					<div class="input-group-addon">@${userForm.domain}</div>
				</div>
				<form:errors path="aliasName" class="control-label" />
			</div>
		</div>
	</spring:bind>

	<spring:bind path="deliverTo">
		<div class="form-group ${status.error ? 'has-error' : ''}">
			<label class="col-sm-3 control-label">Redirect to</label>
			<div class="col-sm-9">
				<form:input path="deliverTo" class="form-control" placeholder="Redirect to" />
				<form:errors path="deliverTo" class="control-label" />
			</div>
		</div>
	</spring:bind>

</form:form>
