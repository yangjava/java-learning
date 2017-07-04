package com.java.mybatis.test2;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import com.java.mybatis.test2.model.User;

public class MybatisTestTwo {
	
	
	
    @Test
    public void findUserByIdTest() throws IOException{
        // mybatis配置文件
        String resource = "SqlMapConfig2.xml";
        // 得到配置文件流
        InputStream inputStream =  Resources.getResourceAsStream(resource);
        //创建会话工厂，传入mybatis配置文件的信息
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 通过工厂得到SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 通过SqlSession操作数据库
        // 第一个参数：映射文件中statement的id，等于=namespace+"."+statement的id
        //  com.java.mybatis.one.dao.UserMapper   selectByPrimaryKey
        // 第二个参数：指定和映射文件中所匹配的parameterType类型的参数
        // sqlSession.selectOne结果 是与映射文件中所匹配的resultType类型的对象
        // selectOne查询出一条记录
        User user = sqlSession.selectOne("com.java.mybatis.test2.dao.UserMapper.findUserById", 1);

        System.out.println(user);

        // 释放资源
        sqlSession.close();

    }
    
    
    
    // 根据用户名称模糊查询用户列表
    @Test
    public void findUserByNameTest() throws IOException {
        // mybatis配置文件
        String resource = "SqlMapConfig2.xml";
        // 得到配置文件流
        InputStream inputStream = Resources.getResourceAsStream(resource);

        // 创建会话工厂，传入mybatis的配置文件信息
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
                .build(inputStream);

        // 通过工厂得到SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // list中的user和映射文件中resultType所指定的类型一致
        List<User> list = sqlSession.selectList("com.java.mybatis.test2.dao.UserMapper.findUserByName", "admin");
        System.out.println(list);
        sqlSession.close();

    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
