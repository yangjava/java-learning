#mybatis学习笔记(1)-mybatis概述


##mybatis简介


Mybatis是Apache的ibatis发展过来,目前迁移到github上
ibatis来源于 internet和abatis,是基于Java的持久层框架
ibatis提供的持久层框架包含SQL Maps和DAO(data access Objects)
mybatis让程序将主要精力放在sql上，通过mybatis提供的映射方式，自由灵活生成（半自动化，大部分需要程序员编写sql）满足需要sql语句。
mybatis可以将向 preparedStatement中的输入参数自动进行输入映射，将查询结果集灵活映射成java对象。（输出映射）


## mybatis框架执行过程

1、配置mybatis的配置文件，SqlMapConfig.xml（名称不固定）

2、通过配置文件，加载mybatis运行环境，创建SqlSessionFactory会话工厂(SqlSessionFactory在实际使用时按单例方式)

3、通过SqlSessionFactory创建SqlSession。SqlSession是一个面向用户接口（提供操作数据库方法），实现对象是线程不安全的，建议sqlSession应用场合在方法体内。

4、调用sqlSession的方法去操作数据。如果需要提交事务，需要执行SqlSession的commit()方法。

5、释放资源，关闭SqlSession

## mybaits的核心组件

1.  SqlSessionFactoryBuilder(构造器) 
	它会根据配置信息或者代码来生成SqlSessionFactory(工厂接口),通过XML或者Java编码获取资源来构建SQLSessionFactory
通过它可以构建多个SqlSessionFactory;它的生命周期只存在方法中,一旦创建SqlsessionFactory对象,就失去生存的意义
2.SqlSessionFactory :
	依靠工厂生成SqlSession(会话),sqlsesion是一个会话,相当于JDBC的Connection对象;每次有应用程序访问数据库,我们就要通过
	SqlSessionFactory创建SqlSession.所以SqlSessionFactory存在Mybaits的整个生命周期;为了减少资源消耗,所以是单例模式,它的责任是
	创建SqlSession
3.SqlSession :
	是一个既可以发送SQL去执行并返回结果,也可以获取Mapper接口
	生命周期是在请求数据库处理事务的过程中;
	是一个线程不安全对象,设计多线程时候需要注意隔离级别,数据库锁等高级特性.每次创建SqlSession对象都必须及时关闭它
4.SQL Mapper:
	它是Mybaits的组件,它由Java接口和XML文件(或者注解)构成,需要给出对应的SQL和映射规则,它负责发送SQL去执行,
并返回结果





