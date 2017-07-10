mybatis学习笔记(3)-mybatis入门程序

## pom文件
        
         <properties>
  			<mybatis.version>3.4.0</mybatis.version>
  		</properties>
        
		<dependency>
			<groupId>org.mybatis</groupId>
			<artifactId>mybatis</artifactId>
			<version>${mybatis.version}</version>
		</dependency>

## sqlMapConfig.xml 文件
	


## userMapper.xml 
  如果SQL的列名和POJO的属性名保持一致,Mybaits会将返回结果自动映射到POJo上,这就是自动映射


## Mybatistest.java

## 原始的dao
原始的Dao需要编写Dao接口和实现类
简化的可以 编写Mapper接口,Mybaits会通过动态代理,自动实现映射

## 结果

DEBUG [com.java.mybatis.test2.dao.UserMapper.findUserById] - ==>  Preparing: SELECT * FROM user WHERE id=? 
DEBUG [com.java.mybatis.test2.dao.UserMapper.findUserById] - ==> Parameters: 1(Integer)
DEBUG [com.java.mybatis.test2.dao.UserMapper.findUserById] - <==      Total: 1
User [id=1, username=admin, password=admin, birthday=Fri Jan 13 00:00:00 CST 1989, sex=1, address=北京市]


DEBUG [com.java.mybatis.test2.dao.UserMapper.findUserByName] - ==>  Preparing: SELECT * FROM user WHERE username LIKE '%admin%' 
[main] DEBUG [com.java.mybatis.test2.dao.UserMapper.findUserByName] - ==> Parameters: 
[main] DEBUG [com.java.mybatis.test2.dao.UserMapper.findUserByName] - <==      Total: 1
[User [id=1, username=admin, password=admin, birthday=Fri Jan 13 00:00:00 CST 1989, sex=1, address=北京市]]

## 问题总结

1. 在Mybaits中保留着 ibatis的"命名空间+SQL ID"的方式发送SQL语句;
如  User user = sqlSession.selectOne("com.java.mybatis.test2.dao.UserMapper.findUserById", 1);
如果只存在一个SQL ID 可以简写成  User user = sqlSession.selectOne("findUserById", 1);

2.我们需要Mapper接口吗?
需要;
1) 使用Mapper接口可以屏蔽SqlSession的细节
2) 使用SqlSession.SelectOne 功能性代码,而Mapper接口能正确反应出业务对象,符合面向对象编程规范
3) 使用Mapper接口能容易检查出错误

3.Mapper 接口没有实现类 如果运行
使用Java的动态代理,我们会在 Mybatis上下文描述这个对象,Mybatis会生成代理对象,通过"全限定性路径+方法名"
去匹配,找到对应的XML中的SQL语句去执行,返回结果

