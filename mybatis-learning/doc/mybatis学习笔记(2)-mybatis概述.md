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
