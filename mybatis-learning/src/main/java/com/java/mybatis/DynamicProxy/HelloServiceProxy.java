package com.java.mybatis.DynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

//动态代理
public class HelloServiceProxy implements InvocationHandler{
	
	private Object target;
	
	public Object bind(Object object){
		this.target=object;
		//类加载器  接口 
		//this --HelloServiceProxy
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		System.out.println("我是JDK的动态代理");
	    Object result=null;
	    System.out.println("动态代理start");
		result=method.invoke(target, args);
		return result;
	}

}
