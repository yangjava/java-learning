package com.java.spring.ioc;

import org.junit.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;


@SuppressWarnings("deprecation")
public class BeanFactoryTest {

   @Test
   public  void testHelloWorld(){
	   HelloWorldService hello=new HelloWorldServiceImpl();
	   hello.sayHello();
   }
	
   @Test
	public void FileSystemXmlApplicationContext() throws Exception{
	   //可以使用classpath:
//	   ApplicationContext applicationContext = new FileSystemXmlApplicationContext(
//				"src/main/resources/bean1.xml");
	  
	ApplicationContext applicationContext = new FileSystemXmlApplicationContext(
				"classpath:bean1.xml");
	   HelloWorldService hello = (HelloWorldService) applicationContext
				.getBean("helloWorldService");
	   hello.sayHello();
	}
   
   @Test
	public void testClassPathXmlApplicationContext() throws Exception{
		// 工厂 + 反射 + 配置文件 ，实例化 IHelloService的对象
//		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
//				"bean1.xml");
	   
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"classpath:bean1.xml");
		
//		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
//				"file:.....xml");
		// 通过工厂 根据 配置名称 获得实例对象
		HelloWorldService hello = (HelloWorldService) applicationContext
				.getBean("helloWorldService");
		 hello.sayHello();

		// 控制反转，对象的创建权 被反转到 Spring 框架
	}
	
    @Test
	public  void testXmlBeanFactory(){
		BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource(
				"bean1.xml"));
		HelloWorldService hello = (HelloWorldService) beanFactory
				.getBean("helloWorldService");
		 hello.sayHello();
	}
	
    @Test
 	public void test1() throws Exception{
 		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
 				"classpath:bean1.xml");
 		HelloWorldService hello = (HelloWorldService) applicationContext
 				.getBean("helloWorldService");
 		 hello.sayHello();
 	}
    @Test
 	public void test11() throws Exception{
 		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
 				"classpath:bean1.xml");
 		HelloWorldService hello = (HelloWorldService) applicationContext
 				.getBean("helloWorldService-constructor");
 		 hello.sayHello();
 	}
    @Test
 	public void test2() throws Exception{
 		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
 				"classpath:bean1.xml");
 		HelloWorldService hello = (HelloWorldService) applicationContext
 				.getBean("helloWorldService-static");
 		 hello.sayHello();
 	}
    
    @Test
 	public void test3() throws Exception{
 		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
 				"classpath:bean1.xml");
 		HelloWorldService hello = (HelloWorldService) applicationContext
 				.getBean("helloWorldService-bean");
 		 hello.sayHello();
 	}
    
    @Test
 	public void testnull() throws Exception{
 		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
 				"classpath:bean1.xml");
 		HelloWorldService hello = (HelloWorldService) applicationContext
 				.getBean("helloWorldService-null");
 		 hello.sayHello();
 	}
    
    @Test
 	public void testInit() throws Exception{
 		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
 				"classpath:bean1.xml");
 		HelloWorldService hello = (HelloWorldService) applicationContext
 				.getBean("helloWorldInit");
 		 hello.sayHello();
 	}
    @Test
 	public void testInitBean() throws Exception{
 		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
 				"classpath:bean1.xml");
 		HelloWorldService hello = (HelloWorldService) applicationContext
 				.getBean("HelloWorldInitBean");
 		 hello.sayHello();
 	}
    //helloWorldAutowire
    
    @Test
 	public void testAutowire() throws Exception{
 		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
 				"classpath:bean1.xml");
 		
 		HelloWorldAutowire helloWorldAutowirebyType = (HelloWorldAutowire) applicationContext
 				.getBean("helloWorldAutowirebyType");
 		System.out.println(helloWorldAutowirebyType.getUser().getName());
 		
		HelloWorldAutowire helloWorldAutowirebyName = (HelloWorldAutowire) applicationContext
 				.getBean("helloWorldAutowirebyName");
 		System.out.println(helloWorldAutowirebyName.getUser().getName());
 		
		HelloWorldAutowire helloWorldAutowireconstructor = (HelloWorldAutowire) applicationContext
 				.getBean("helloWorldAutowireconstructor");
 		System.out.println(helloWorldAutowireconstructor.getUser().getName());
 		
		HelloWorldAutowire helloWorldAutowireno = (HelloWorldAutowire) applicationContext
 				.getBean("helloWorldAutowireno");
 		System.out.println(helloWorldAutowireno.getUser().getName());
 	}
    
    @Test
 	public void testBean() throws Exception{
    	//通过Beanwrap管理
    	Object obj=Class.forName("com.java.spring.ioc.HelloWorld").newInstance();
    	BeanWrapper bean=new BeanWrapperImpl(obj);
    	bean.setPropertyValue("msg","HelloWorld bean");;
    	System.out.println(bean.getPropertyValue("msg"));
    	
    	//通过BeanFactory管理
    	BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource(
				"bean1.xml"));
		HelloWorld helloWorld1 = (HelloWorld) beanFactory
 				.getBean("helloWorld");
		System.out.println(helloWorld1.getMsg());
    	
		//通过ApplicationContext管理
 		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
 				"classpath:bean1.xml");
 		HelloWorld helloWorld2 = (HelloWorld) applicationContext
 				.getBean("helloWorld");
 		System.out.println(helloWorld2.getMsg());
    }
    
    
    
    
}
