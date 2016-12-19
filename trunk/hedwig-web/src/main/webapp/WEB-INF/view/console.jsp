<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Hedwig Web Console</title>
<link rel="stylesheet" href="css/bootstrap.min.css"/>
<link rel="stylesheet" href="css/font-awesome.min.css"/>
<link rel="stylesheet" href="css/jquery-ui.css"/>
<link rel="stylesheet" href="css/skin-lion/ui.fancytree.css"/>
<link rel="stylesheet" href="css/default.css"/>
<script src="js/jquery/jquery.js"></script>
<script src="js/jquery/jquery-ui.js"></script>
<script src="js/jquery/jquery.fancytree.js"></script>
<script src="js/jquery/jquery.pagination.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/eModal.js"></script>
<script>
function gotopage(page) {
	$('#main-contents').load($('#main-form').attr('action') + '?page=' + page);
}
$(function() {
	$('#tree').fancytree({
		activate: function(event, data) {
			var path = data.node.getKeyPath().substring(1);
			console.log(path);
			if (!data.node.unselectable) {
				$('#main-contents').load(path, function() {
					$('#main-form').attr('action', path);
				});
			}
		}
	});
});
</script>
</head>
<body>
	<header>
		<form id="main-form">
			<nav class="navbar navbar-default">
				<div class="container-fluid">
					<div class="navbar-header">
						<a class="navbar-brand" href="http://hwmail.sourceforge.net">
							<!--<img src="img/xdev_logo.png"/>-->
						</a>
					</div>
					<p class="navbar-text navbar-right">
						<a href="logout">Logout</a>
					</p>
				</div>
			</nav>
	  	</form>
	</header>

	<div class="container">
		<div class="row">
			<div class="col-md-3 no-float">
				<div id="tree">
					<ul style="display: none;">
						<li>Welcome</li>
						<li id="domains" class="expanded unselectable folder">Domains
							<ul>
							  <c:forEach var="domain" items="${domains}">
								<li id="<c:out value="${domain}"/>" class="expanded unselectable folder"><c:out value="${domain}"/>
									<ul>
										<li id="accounts">Accounts</li>
										<li id="aliases">Aliases</li>
									</ul>
								</li>
							  </c:forEach>
							</ul>
						</li>
						<li id="settings" class="expanded unselectable folder">Settings
							<ul>
							  <c:if test="${not empty namespaces}">
								<li id="namespaces" class="expanded unselectable folder">Namespaces
									<ul>
							  		  <c:forEach var="namespace" items="${namespaces}">
										<li id="<c:out value="${namespace}"/>">#<c:out value="${namespace}"/></li>
									  </c:forEach>
									</ul>
								</li>
							  </c:if>
							</ul>
						</li>
						<li id="utils" class="expanded unselectable folder">Utilities
							<ul>
								<li>MX-query</li>
								<li>Server sendout</li>
								<li>Diagnostics</li>
							</ul>
						</li>
					</ul>
				</div>
			</div>
			<div id="main-contents" class="col-md-9 no-float">
				<div class="wrapper-content"></div>
			</div>
		</div>
	</div>
</body>
</html>