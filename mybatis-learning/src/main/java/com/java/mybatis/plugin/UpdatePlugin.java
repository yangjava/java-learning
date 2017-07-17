package com.java.mybatis.plugin;

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

@Intercepts({@Signature(type=Executor.class, args = {MappedStatement.class,Object.class}, method = "update")})
public class UpdatePlugin implements Interceptor{
	
    Properties props=null;
    
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		System.out.println("before");
		System.out.println("在拦截器中获取参数"+props.getProperty("dbType"));
		Object obj = invocation.proceed();
		System.out.println("after");
		return obj;
	}

	@Override
	public Object plugin(Object target) {
		System.out.println("plugin  调用代理对象");
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		System.out.println("获取参数"+properties.getProperty("dbType"));
		this.props=properties;
	}

}
