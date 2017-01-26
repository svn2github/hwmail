<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:if test="${not empty error}">
	<div class="alert alert-warning alert-dismissible" role="alert">
  		<button type="button" class="close" data-dismiss="alert" aria-label="Close">
  			<span aria-hidden="true">&times;</span>
  		</button>
  		<strong>Error!</strong> <c:out value="${error.message}" />
	</div>
</c:if>

<form:form class="form-horizontal" method="post" 
	name="fetchForm" modelAttribute="fetchForm">

	<form:hidden path="userID" />
	
	<div class="form-group">
		<label class="col-sm-3 control-label">Protocol</label>
		<div class="col-sm-9">
				<form:select path="protocol" class="form-control">
					<form:option value="pop3" label="POP3"/>
				</form:select>
		</div>
	</div>

	<spring:bind path="serverName">
		<div class="form-group ${status.error ? 'has-error' : ''}">
			<label class="col-sm-3 control-label">Server address</label>
			<div class="col-sm-5">
				<form:input path="serverName" class="form-control" placeholder="Server" />
				<form:errors path="serverName" class="control-label" />
			</div>
			<label class="col-sm-2 control-label">Port</label>
			<div class="col-sm-2">
				<form:input path="port" class="form-control" placeholder="Port" />
			</div>
		</div>
	</spring:bind>

	<spring:bind path="userName">
		<div class="form-group ${status.error ? 'has-error' : ''}">
			<label class="col-sm-3 control-label">User name</label>
			<div class="col-sm-9">
				<form:input path="userName" class="form-control" placeholder="ID" />
				<form:errors path="userName" class="control-label" />
			</div>
		</div>
	</spring:bind>

	<spring:bind path="password">
		<div class="form-group ${status.error ? 'has-error' : ''}">
			<label class="col-sm-3 control-label">Password</label>
			<div class="col-sm-9">
				<form:password path="password" showPassword="true" class="form-control" placeholder="Password" />
				<form:errors path="password" class="control-label" />
			</div>
		</div>
	</spring:bind>

	<div class="form-group">
		<label class="col-sm-3 control-label" />
		<div class="col-sm-9">
			<label class="checkbox-inline">
				<form:checkbox path="useSSL" /> Use SSL
			</label>
			<label class="checkbox-inline">
				<form:checkbox path="autoEmpty" /> Delete messages immediately
			</label>
		</div>
	</div>

</form:form>
