package com.java.mybatis.DynamicProxy;
//接口的实现类
public class HelloServiceImpl implements HelloService{

	@Override
	public void sayHello(String name) {
	System.out.println("Hello world "+name);
	}

}
