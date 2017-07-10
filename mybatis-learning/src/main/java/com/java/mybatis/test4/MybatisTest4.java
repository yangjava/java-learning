package com.java.mybatis.test4;

import java.io.IOException;
import java.util.Date;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import com.java.mybatis.test1.SqlSessionFactoryUtil;
import com.java.mybatis.test4.dao.UserMapper;
import com.java.mybatis.test4.model.User;

public class MybatisTest4 {
	
	
	
    @Test
    public void insertUserTest() throws IOException{
        // mybatis配置文件
        String resource = "SqlMapConfig4.xml";
        SqlSession sqlSession = SqlSessionFactoryUtil.openSqlSession(resource);
        //通过Mapper 加载SQL信息
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        User user=new User();
        user.setUsername("admin2");
        user.setPassword("admin");
        user.setSex("1");
        user.setBirthday(new Date());
        user.setAddress("南京");
        int insertNum = userMapper.insertUser(user);
        System.out.println(insertNum);
        System.out.println(user.getId());
        //提交事务,如果没有这句话会出现返回结果为insertUser=1,但是数据库中没有数据
//        数据库很好理解，你没有commit的数据只有在你当前的transaction中可以查到，其他客户端是查不到的，如果查到了岂不是脏读了吗？
//        mybatis的缓存很简单，默认insert,delete,update都会清空缓存，只有在select的时候才会在缓存里放东西。
//        所以答案很明显了，就是你auto commit关闭的问题，没有把更新操作commit到数据库，所以只能在你自己这里查到，你开个客户端却查不到
        sqlSession.commit();
        // 释放资源
        sqlSession.close();

    }
    
 
    
    @Test
    public void deleteUserTest() throws IOException{
        // mybatis配置文件
        String resource = "SqlMapConfig4.xml";
        SqlSession sqlSession = SqlSessionFactoryUtil.openSqlSession(resource);
        //通过Mapper 加载SQL信息
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        int deleteNum = userMapper.deleteUser(29);
        System.out.println(deleteNum);
        sqlSession.commit();
        // 释放资源
        sqlSession.close();

    }
    
    
    @Test
    public void updateUserTest() throws IOException{
        // mybatis配置文件
        String resource = "SqlMapConfig4.xml";
        SqlSession sqlSession = SqlSessionFactoryUtil.openSqlSession(resource);
        //通过Mapper 加载SQL信息
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        User user=new User();
        user.setUsername("yang");
        user.setPassword("admin");
        user.setId(30);
        user.setSex("1");
        user.setBirthday(new Date());
        user.setAddress("天津");
        int insertNum = userMapper.updateUser(user);
        //如果id不填,更新0条 
        System.out.println(insertNum);
        System.out.println(user.getId());
        sqlSession.commit();
        sqlSession.close();

    }
    
    
    
    
    
    
    
    
    
    
    
}
