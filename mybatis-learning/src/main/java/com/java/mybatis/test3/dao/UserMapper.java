package com.java.mybatis.test3.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.java.mybatis.test3.model.User;
import com.java.mybatis.test3.model.UserMap;
import com.java.mybatis.test3.model.UserQueryVo;

public interface UserMapper {
    //通过XML文件加载SQL语句
    User findUserById(Integer id);
    
    List<User> findUserByName(String userName);
    
    User findUserByIdAndName(Map<String,Object> map);
    
    User findUserByIdAndNameParam(@Param("id") int id,@Param("username") String name);
    
    //通过注解加载SQL语句
    @Select("SELECT * FROM  user  WHERE id=#{value}")
    User selectUserById(Integer id);

    public List<User> findUserByUserQueryVoId(UserQueryVo userQueryVo);
    
    public int findUserCount(UserQueryVo userQueryVo);
    
    UserMap findUserMap(Integer id);
    
    List<User> findAllUser();
}