package com.java.mybatis.test4.dao;

import com.java.mybatis.test4.model.User;


public interface UserMapper {


	int insertUser(User user);
	
	int  deleteUser(Integer id);

	int updateUser(User user);


}