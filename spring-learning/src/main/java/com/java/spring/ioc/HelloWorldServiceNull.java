package com.java.spring.ioc;

public class HelloWorldServiceNull implements HelloWorldService{
	
    private String msg;
    
	@Override
	public void sayHello() {
		if(msg==null){
			System.out.println("Hello World 测试null");
		}else{
			System.out.println("Hello World"+msg);
		}
		
		
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
   
}
