#mybatis学习笔记(1)-传统的jdbc编程


##jdbc简介


Java程序都是通过JDBC(Java Data Base Connectivity)连接数据库,这样我们就可以
通过SQL对数据库进行编程;
JDBC是有SUN公司提出一系列的编程规范;但是它只定义了接口规范,具体实现则通过这个数据库
提供商来实现;
JDBC就是典型的桥接模式;


## JDBC编程的步骤


1.注册驱动
2.获取数据库连接Connection,创建Statement对象
3.通过Statemnt执行SQL,返回结果到ResultSet对象
4.获取结果,进行解析处理
5.释放数据库相关资源(resultSet、preparedstatement(或者Statemnt)、connection)


## 问题总结

1.数据库连接，使用时就创建，不使用立即释放，对数据库进行频繁连接开启和关闭，造成数据库资源浪费，影响数据库性能。
 
设想：使用数据库连接池管理数据库连接。

2.将sql语句硬编码到java代码中，如果sql语句修改，需要重新编译java代码，不利于系统维护。

设想：将sql语句配置在xml配置文件中，即使sql变化，不需要对java代码进行重新编译。

3.向preparedStatement中设置参数，对占位符号位置和设置参数值，硬编码在java代码中，不利于系统维护。

设想：将sql语句及占位符号和参数全部配置在xml中。

4.从resutSet中遍历结果集数据时，存在硬编码，将获取表的字段进行硬编码，不利于系统维护。
 
设想：将查询的结果集，自动映射成java对象。

##jdbcTest代码

pom文件

    	<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>5.1.38</version>
		</dependency>
		
代码见  com.java.jdbc.JdbcTest