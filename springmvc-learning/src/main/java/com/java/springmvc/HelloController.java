package com.java.springmvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class HelloController implements Controller{

	private Logger LOGGER=Logger.getLogger(this.getClass().getName());
	
	private String helloWorld;
	
	private String viewPage;
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		LOGGER.info("spring-mvc Hello World");
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("helloWorld", getHelloWorld());
		return new ModelAndView(getViewPage(),map);
	}

	public String getHelloWorld() {
		return helloWorld;
	}

	public void setHelloWorld(String helloWorld) {
		this.helloWorld = helloWorld;
	}

	public String getViewPage() {
		return viewPage;
	}

	public void setViewPage(String viewPage) {
		this.viewPage = viewPage;
	}

	
	
}
