<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<title>Sign in</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="css/bootstrap-responsive.css" rel="stylesheet">
<link href="css/login.css" rel="stylesheet">
<script src="js/jquery/jquery.js"></script>
<script src="js/bootstrap.min.js"></script>
</head>
<body>
	<div class="container">
		<form action="login" method="post" class="form-signin">
			<h2 class="form-signin-heading">Please sign in</h2>
			<input name="username" type="text" class="input-block-level" placeholder="Email address">
			<input name="password" type="password" class="input-block-level" placeholder="Password">
			<label class="checkbox">
				<input type="checkbox" value="remember-me"> Remember me
			</label>
			<button class="btn btn-large btn-primary" type="submit">Sign in</button>
		</form>
	</div> <!-- /container -->
</body>
</html>