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