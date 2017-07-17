mybatis学习笔记(9)-设计原理

## 动态代理

当我们使用Mapper接口时,需要实现类,但是却没有实现类.这是Mybaits使用了动态代理
MapperProxy类

## 动态代理两种方式

1.JDK反射机制提供的代理,需要提供接口
2.CGLIB代理不需要提供接口

## JDK动态代理
JDK动态代理,由java.lang.reflect.*包提供支持的
   1.编写服务类和接口,
    2.编写代理类,提供绑定和代理方法

JDK的代理最大缺点 是需要提供接口,而Mybaits中的Mapper就是个接口,所以使用的就是JDK的动态代理


## 构建SqlSessionFactory的过程

SqlSessionFactory是Mybaits的核心类之一,重要功能技术就是提供创建Mybaits核心接口SqlSession,我们需要创建SqlSessionFactory
,提供配置文件和相关的参数,采用构造模式去创建SqlSessionFactory,我们通过SqlSessionFacrotyBuilder去构建

第一步;通过org.apache.ibatis.builder.xml.XMLConfigBuilder 解析XML文件,读取配置文件,并将配置信息存入
org.apache.ibatis.session.Configuration

第二步;通过Configuration对象去创建Sql
mybatis中的SqlSessionFactory是一个接口,而不是一个实现类,为此Mybaits提供了一个默认的SqlSessionFactory
实现类,我们一般都会使用 org.apache.ibatis.session.defaults.DefaultSqlSessionFactory

  -- 使用Builder模式;对于复杂的对象而言,直接使用构造方法创建有困难;
  
  
  在设计模式中对Builder模式的定义是用于构建复杂对象的一种模式，所构建的对象往往需要多步初始化或赋值才能完成。那么，在实际的开发过程中，我们哪些地方适合用到Builder模式呢？其中使用Builder模式来替代多参数构造函数是一个比较好的实践法则。

我们常常会面临编写一个这样的实现类(假设类名叫DoDoContact)，这个类拥有多个构造函数，

DoDoContact(String name);

DoDoContact(String name, int age);

DoDoContact(String name, int age, String address);

DoDoContact(String name, int age, String address, int cardID);

这样一系列的构造函数主要目的就是为了提供更多的客户调用选择，以处理不同的构造请求。这种方法很常见，也很有效力，但是它的缺点也很多。类的作者不得不书写多种参数组合的构造函数，而且其中还需要设置默认参数值，这是一个需要细心而又枯燥的工作。其次，这样的构造函数灵活性也不高，而且在调用时你不得不提供一些没有意义的参数值，例如，DoDoContact("Ace", -1, "SH")，显然年龄为负数没有意义，但是你又不的不这样做，得以符合Java的规范。如果这样的代码发布后，后面的维护者就会很头痛，因为他根本不知道这个-1是什么含义。对于这样的情况，就非常适合使用Builder模式。Builder模式的要点就是通过一个代理来完成对象的构建过程。这个代理职责就是完成构建的各个步骤，同时它也是易扩展的。下面是改写自Effective Java里面的一段代码：

public class DoDoContact {
    private final int    age;
    private final int    safeID;
    private final String name;
    private final String address;
 
    public int getAge() {
        return age;
    }
 
    public int getSafeID() {
        return safeID;
    }
 
    public String getName() {
        return name;
    }
 
    public String getAddress() {
        return address;
    }
 
    public static class Builder {
        private int    age     = 0;
        private int    safeID  = 0;
        private String name    = null;
        private String address = null;
　　　// 构建的步骤
        public Builder(String name) {
            this.name = name;
        }
 
        public Builder age(int val) {
            age = val;
            return this;
        }
 
        public Builder safeID(int val) {
            safeID = val;
            return this;
        }
 
        public Builder address(String val) {
            address = val;
            return this;
        }
 
        public DoDoContact build() { // 构建，返回一个新对象
            return new DoDoContact(this);
        }
    }
 
    private DoDoContact(Builder b) {
        age = b.age;
        safeID = b.safeID;
        name = b.name;
        address = b.address;
 
    }
}
最终，客户程序可以很灵活的去构建这个对象。
DoDoContact ddc = new DoDoContact.Builder("Ace").age(10)
                .address("beijing").build();
System.out.println("name=" + ddc.getName() + "age =" + ddc.getAge()
                + "address" + ddc.getAddress());
将想法付诸于实践，借此来影响他人是一个人存在的真正价值

## 构建Configuration

在SqlSessionFactory构建中,Configuration是最重要的,它的作用如下;

1.读入配置文件,包含基础配置的XML文件和映射器的XML文件
2.初始化基础配置,比如Mybaits的别名等,一些重要的类对象,比如 插件,映射器.ObjectFactory和TypeHandler
3.提供单例,为后续创建SessionFactory服务并提供配置的参数
4.执行一些重要的对象方法,初始化配置信息

Configuration是通过XMLConfigBuilder去构建的;
首先Mybaits读出XML配置的信息,然后将信息保存到Configuration类的单例中;它会做如下的初始化
	properties全局变量
	settings设置
	typeAliases别名
	typeHandler类型处理器
    ObjectFactory对象
	plugin插件
	environment环境
	DatabaseIdProvider数据库标识
	Mapper映射器

映射器的内部组成,一般分为3个部分:

MappedStatement ,它保存映射器的一个节点(select|insert|delete|update);
包含我们配置的SQL,SQL的ID,缓存信息,resultMap,parameterType,resultType,languageDriver等重要的配置内容

SqlSource,它提供BoundSql对象的房,它是MappedStatement的一个属性

BoundSql,它建立SQL和参数的地方,它由3个常用的属性 SQL parameterObject parameterMappings 

parameterObject 为参数对象,可以是 简单对象 POJO Map @Param注解的参数
	1.传递简单对象(int,String,float,double)Mybaits会把参数参数编程Interger对象传递,其他类似
	2.如果我们传递的POJO或者Map,那么parameterObject就是你传入的POJO或者Map不变
	3.我们可传递多个参数,如果没有@Param注解,Mybaits会把parameterObject转为一个Map<String,Object>对象,
	类似于没有@Param注解,只是把数字的键值对应置换为@Param注解的键值,比如我们注解
	(@Param("key1") String p1,@Param("key2") String p2,@Param("key3") String p3)
	那么他们键值包含{"key1":p1,"key2":p2,"key3",p3,"param1":p1,"param2":p2,"param3":p3}
	
	
	在MapperMethod.Java会首先经过下面方法来转换参数：

   public Object convertArgsToSqlCommandParam(Object[] args) {
    final int paramCount = params.size();
     if (args == null || paramCount == 0) {
      return null;
      } else if (!hasNamedParameters && paramCount == 1) {
       return args[params.keySet().iterator().next()];
     } else {
    final Map<String, Object> param = new ParamMap<Object>();
    int i = 0;
    for (Map.Entry<Integer, String> entry : params.entrySet()) {
      param.put(entry.getValue(), args[entry.getKey()]);
      // issue #71, add param names as param1, param2...but ensure backward compatibility
      final String genericParamName = "param" + String.valueOf(i + 1);
      if (!param.containsKey(genericParamName)) {
        param.put(genericParamName, args[entry.getKey()]);
      }
      i++;
    }
    return param;
  }
}
在这里有个很关键的params，这个参数类型为Map<Integer, String>，他会根据接口方法按顺序记录下接口参数的定义的名字，如果使用@Param指定了名字，就会记录这个名字，如果没有记录，那么就会使用它的序号作为名字。

例如有如下接口：

List<User> select(@Param('sex')String sex,Integer age);
那么他对应的params如下:

{
    0:'sex',
    1:'1'
}
继续看上面的convertArgsToSqlCommandParam方法，这里简要说明3种情况：
入参为null或没有时，参数转换为null
没有使用@Param注解并且只有一个参数时，返回这一个参数
使用了@Param注解或有多个参数时，将参数转换为Map1类型，并且还根据参数顺序存储了key为param1,param2的参数。
注意：从第3种情况来看，建议各位有多个入参的时候通过@Param指定参数名，方便后面（动态sql）的使用。

经过上面方法的处理后，在MapperMethod中会继续往下调用命名空间方式的方法：

Object param = method.convertArgsToSqlCommandParam(args);
result = sqlSession.<E>selectList(command.getName(), param);
从这之后开始按照统一的方式继续处理入参。
	4.parameterMapping,它是一个List,每一个元素都是parameterMapping的对象,这个对象会描述我们的参数,
参数包含 属性 名称 表达式 javaType jdbcType typeHander等重要信息,通过它实现参数和SQL的结合
	5.SQL属性就是我们书写映射器里面的一条SQL,
	
## 构造SqlSessionFactory对象

 SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
 
 Mybaits会根据Configuration对象创建SqlSessionFactory
 
## SqlSession运行过程

在SqlSession接口调用的insert/update/delete方法中，所有的操作都交给了Executor来操作。
SqlSession接口是Mybatis框架暴露的外部接口，而Executor是内部的实现接口。
在Executor的实现中，又是调用StatementHandler来处理的。


## 映射器的动态代理

Mapper 映射是通过动态代理实现的.

public class MapperProxyFactory<T> {
   ...
  @SuppressWarnings("unchecked")
  protected T newInstance(MapperProxy<T> mapperProxy) {
    return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
  }

  public T newInstance(SqlSession sqlSession) {
    final MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, mapperInterface, methodCache);
    return newInstance(mapperProxy);
  }

}
这里我们看到动态代理对接口的绑定,作用生成动态代理对象(站位)
代理的方法放到了MapperProxy类中

public class MapperProxy<T> implements InvocationHandler, Serializable {
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (Object.class.equals(method.getDeclaringClass())) {
      try {
        return method.invoke(this, args);
      } catch (Throwable t) {
        throw ExceptionUtil.unwrapThrowable(t);
      }
    }
	//Mapper是接口
    final MapperMethod mapperMethod = cachedMapperMethod(method);
    return mapperMethod.execute(sqlSession, args);
  }
  
}


上面运用了invoke方法,一旦mapper是一个代理对象,那么就会运行invoke方法,
首先判断是否是一个类,这里的Mapper是一个接口,不是类,
通过cachedMapperMethod对其初始化,然后执行execute方法,把sqlSession和当前的参数传递进去
execute源码如下

public class MapperMethod {

  private final SqlCommand command;
  private final MethodSignature method;
  
  
    public Object execute(SqlSession sqlSession, Object[] args) {
    Object result;
    if (SqlCommandType.INSERT == command.getType()) {
      Object param = method.convertArgsToSqlCommandParam(args);
      result = rowCountResult(sqlSession.insert(command.getName(), param));
    } else if (SqlCommandType.UPDATE == command.getType()) {
      Object param = method.convertArgsToSqlCommandParam(args);
      result = rowCountResult(sqlSession.update(command.getName(), param));
    } else if (SqlCommandType.DELETE == command.getType()) {
      Object param = method.convertArgsToSqlCommandParam(args);
      result = rowCountResult(sqlSession.delete(command.getName(), param));
    } else if (SqlCommandType.SELECT == command.getType()) {
      if (method.returnsVoid() && method.hasResultHandler()) {
        executeWithResultHandler(sqlSession, args);
        result = null;
      } else if (method.returnsMany()) {
	  //重点类
        result = executeForMany(sqlSession, args);
      } else if (method.returnsMap()) {
        result = executeForMap(sqlSession, args);
      } else if (method.returnsCursor()) {
        result = executeForCursor(sqlSession, args);
      } else {
        Object param = method.convertArgsToSqlCommandParam(args);
        result = sqlSession.selectOne(command.getName(), param);
      }
    } else if (SqlCommandType.FLUSH == command.getType()) {
        result = sqlSession.flushStatements();
    } else {
      throw new BindingException("Unknown execution method for: " + command.getName());
    }
    if (result == null && method.getReturnType().isPrimitive() && !method.returnsVoid()) {
      throw new BindingException("Mapper method '" + command.getName() 
          + " attempted to return null from a method with a primitive return type (" + method.getReturnType() + ").");
    }
    return result;
  }
  
    .......
    private <E> Object executeForMany(SqlSession sqlSession, Object[] args) {
    List<E> result;
    Object param = method.convertArgsToSqlCommandParam(args);
    if (method.hasRowBounds()) {
      RowBounds rowBounds = method.extractRowBounds(args);
      result = sqlSession.<E>selectList(command.getName(), param, rowBounds);
    } else {
      result = sqlSession.<E>selectList(command.getName(), param);
    }
    // issue #510 Collections & arrays support
    if (!method.getReturnType().isAssignableFrom(result.getClass())) {
      if (method.getReturnType().isArray()) {
        return convertToArray(result);
      } else {
        return convertToDeclaredCollection(sqlSession.getConfiguration(), result);
      }
    }
    return result;
  }
 }
 MapperMethod采用命令模式运行,根据上下文跳转到许多方法中,通过sqlSession去运行SQL
 
 
 ## SqlSession下的四大对象
 
 Mapper执行的过程是通过Executor StatementHandler ParameterHandler ResultHandler来完成数据库操作和结果返回的
 
 1.Executor是执行器 ,由它来调度 StatementHandler ParameterHandler ResultHandler执行对应的SQL
 2.StatementHandler 使用数据库的Statement(PreparedStatement)执行操作
 3.ParameterHandler 用于SQL对参数的处理
 4.ResultHandler 进行最后数据集(ResultSet)的封装返回处理的

 ## 执行器
 执行器(Executor)是真正执行Java和数据库交互的东西,在Mybaits中存在三种执行器.我们可以在Mybaits配置文件中进行选择
,在setting元素的属性defaultExecutorType

  SIMPLE,简易执行器,不配置它就是默认执行器
  REUSE,是一种执行器重用预处理语句
  BATCH,是专门用于批量更新
 public class Configuration {
 .......
  public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
    executorType = executorType == null ? defaultExecutorType : executorType;
    executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
    Executor executor;
    if (ExecutorType.BATCH == executorType) {
      executor = new BatchExecutor(this, transaction);
    } else if (ExecutorType.REUSE == executorType) {
      executor = new ReuseExecutor(this, transaction);
    } else {
      executor = new SimpleExecutor(this, transaction);
    }
    if (cacheEnabled) {
      executor = new CachingExecutor(executor);
    }
    executor = (Executor) interceptorChain.pluginAll(executor);
    return executor;
  }
 }
 执行这段代码:
 executor = (Executor) interceptorChain.pluginAll(executor);
 在调度Executor方法之前配置插件的代码可以修改
 public class SimpleExecutor extends BaseExecutor {
 
 
  @Override
  public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
    Statement stmt = null;
    try {
	//根据Configuration构建StatementHandler
      Configuration configuration = ms.getConfiguration();
      StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
      //然后创建prepareStatement
	  stmt = prepareStatement(handler, ms.getStatementLog());
      return handler.<E>query(stmt, resultHandler);
    } finally {
      closeStatement(stmt);
    }
  }
  
    private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
    Statement stmt;
    Connection connection = getConnection(statementLog);
	//预编译和基础设置
    stmt = handler.prepare(connection, transaction.getTimeout());
    handler.parameterize(stmt);
    return stmt;
  }

}
 
## 数据库会话器
public class Configuration {


  public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
    StatementHandler statementHandler = new RoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
    statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
    return statementHandler;
  } 
  
 }
 
 StatementHandler通过类名我们可以了解到它可能是Statement的处理器，它是一个接口，其实现类如下：
 
BaseStatementHandler：一个抽象类，只是实现了一些不涉及具体操作的方法
RoutingStatementHandler：类似路由器，根据配置文件来路由选择具体实现类SimpleStatementHandler、CallableStatementHandler和PreparedStatementHandler


分为三种  SimpleStatementHandler  PrepareStatementHandler  CallableStatementHandler
SimpleStatementHandler：就是直接使用普通的Statement对象，这样每次执行SQL语句都需要数据库对SQL进行预编译
PrepareStatementHandler：使用PrepareStatement执行，虽然初次创建PrepareStatement时开销比较大，但在多次处理SQL时只需要初始化一次，可以有效提高性能
CallableStatementHandler：使用CallableStatement执行，CallableStatement是用来执行存储过程的。
选择不同StatementHandler的配置如下：通过根据不同的操作配置不同的StatementHandler来进行处理


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


  public RoutingStatementHandler(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {

    switch (ms.getStatementType()) {
      case STATEMENT:
        delegate = new SimpleStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
        break;
      case PREPARED:
        delegate = new PreparedStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
        break;
      case CALLABLE:
        delegate = new CallableStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
        break;
      default:
        throw new ExecutorException("Unknown statement type: " + ms.getStatementType());
    }

  }
  
各种StatmentHandler的分析
SimpleStatementHandler-简单的jdbc执行操作封装
PreparedStatementHandler-preparestatement操作封装
CallableStatementHandler-CallableStatement操作封装

## 参数处理器 ParameterHandler

public interface ParameterHandler {

  Object getParameterObject();

  void setParameters(PreparedStatement ps)
      throws SQLException;

}

默认实现类

public class DefaultParameterHandler implements ParameterHandler {


 @Override
  public void setParameters(PreparedStatement ps) {
    ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    if (parameterMappings != null) {
      for (int i = 0; i < parameterMappings.size(); i++) {
        ParameterMapping parameterMapping = parameterMappings.get(i);
        if (parameterMapping.getMode() != ParameterMode.OUT) {
          Object value;
          String propertyName = parameterMapping.getProperty();
          if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
            value = boundSql.getAdditionalParameter(propertyName);
          } else if (parameterObject == null) {
            value = null;
          } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            value = parameterObject;
          } else {
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            value = metaObject.getValue(propertyName);
          }
          TypeHandler typeHandler = parameterMapping.getTypeHandler();
          JdbcType jdbcType = parameterMapping.getJdbcType();
          if (value == null && jdbcType == null) {
            jdbcType = configuration.getJdbcTypeForNull();
          }
          try {
            typeHandler.setParameter(ps, i + 1, value, jdbcType);
          } catch (TypeException e) {
            throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
          } catch (SQLException e) {
            throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
          }
        }
      }
    }
  }

}

这里重点是setParameters()，首先它读取了ParameterObject参数对象，
然后用typeHandler对参数进行设置，而typeHandler里面需要对jdbcType和javaType进行处理，
然后就设置参数了。
也很好理解。所以当我们使用TypeHandler的时候完全可以控制如何设置SQL参数。

## ResultSetHandler

public interface ResultHandler<T> {  
  
  void handleResult(ResultContext<? extends T> resultContext);  
  
} 

Mybatis中只提供了一个ResultSetHandler的实现，那就是DefaultResultSetHandler。
下面来看看他的handleResultSets()方法

public List<Object> handleResultSets(Statement stmt) throws SQLException {  
    final List<Object> multipleResults = new ArrayList<Object>();  
  
    int resultSetCount = 0;  
    //获取第一个ResultSet,通常只会有一个  
    ResultSetWrapper rsw = getFirstResultSet(stmt);  
    //从配置中读取对应的ResultMap，通常也只会有一个  
    List<ResultMap> resultMaps = mappedStatement.getResultMaps();  
    int resultMapCount = resultMaps.size();  
    validateResultMapsCount(rsw, resultMapCount);  
    while (rsw != null && resultMapCount > resultSetCount) {  
      ResultMap resultMap = resultMaps.get(resultSetCount);  
      //完成映射，将结果加到入multipleResults中  
      handleResultSet(rsw, resultMap, multipleResults, null);  
      rsw = getNextResultSet(stmt);  
      cleanUpAfterHandlingResultSet();  
      resultSetCount++;  
    }  
  
    String[] resultSets = mappedStatement.getResulSets();  
    if (resultSets != null) {  
      while (rsw != null && resultSetCount < resultSets.length) {  
        ResultMapping parentMapping = nextResultMaps.get(resultSets[resultSetCount]);  
        if (parentMapping != null) {  
          String nestedResultMapId = parentMapping.getNestedResultMapId();  
          ResultMap resultMap = configuration.getResultMap(nestedResultMapId);  
          handleResultSet(rsw, resultMap, null, parentMapping);  
        }  
        rsw = getNextResultSet(stmt);  
        cleanUpAfterHandlingResultSet();  
        resultSetCount++;  
      }  
    }  
    //如果只有一个映射，返回第一个  
    return collapseSingleResultList(multipleResults);  
  }  
  
  