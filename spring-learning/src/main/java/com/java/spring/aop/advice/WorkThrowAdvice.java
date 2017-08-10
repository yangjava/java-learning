package com.java.spring.aop.advice;

import java.lang.reflect.Method;

import org.springframework.aop.ThrowsAdvice;
//异常通知  注意：查看ThrowsAdvice源码会发现这个接口里面没有定义方法，但是这个方法必须这么写，
public class WorkThrowAdvice implements ThrowsAdvice {

	public void afterThrowing(Method m, Object args, Object target, Throwable e) {
		System.out.println("异常通知:方法" + m.getName() + "发生异常," + e.getMessage());

		System.exit(0);

	}

}
