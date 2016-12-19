<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form class="form-horizontal" method="post" 
	name="userForm" modelAttribute="userForm">

	<form:hidden path="namespace" />
	
	<spring:bind path="name">
		<div class="form-group ${status.error ? 'has-error' : ''}">
			<label class="col-sm-2 control-label">Name</label>
			<div class="col-sm-10">
				<form:input path="name" class="form-control" id="name" placeholder="Name" />
				<form:errors path="name" class="control-label" />
			</div>
		</div>
	</spring:bind>

</form:form>
