package com.java.spring.ioc.lifecycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

// 实现DAO 方法
public class UserDAOImpl implements UserDAO, BeanNameAware,
		ApplicationContextAware, InitializingBean, DisposableBean {

	public UserDAOImpl() {
		System.out.println("第一步 Bean的实例化 ...");
	}

	@Override
	public void add() {
		System.out.println("第九步  业务操作 .... 添加");
	}

	@Override
	public void search() {
		System.out.println("第九步  业务操作 .... 查询");
	}

	// 设置company
	public void setCompany(String company) {
		System.out.println("第二步 设置Bean的属性");
	}

	@Override
	public void setBeanName(String beanName) {
		System.out.println("第三步 将xml配置Bean的name设置到程序中：" + beanName);
		// <bean id="userDAO"
		// class="cn.itcast.spring.d_lifecycle.UserDAOImpl"></bean>
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		System.out.println("第四步 将整合工厂上下文对象 设置到 Bean中 ");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("第六步 属性设置完成后...");
	}

	public void setup() {
		System.out.println("第七步 配置初始化方法...");
	}

	@Override
	public void destroy() throws Exception {
		System.out.println("第十步  无需配置的销毁方法");
	}

	public void teardown() {
		System.out.println("第十一步 通过配置 设置销毁方法...");
	}

}
