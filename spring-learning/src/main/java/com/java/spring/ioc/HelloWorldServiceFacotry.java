package com.java.spring.ioc;

public class HelloWorldServiceFacotry {
	
	public static HelloWorldService getHelloWorldService(){
		return new HelloWorldServiceImpl();
	}

}
