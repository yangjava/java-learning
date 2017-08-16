package com.java.spring.ioc;

public class HelloWorldServiceConstructor implements HelloWorldService{
	
    private String msg;
    
    
	public HelloWorldServiceConstructor(String msg) {
		super();
		this.msg = msg;
	}
	@Override
	public void sayHello() {
		System.out.println("Hello World  "+msg);
		
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
   
}
