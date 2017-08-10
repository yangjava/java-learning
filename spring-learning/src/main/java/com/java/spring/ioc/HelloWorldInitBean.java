package com.java.spring.ioc;

import java.util.Date;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class HelloWorldInitBean implements InitializingBean,HelloWorldService,DisposableBean{
	
	private String msg;
	
	private Date date;
	
	@Override
	public void sayHello() {
      System.out.println("Hello World HelloWorldInitBean "+msg +date);		
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.date=new Date();
		this.msg="init";
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

	@Override
	public void destroy() throws Exception {
		System.out.println("你销毁了HelloWorldInitBean");
		
	}
}
