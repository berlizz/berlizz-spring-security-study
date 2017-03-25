<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<c:if test="${param.fail eq 'true'}">
	<script>
		alert("id or password do not match");
		$("#loginId").focus();
	</script>
</c:if>

<form id="loginForm" name="loginForm" action="/loginCheck" method="post">
	<!-- csrf공격을 막기 위해서 구별되는 csrf 토큰 값을 같이 전달해야 한다. -->
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
	<table>
		<tr>
			<td>ID</td>
			<td><input type="text" id="loginId" name="loginId"></td>
		</tr>
		<tr>
			<td>PASSWORD</td>
			<td><input type="password" id="loginPw" name="loginPw"></td>
		</tr>
		<tr>
			<td colspan="2"><input type="submit" id="loginBtn"></td>
		</tr>
		
		<c:if test="${not empty param.fail}">
		<tr>
			<td colspan="2">
				<p style="color:red">Your login attempt was not successful, try again.</p>
				<p style="color:red">Reason : ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</p>
			</td>
		</tr>
		</c:if>
		
	</table>
</form>




<script src="/resources/jquery/jquery-3.1.1.min.js"></script>
</body>
</html>