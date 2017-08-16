package com.java.spring.ioc;

public class HelloWorldServiceFacotryBean {
	
	public  HelloWorldService getHelloWorldService(){
		return new HelloWorldServiceImpl();
	}

}
