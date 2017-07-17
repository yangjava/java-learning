package com.java.mybatis.DynamicProxy;

public class HelloServiceMain {
	
	
	public static void main(String[] args) {
    HelloServiceProxy helloServiceProxy=new HelloServiceProxy();
    HelloService proxy=(HelloService) helloServiceProxy.bind(new HelloServiceImpl());
    proxy.sayHello("yang");
    
    HelloServiceGcLib helloServiceGcLib=new HelloServiceGcLib();
    HelloServiceImpl helloServiceImpl = (HelloServiceImpl) helloServiceGcLib.getInstance(new HelloServiceImpl());
    helloServiceImpl.sayHello(" yang");
    
    
	}

}
