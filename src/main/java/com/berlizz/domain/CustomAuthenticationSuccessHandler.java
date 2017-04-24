package com.berlizz.domain;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	private RequestCache requestCache = new HttpSessionRequestCache();
	private String targetUrlParameter;	
	private String defaultUrl;
	private boolean useReferer;
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	public CustomAuthenticationSuccessHandler() {
		targetUrlParameter = "";
		defaultUrl = "";
		useReferer = false;
	}

	/* 
	 *	구현해야할 메소드는 이거 하나밖에 없고, 로그인 성공 시 이 메소드를 실행하게 된다
	 *	Authentication객체를 받으므로 사용자 정보를 가져올 수 있다.
	 */
	@Override 
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		
		clearAuthenticationAttributes(request);
		
		int intRedirectStrategy = decideRedirectStrategy(request, response);
		switch(intRedirectStrategy) {
			case 1 :
				useTargetUrl(request, response);
				break;
			case 2 :
				useSessionUrl(request, response);
				break;
			case 3 :
				useRefererUrl(request, response);
				break;
			default :
				useDefaultUrl(request, response);
				
		}

	}
	
	
	/*
	 * 인증성공 후 어떤 url로 redirect할지 결정한다
	 * return 	1 : targetUrlParameter 값을 읽은 url	(1순위)
	 * 			2 : Session에 저장되어 있는 url			(2순위)
	 * 			3 : referer 헤더에 있는 url				(3순위)
	 * 			4 : default url						(4순위)
	 */
	private int decideRedirectStrategy(HttpServletRequest request, HttpServletResponse response) {
		int result = 0;
		
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		
		if(!"".equals(targetUrlParameter)) {
			String targetUrl = request.getParameter(targetUrlParameter);
			if(StringUtils.hasText(targetUrl)) {
				result = 1;
			} else {
				if(savedRequest != null) {
					result = 2;
				} else {
					String refererUrl = request.getHeader("REFERER");
					if(useReferer && StringUtils.hasText(refererUrl)) {
						result = 3;
					} else {
						result = 4;
					}
				}
			}
			
			return result;
		}
		
		if(savedRequest != null) {
			result = 2;
			return result;
		}
		
		String refererUrl = request.getHeader("REFERER");
		if(useReferer && StringUtils.hasText(refererUrl)) {
			result = 3;
		} else {
			result = 0;
		}
		
		return result;
	}
	
	
	// 로그인을 하는 과정에서 실패한 경우에도 세션에 관련 에러를 저장하게 되는데, 로그인에 성공하였을 시 앞에 실패했던 에러를 세션에서 삭제한다. 	
	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session == null) {
			return;
		}
		
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}
	
	private void useTargetUrl(HttpServletRequest request, HttpServletResponse response) {
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		if(savedRequest != null) {
			requestCache.removeRequest(request, response);
		}
		
		String targetUrl = request.getParameter(targetUrlParameter);
		
		try {
			redirectStrategy.sendRedirect(request, response, targetUrl);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void useSessionUrl(HttpServletRequest request, HttpServletResponse response) {
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		String targetUrl = savedRequest.getRedirectUrl();
		
		try {
			redirectStrategy.sendRedirect(request, response, targetUrl);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void useRefererUrl(HttpServletRequest request, HttpServletResponse response) {
		String targetUrl = request.getHeader("REFERER");
		
		try {
			redirectStrategy.sendRedirect(request, response, targetUrl);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void useDefaultUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			redirectStrategy.sendRedirect(request, response, defaultUrl);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getTargetUrlParameter() {
		return targetUrlParameter;
	}

	public void setTargetUrlParameter(String targetUrlParameter) {
		this.targetUrlParameter = targetUrlParameter;
	}

	public String getDefaultUrl() {
		return defaultUrl;
	}

	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}

	public boolean isUseReferer() {
		return useReferer;
	}

	public void setUseReferer(boolean useReferer) {
		this.useReferer = useReferer;
	}
	
	
	
	

}
