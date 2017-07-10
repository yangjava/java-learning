package com.java.mybatis.test3;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import com.java.mybatis.test3.dao.UserMapper;
import com.java.mybatis.test3.model.User;
import com.java.mybatis.test3.model.UserMap;
import com.java.mybatis.test3.model.UserQueryVo;

public class MybatisTest3 {
	
	
	
    @Test
    public void findUserByIdTest() throws IOException{
        // mybatis配置文件
        String resource = "SqlMapConfig3.xml";
        // 得到配置文件流
        InputStream inputStream =  Resources.getResourceAsStream(resource);
        //创建会话工厂，传入mybatis配置文件的信息
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 通过工厂得到SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //通过Mapper 加载SQL信息
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        User user=userMapper.findUserById(1);
        System.out.println(user);
        // 释放资源
        sqlSession.close();

    }
    
    @Test
    public void selectUserByIdTest() throws IOException{
        // mybatis配置文件
        String resource = "SqlMapConfig3.xml";
        // 得到配置文件流
        InputStream inputStream =  Resources.getResourceAsStream(resource);
        //创建会话工厂，传入mybatis配置文件的信息
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 通过工厂得到SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //通过Mapper 加载SQL信息
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        User user=userMapper.selectUserById(1);
        System.out.println(user);
        // 释放资源
        sqlSession.close();

    }
    
    // 根据用户名称模糊查询用户列表
//    @Test
//    public void findUserByNameTest() throws IOException {
//        // mybatis配置文件
//        String resource = "SqlMapConfig2.xml";
//        // 得到配置文件流
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//
//        // 创建会话工厂，传入mybatis的配置文件信息
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
//                .build(inputStream);
//
//        // 通过工厂得到SqlSession
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//        // list中的user和映射文件中resultType所指定的类型一致
//        List<User> list = sqlSession.selectList("com.java.mybatis.test2.dao.UserMapper.findUserByName", "admin");
//        System.out.println(list);
//        sqlSession.close();
//
//    }
    
    
    
    
    @Test
    public void findUserByUserQueryVoIdTest() throws IOException{
        // mybatis配置文件
        String resource = "SqlMapConfig3.xml";
        // 得到配置文件流
        InputStream inputStream =  Resources.getResourceAsStream(resource);
        //创建会话工厂，传入mybatis配置文件的信息
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 通过工厂得到SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //通过Mapper 加载SQL信息
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        UserQueryVo userQueryVo=new UserQueryVo();
        User userQuery=new User();
        userQuery.setId(1);
        userQuery.setUsername("admin");
        userQueryVo.setUser(userQuery);
        List<User> user=userMapper.findUserByUserQueryVoId(userQueryVo);
        System.out.println(user);
        // 释放资源
        sqlSession.close();

    }
    
    @Test
    public void findUserCountTest() throws IOException{
        // mybatis配置文件
        String resource = "SqlMapConfig3.xml";
        // 得到配置文件流
        InputStream inputStream =  Resources.getResourceAsStream(resource);
        //创建会话工厂，传入mybatis配置文件的信息
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 通过工厂得到SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //通过Mapper 加载SQL信息
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        UserQueryVo userQueryVo=new UserQueryVo();
        User userQuery=new User();
        userQuery.setId(1);
        userQuery.setUsername("admin");
        userQueryVo.setUser(userQuery);
        int userCount=userMapper.findUserCount(userQueryVo);
        System.out.println(userCount);
        // 释放资源
        sqlSession.close();

    }
    
    @Test
    public void findUserMapTest() throws IOException{
        // mybatis配置文件
        String resource = "SqlMapConfig3.xml";
        // 得到配置文件流
        InputStream inputStream =  Resources.getResourceAsStream(resource);
        //创建会话工厂，传入mybatis配置文件的信息
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 通过工厂得到SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //通过Mapper 加载SQL信息
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        UserMap user=userMapper.findUserMap(1);
        System.out.println(user);
        // 释放资源
        sqlSession.close();

    }
    
    
    @Test
    public void findUserByIdAndNameTest() throws IOException{
        // mybatis配置文件
        String resource = "SqlMapConfig3.xml";
        // 得到配置文件流
        InputStream inputStream =  Resources.getResourceAsStream(resource);
        //创建会话工厂，传入mybatis配置文件的信息
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 通过工厂得到SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //通过Mapper 加载SQL信息
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("id", 1);
        map.put("username", "admin");
        User user=userMapper.findUserByIdAndName(map);
        System.out.println(user);
        // 释放资源
        sqlSession.close();

    }
    
    @Test
    public void findUserByIdAndNameParamTest() throws IOException{
        // mybatis配置文件
        String resource = "SqlMapConfig3.xml";
        // 得到配置文件流
        InputStream inputStream =  Resources.getResourceAsStream(resource);
        //创建会话工厂，传入mybatis配置文件的信息
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 通过工厂得到SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //通过Mapper 加载SQL信息
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        User user=userMapper.findUserByIdAndNameParam(1, "admin");
        System.out.println(user);
        // 释放资源
        sqlSession.close();

    }
    
}
