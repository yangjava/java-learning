package com.java.spring.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

public class WorkTimeDynamicProxy implements InvocationHandler{

	 Logger LOGGER=Logger.getLogger(this.getClass().getName());
	 
	private Object target;
	
	public Object bind(Object target){
		this.target=target;
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),this);
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result=null;
  	   Long nowTime=System.currentTimeMillis();
  	    result=method.invoke(target, args);	
  	   String name=(String) args[0];
  	   LOGGER.info(name+"开始工作");  
  	   try {
			Thread.sleep(3000);
		   } catch (InterruptedException e) {
		  }
  	   Long lastTime=System.currentTimeMillis();
  	   LOGGER.info(name+"结束工作"); 
  	   LOGGER.info(name+"工作时长"+(lastTime-nowTime)/1000+"秒"); 
		return result;
	}

}
