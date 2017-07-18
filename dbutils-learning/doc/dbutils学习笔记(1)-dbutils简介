 ##  dbutils简介

Commons DbUtils是Apache组织提供的一个对JDBC进行简单封装的开源工具类库，
使用它能够简化JDBC应用程序的开发，同时也不会影响程序的性能。

## DBUtils 特点

DBUtils是java编程中的数据库操作实用工具，小巧简单实用，
1.对于数据表的读操作，他可以把结果转换成List，Array，Set等java集合，便于程序员操作；
2.对于数据表的写操作，也变得很简单（只需写sql语句）
3.可以使用数据源，使用JNDI，数据库连接池等技术来优化性能--重用已经构建好的数据库连接对象，
而不像php，asp那样，费时费力的不断重复的构建和析构这样的对象。

## DBUtils 包

DBUtils包括3个包：
org.apache.commons.dbutils
org.apache.commons.dbutils.handlers
org.apache.commons.dbutils.wrappers

## API

org.apache.commons.dbutils

DbUtils 关闭链接等操作
QueryRunner 进行查询的操作

org.apache.commons.dbutils.handlers

ResultSetHandler实现类介绍（由DbUtils框架提供）
     备注：DbUtils给我们提供了10个ResultSetHandler实现类，分别是：
     ①ArrayHandler：     将查询结果的第一行数据，保存到Object数组中
      ②ArrayListHandler     将查询的结果，每一行先封装到Object数组中，然后将数据存入List集合
      ③BeanHandler     将查询结果的第一行数据，封装到user对象
     ④BeanListHandler     将查询结果的每一行封装到user对象，然后再存入List集合
     ⑤ColumnListHandler     将查询结果的指定列的数据封装到List集合中
     ⑥MapHandler     将查询结果的第一行数据封装到map结合（key==列名，value==列值）
     ⑦MapListHandler     将查询结果的每一行封装到map集合（key==列名，value==列值），再将map集合存入List集合
     ⑧BeanMapHandler     将查询结果的每一行数据，封装到User对象，再存入mao集合中（key==列名，value==列值）
     ⑨KeyedHandler     将查询的结果的每一行数据，封装到map1（key==列名，value==列值 ），然后将map1集合（有多个）存入map2集合（只有一个）
     ⑩ScalarHandler     封装类似count、avg、max、min、sum......函数的执行结果

org.apache.commons.dbutils.wrappers

SqlNullCheckedResultSet ：对ResultSet进行操作，改版里面的值
StringTrimmedResultSet ：去除ResultSet中中字段的左右空格。Trim()

## 主要方法：
DbUtils类：启动类
ResultSetHandler接口：转换类型接口
MapListHandler类：实现类，把记录转化成List
BeanListHandler类：实现类，把记录转化成List，使记录为JavaBean类型的对象
Query Runner类：执行SQL语句的类

