<!-- 로그인 전용 페이지(권한이 필요한 페이지로 가기 전 나오는 경우의 로그인페이지, 로그인 페이지로 직접 이동한 경우 -->

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="org.springframework.security.core.Authentication" %>
<%@ page import="com.berlizz.domain.MemberInfo" %>
<%
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	
	Object principal = auth.getPrincipal();	// 인증 실패 시 anonymousUser란 문자열이 있는 String객체 리턴, 인증 성공 시 작성한 MemberInfo객체 리턴, 둘 다 수용하기 위해 Object객체가 리턴됨
	
	String name = "";
	
	if(principal != null && principal instanceof MemberInfo) {
		name = ((MemberInfo)principal).getName();
	}
	
%>

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
			<td><input type="text" id="loginId" name="loginId" value="${loginId}"></td>
		</tr>
		<tr>
			<td>PASSWORD</td>
			<td><input type="password" id="loginPw" name="loginPw" value="${loginPw}"></td>
		</tr>
		<tr>
			<td colspan="2"><input type="submit" id="loginBtn"></td>
		</tr>
		
		<!-- 세션에 저장된 에러 메시지 -->
		<c:if test="${not empty param.fail}">
		<tr>
			<td colspan="2">
				<p style="color:red">Your login attempt was not successful, try again.</p>
				<p style="color:red">Reason : ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</p>
				<p>세션에 저장된 에러 메시지</p>
			</td>
		</tr>
		</c:if>
		
		<!-- CustomAuthenticationFailureHandler에서 오는 메시지 -->
		<c:if test="${not empty securityExceptionMsg}">
		<tr>
			<td colspan="2">
				<p style="color:red">Your login attempt was not successful, try again.</p>
				<p style="color:red">Reason : ${securityExceptionMsg}</p>
				<p>CustomAuthenticationFailureHandler에서 보낸 에러 메시지</p>
			</td>
		</tr>
		</c:if>
		
	</table>
	
	<!-- loginRedirect라는 이름으로 파라미터 전달 -->
	<!-- 이 값은 로그인 화면으로 바로 온 경우는 비어있고, 로그인 실패 시 CustomAuthenticationFailureHandler 클래스에서 넣는다  -->
	<!-- 로그인 성공 시 이 값이 비어있어도 권한이 필요한 화면으로 이동가능한 이유는 로그인 화면을 보여주기 전 DefaultSavedRequest 클래스를 생성한 뒤 거기에 요청과 url을 넣어둬서 
			CustomAuthenticationSuccessHandler의 2순위 세션검사에 만족하기 때문에 해당 url로 이동이 가능하다. -->
	<input type="hidden" name="loginRedirect" value="${loginRedirect}" >
</form>




<script src="/resources/jquery/jquery-3.1.1.min.js"></script>
</body>
</html>