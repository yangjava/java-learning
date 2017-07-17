mybatis学习笔记(10)-插件

1.插件接口

public interface Interceptor {
   //直接覆盖你所拦截对象原有的所有方法,核心方法
   //反射调度原来对象的方法
  Object intercept(Invocation invocation) throws Throwable;
  //给拦截对象生成一个代理对象,并返回它
  Object plugin(Object target);
  // 允许在plugin元素中配置所需参数
  void setProperties(Properties properties);

}

  这种模式我们成为 模板模式 ,就是提供一个骨架,并且告知骨架中的方法是干什么用的,由开发者完成它
  
2.插件的初始化

插件是在Mybaits初始化的时候完成的, 

public class XMLConfigBuilder extends BaseBuilder{


private void pluginElement(XNode parent) throws Exception {
    if (parent != null) {
      for (XNode child : parent.getChildren()) {
        String interceptor = child.getStringAttribute("interceptor");
        Properties properties = child.getChildrenAsProperties();
        Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).newInstance();
        interceptorInstance.setProperties(properties);
        configuration.addInterceptor(interceptorInstance);
      }
    }
  }
  
}

在上下文初始化时候,读取插入节点和我们配置的参数,利用发射技术生成对应的实例,调用setProperties方法,配置我们的参数,所以plugin一开始就初始化,而不是用
到的时候才开始初始化的
public class Configuration {

  protected final InterceptorChain interceptorChain = new InterceptorChain();
  
  public void addInterceptor(Interceptor interceptor) {
    interceptorChain.addInterceptor(interceptor);
  }
  
}  

public class InterceptorChain {

  private final List<Interceptor> interceptors = new ArrayList<Interceptor>();

  public Object pluginAll(Object target) {
    for (Interceptor interceptor : interceptors) {
      target = interceptor.plugin(target);
    }
    return target;
  }

  public void addInterceptor(Interceptor interceptor) {
    interceptors.add(interceptor);
  }
  
  public List<Interceptor> getInterceptors() {
    return Collections.unmodifiableList(interceptors);
  }

}

## 责任链模式

插件使用的是责任链模式,　责任链模式是一种对象的行为模式。
在责任链模式里，很多对象由每一个对象对其下家的引用而连接起来形成一条链。
请求在这个链上传递，直到链上的某一个对象决定处理此请求。
发出这个请求的客户端并不知道链上的哪一个对象最终处理这个请求，
这使得系统可以在不影响客户端的情况下动态地重新组织和分配责任。

public class Plugin implements InvocationHandler {

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      Set<Method> methods = signatureMap.get(method.getDeclaringClass());
      if (methods != null && methods.contains(method)) {
        return interceptor.intercept(new Invocation(target, method, args));
      }
      return method.invoke(target, args);
    } catch (Exception e) {
      throw ExceptionUtil.unwrapThrowable(e);
    }
  }
  
  }
  
## Mybaits 常用的工具类MetaObject

public class MetaObject {

  public static MetaObject forObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
    if (object == null) {
      return SystemMetaObject.NULL_META_OBJECT;
    } else {
      return new MetaObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
    }
  }
    //获取对象属性,支持OGNL
    public Object getValue(String name) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
      if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
        return null;
      } else {
        return metaValue.getValue(prop.getChildren());
      }
    } else {
      return objectWrapper.get(prop);
    }
  }
  //设置对象属性,支持OGNL
  public void setValue(String name, Object value) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
      if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
        if (value == null && prop.getChildren() != null) {
          // don't instantiate child path if value is null
          return;
        } else {
          metaValue = objectWrapper.instantiatePropertyValue(name, prop, objectFactory);
        }
      }
      metaValue.setValue(prop.getChildren(), value);
    } else {
      objectWrapper.set(prop, value);
    }
  }
  
## 插件拦截的对象

Executor 是执行SQL的全过程,都可以拦截,较为广泛,一般使用的不多
StatementHandler是执行SQL的过程,我们重写执行的SQL,我们使用的最多
ParameterHandler 拦截SQL的参数
ResultSetHandler 用于拦截执行结果的组装

## 拦截方法和参数

查询过程的通过Executor调度StatementHandler的prepare方法预编译SQL,我们需要拦截prepare

public interface StatementHandler {

  Statement prepare(Connection connection, Integer transactionTimeout)
      throws SQLException;

  void parameterize(Statement statement)
      throws SQLException;

  void batch(Statement statement)
      throws SQLException;

  int update(Statement statement)
      throws SQLException;

  <E> List<E> query(Statement statement, ResultHandler resultHandler)
      throws SQLException;

  <E> Cursor<E> queryCursor(Statement statement)
      throws SQLException;

  BoundSql getBoundSql();

  ParameterHandler getParameterHandler();

}


插件的基本写法

@Intercepts(value={@Signature(type=StatementHandler.class, args = {Connection.class}, method = "prepare")})
public class PageNumPlugin implements Interceptor{

}

@Intercepts拦截器的说明 @Signature拦截的位置  type是拦截的类型 method是拦截的方法 args是拦截的参数


##  参考例子

@Intercepts({@Signature(type=Executor.class, args = {MappedStatement.class,Object.class}, method = "update")})
public class PageNumPlugin implements Interceptor{
	
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

结果:

获取参数MYSQL
plugin  调用代理对象
before
在拦截器中获取参数MYSQL
plugin  调用代理对象
plugin  调用代理对象
plugin  调用代理对象
DEBUG [com.java.mybatis.test4.dao.UserMapper.insertUser] - ==>  Preparing: insert into user (id, username, password, birthday, sex, address) values (?, ?, ?, ?, ?, ?) 
DEBUG [com.java.mybatis.test4.dao.UserMapper.insertUser] - ==> Parameters: null, admin2(String), admin(String), 2017-07-17 14:22:16.571(Timestamp), 1(String), 南京(String)
DEBUG [com.java.mybatis.test4.dao.UserMapper.insertUser] - <==    Updates: 1
after
1
34

## 返回数据条数限制插件

 插件代码
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



       	<plugins>
		<plugin interceptor="com.java.mybatis.plugin.QueryLimitPlugin">
		<property name="dbType" value="MYSQL"/>
		<property name="limit" value="2"/>
		</plugin>
		</plugins>
