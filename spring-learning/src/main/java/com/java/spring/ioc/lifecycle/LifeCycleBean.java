package com.java.spring.ioc.lifecycle;


public class LifeCycleBean {
	public LifeCycleBean() {
		System.out.println("LifeCycleBean 被构造了...");
	}

	public void setup() {
		System.out.println("初始化...");
	}

	public void teardown() {
		System.out.println("销毁....");
	}

}
