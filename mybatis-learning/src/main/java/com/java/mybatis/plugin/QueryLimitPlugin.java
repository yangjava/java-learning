package com.java.mybatis.plugin;

import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class,Integer.class }) })  
public class QueryLimitPlugin implements Interceptor{

	
	private int limit;
	private String dbType;
	private static final String LIMIT_TABLE_NAME="limit_table_name_xxx";
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler=(StatementHandler) invocation.getTarget();
		   MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler
			     );  
			     // 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过下面的两次循环  
			     // 可以分离出最原始的的目标类)  
			     while (metaStatementHandler.hasGetter("h")) {  
			         Object object = metaStatementHandler.getValue("h");  
			         metaStatementHandler = SystemMetaObject.forObject(object);  
			     }  
			     // 分离最后一个代理对象的目标类  
			     while (metaStatementHandler.hasGetter("target")) {  
			         Object object = metaStatementHandler.getValue("target");  
			         metaStatementHandler = SystemMetaObject.forObject(object);  
			     }  
			     String sql=(String) metaStatementHandler.getValue("delegate.boundSql.sql");
		         String limitSql;
		         if("MYSQL".equals(dbType)&&sql.indexOf(LIMIT_TABLE_NAME)==-1){
		        	 sql=sql.trim();
		        	 //分页SQL
		        	 limitSql= "select *  from ( "+sql+" ) "+LIMIT_TABLE_NAME +" limit " +limit;
		        	 metaStatementHandler.setValue("delegate.boundSql.sql", limitSql);
		         }
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		this.dbType=properties.getProperty("dbType");
		this.limit=Integer.valueOf(properties.getProperty("limit"));
		
	}

}
