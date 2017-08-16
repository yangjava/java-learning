package com.java.spring.ioc.lifecycle;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LifeCycleBeanTest {
	@Test
	// 测试Spring 生命周期
	public void demo() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		LifeCycleBean lifeCycleBean = (LifeCycleBean) applicationContext
				.getBean("lifeCycleBean");
		System.out.println(lifeCycleBean);

		// 关闭工厂
		applicationContext.close();
	}

	@Test
	// 测试Spring 生命周期
	public void demo2() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		UserDAO userDAO = (UserDAO) applicationContext.getBean("userDAO");

		userDAO.add();
		userDAO.search();

		// 关闭工厂
		applicationContext.close();
	}
}
