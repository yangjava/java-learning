package com.java.spring.ioc.lifecycle;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 自定义 后处理Bean
 * 
 * 
 */
public class MyBeanPostProccessor implements BeanPostProcessor {

	@Override
	/**
	 * bean 就是对象实例 
	 * beanName 就是xml 配置Bean的id 或者 name
	 */
	public Object postProcessAfterInitialization(final Object bean,
			String beanName) throws BeansException {
		System.out.println("第八步 执行后处理Bean 的初始化完成后方法...");
		if (beanName.equals("userDAO")) {
			// 需要进行时间监控Bean
			Object proxy = Proxy.newProxyInstance(bean.getClass()
					.getClassLoader(), bean.getClass().getInterfaces(),
					new InvocationHandler() {

						@Override
						public Object invoke(Object proxy, Method method,
								Object[] args) throws Throwable {
							if (method.getName().equals("search")) {
								// 增强search方法
								System.out.println("开始时间："
										+ System.currentTimeMillis());
								Object result = method.invoke(bean, args);
								System.out.println("结束时间："
										+ System.currentTimeMillis());
								return result;
							} else {
								// 不加强
								return method.invoke(bean, args);
							}
						}
					});
			return proxy;
		}

		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		System.out.println("第五步 执行后处理Bean 的初始化前方法...");
		return bean;
	}

}
