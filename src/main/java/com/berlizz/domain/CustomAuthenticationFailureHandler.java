package com.berlizz.domain;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
	
	private String loginIdName; 		// 로그인 id 값이 들어오는 input 태그 name(로그인 아이디가 저장되어 있는 파라미터 이름값이 들어옴)
	private String loginPwdName;
	private String loginRedirectName;	// 로그인 성공 시 redirect 할 url이 지정되어 있는 input 태그 name
	private String exceptionMsgName;	// 예외 메시지를 request의 Attribute에 지정할 때 사용 될 key 값
	private String defaultFailureUrl;	// 화면에 보여줄 url(로그인 화면)
	
	public CustomAuthenticationFailureHandler() {
		loginIdName = "loginId";
		loginPwdName = "loginPw";
		loginRedirectName = "loginRedirect";
		exceptionMsgName = "securityExceptionMsg";
		defaultFailureUrl = "/login?fail=true";
	}

	/* 
	 *	구현해야하는 메소드는 하나이며, 로그인 실패 시 이 메소드가 실행된다.
	 *	로그인을 실패했을 경우 이므로 3번쨰 인자가 onAuthenticationSueecss에서 인증 정보 객체인 Authentication과 달리 
	 *	AuthenticationException 클래스 객체가 오게 된다.  
	 *	AuthencationException에는 로그인이 어떤 이유로 실패했는지에 대한 정보가 들어있다.
	*/
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		String loginId = request.getParameter(loginIdName);
		String loginPw = request.getParameter(loginPwdName);
		String loginRedirect = request.getParameter(loginRedirectName);
		
		// 사용자가 입력했던 아이디와 비밀번호 권한이 필요했던 url 값을 가지고 로그인 페이지로 이동한다.
		request.setAttribute(loginIdName, loginId);
		request.setAttribute(loginPwdName, loginPw);
		request.setAttribute(loginRedirectName, loginRedirect);
		
		// 예외 메시지 저장
		request.setAttribute(exceptionMsgName, exception.getMessage());
		request.getRequestDispatcher(defaultFailureUrl).forward(request, response);

	}

	public String getLoginIdName() {
		return loginIdName;
	}

	public void setLoginIdName(String loginIdName) {
		this.loginIdName = loginIdName;
	}

	public String getLoginPwdName() {
		return loginPwdName;
	}

	public void setLoginPwdName(String loginPwdName) {
		this.loginPwdName = loginPwdName;
	}

	public String getLoginRedirectName() {
		return loginRedirectName;
	}

	public void setLoginRedirectName(String loginRedirectName) {
		this.loginRedirectName = loginRedirectName;
	}

	public String getExceptionMsgName() {
		return exceptionMsgName;
	}

	public void setExceptionMsgName(String exceptionMsgName) {
		this.exceptionMsgName = exceptionMsgName;
	}

	public String getDefaultFailureUrl() {
		return defaultFailureUrl;
	}

	public void setDefaultFailureUrl(String defaultFailureUrl) {
		this.defaultFailureUrl = defaultFailureUrl;
	}
	
	

}
