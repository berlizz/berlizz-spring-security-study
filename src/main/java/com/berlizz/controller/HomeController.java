package com.berlizz.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Inject
	private BCryptPasswordEncoder passwordEncoder;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public void admin() throws Exception {
		logger.info("admin()");
	}
	
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public void user() throws Exception {
		logger.info("user()");
	}
	
	@RequestMapping(value ="/login", method = RequestMethod.GET)
	public void login() throws Exception {
		logger.info("login()");
	}
	
	
	
	@RequestMapping(value = "/passwordEncoder", method = RequestMethod.GET)
	public void passwordEncoder(@RequestParam(value = "targetStr", required = false, defaultValue = "") String targetStr, Model model) {
		logger.info("passwordEncoder");
		
		if(StringUtils.hasText(targetStr)) {
			String bCryptStr = passwordEncoder.encode(targetStr);
			model.addAttribute("targetStr", targetStr);
			model.addAttribute("bCryptStr", bCryptStr);
		}
	}
	
	
	
}
