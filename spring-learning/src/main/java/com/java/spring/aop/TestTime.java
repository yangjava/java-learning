package com.java.spring.aop;

import org.junit.Test;

public class TestTime {
	
    @Test
	public  void testworkTime(){
		WorkTime book=new WorkTime();
		book.work("张三");
	}
	
    @Test
	public  void testworkTimeproxy(){
		WorkTImeProxy proxy=new WorkTImeProxy(new WorkTimeImpl());
		proxy.work("张三");
	}
    
    @Test
    public  void testWorkTimedynamicProxy(){
    	WorkTimeDynamicProxy workTimeDynamicProxy=new WorkTimeDynamicProxy();
    	IWorkTime   work=(IWorkTime) workTimeDynamicProxy.bind(new WorkTimeImpl());
    	 work.work("张三");
    }
    
    
    
    
    
}
