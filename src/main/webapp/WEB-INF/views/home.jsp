<!-- 메인화면에 로그인 폼이 포함되어 있는 상황 -->

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %> <!-- 스프링 시큐리티 태그 라이브러리 -->

<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="org.springframework.security.core.Authentication" %>
<%@ page import="com.berlizz.domain.MemberInfo" %>
<%
	// 로그인 정보를 가져오는 방법은 두가지가 있는데 spring seurity가 자체적으로 제공하는 방법과 HttpServletRequest객체에서 가져오는 방법이 있다.
	// 후자의 방법은 spring security의 reference 참고
	
	// spring security 에서 기본적으로 제공하는 방식
	// 이 코드는 jsp가 아닌 자바코드에서도 사용 가능
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	
	// getPrincipal()은 인증 실패의 경우 anonymousUser란 문자열이 있는 String객체 리턴, 인증 성공 시 작성한 MemberInfo객체 리턴, 둘 다 수용하기 위해 Object객체가 리턴됨
	Object principal = auth.getPrincipal();	
	
	String name = "";
	
	// getPrincipal()로 MemberInfo객체가 리턴 되었을 경우
	if(principal != null && principal instanceof MemberInfo) {
		name = ((MemberInfo)principal).getName();
	}

	// HttpServletRequest객체에서 Authentication을 가져왔을 때 getPrincipal()은 로그인이 되지 않았을 경우 문자열이 아닌 null을 리턴한다.
	// 그래서 HttpServletRequest를 사용하는 방법에서는 null 체크가 필수
	
%>
<html>
<head>
	<title>Home</title>
</head>
<body>

<!-- 로그인 폼 -->
<div style="width:300px;float:left;">

	<!--로그인 하지 않은 경우-->
	<sec:authorize access="isAnonymous()">
		<form id="loginForm" name="loginForm" method="post" action="/loginCheck">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
			
			<table>
				<tr>
					<td>ID</td>
					<td><input type="text" id="loginId" name="loginId" style="width:100px;"></td>
				</tr>
				<tr>
					<td>PASSWORD</td>
					<td><input type="password" id="loginPw" name="loginPw" style="width:100px;"></td>
				</tr>
				<tr>
					<td colspan="2"><input type="submit" id="loginBtn"></td>
				</tr>
			</table>
		</form>
	</sec:authorize>
	
	<!--로그인한 경우-->
	<sec:authorize access="isAuthenticated()">
		<%=name%>님 반갑습니다.
		<form method="post" action="/logout">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
			<input type="submit" value="로그아웃">
			csrf값과 함께 포스트방식으로 전달해야 되는구나
		</form>
		<a href="/logout" >로그아웃</a>
		단순한 get방식으로는 되지 않고
	</sec:authorize>
	
	<!--권한에 따른 접근-->
	<ul>
		<sec:authorize access="hasRole('ROLE_ADMIN')"><li>관리자 화면</li></sec:authorize>
		<sec:authorize access="permitAll"><li>비회원 게시판</li></sec:authorize>
		<sec:authorize access="isAuthenticated()"><li>준회원 게시판</li></sec:authorize>
		<sec:authorize access="hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')"><li>정회원 게시판</li></sec:authorize>
	</ul>

</div>



<!-- 메인화면 컨텐츠 -->
<h1>
	Hello world!  
</h1>

<P>  The time on the server is ${serverTime}. </P>

<a href="/admin">admin</a> <br>
<a href="/user">user</a>
</body>
</html>
