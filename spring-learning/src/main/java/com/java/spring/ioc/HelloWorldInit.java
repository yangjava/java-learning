package com.java.spring.ioc;

import java.util.Date;

public class HelloWorldInit implements HelloWorldService{
	
	private String msg;
	
	private Date date;
	
	public void init(){
		System.out.println("初始化");
		this.date=new Date();
		this.msg="init";
	}

	@Override
	public void sayHello() {
      System.out.println("Hello World "+msg +date);		
	}
	
	public void cleanUp(){
		System.out.println("你销毁HelloWorldInit");
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	
	
}
