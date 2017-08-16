package com.java.spring.aop.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
//环绕通知
public class WorkAroundAdvice implements MethodInterceptor{

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		System.out.println("开始工作");
		Object proceed = invocation.proceed();
		System.out.println("结束工作");
		return proceed;
	}

}
