<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form class="form-horizontal" method="post" 
	name="userForm" modelAttribute="userForm">

	<form:hidden path="ID" />
	
	<spring:bind path="localPart">
		<div class="form-group ${status.error ? 'has-error' : ''}">
			<label class="col-sm-2 control-label">Name</label>
			<div class="col-sm-10">
				<div class="input-group">
					<form:input path="localPart" class="form-control" id="localPart" placeholder="Name" />
					<div class="input-group-addon">@${userForm.domain}</div>
				</div>
				<form:errors path="localPart" class="control-label" />
			</div>
		</div>
	</spring:bind>

	<div class="form-group">
		<label class="col-sm-2 control-label">Password</label>
		<div class="col-sm-10">
			<form:password path="password" showPassword="true" class="form-control" id="password" placeholder="Password" />
		</div>
	</div>

	<div class="form-group">
		<label class="col-sm-2 control-label">Size (MB)</label>
		<div class="col-sm-3">
			<p class="form-control-static"><fmt:formatNumber value='${userForm.size / (1024 * 1024)}' maxFractionDigits='1' /></p>
		</div>
		<label class="col-sm-3 control-label">Quota (MB)</label>
		<div class="col-sm-4">
			<form:input path="quota" class="form-control" />
		</div>
	</div>

	<spring:bind path="forwardTo">
		<div class="form-group ${status.error ? 'has-error' : ''}">
			<label class="col-sm-2 control-label">Forward</label>
			<div class="col-sm-10">
				<form:input path="forwardTo" class="form-control" placeholder="Forward" />
				<form:errors path="forwardTo" class="control-label" />
			</div>
		</div>
	</spring:bind>

</form:form>
