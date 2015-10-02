<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Hedwig Web Console</title>
<link rel="stylesheet" href="css/bootstrap.min.css"/>
<link rel="stylesheet" href="css/default.css"/>
<script src="js/jquery/jquery.js"></script>
<script src="js/bootstrap.min.js"></script>
<script>
$(function() {
});
</script>
</head>
<body>
	<div class="middle-box text-center login-screen">
		<div>
			<h3>Welcome to Hedwig</h3>
			<p class="text-muted">Login in. To manage it.</p>
			<form action="login" method="post" enctype="application/x-www-form-urlencoded">
				<input type="hidden" name="facility" value="PropertiesLogin"/>
				<div class="form-group">
					<input type="text" name="username" class="form-control" placeholder="Username" required/>
				</div>
				<div class="form-group">
					<input type="password" name="password" class="form-control" placeholder="Password" required/>
				</div>
				<button type="submit" class="btn btn-primary full-width">Login</button>
			</form>
		</div>
	</div>
</body>
</html>
