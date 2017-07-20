package com.java.jdbc;

import java.util.List;

import org.junit.Test;

import com.java.dbutils.User;

public class TestMemory {
    
	Memory memory=MemoryFactory.getInstance();
	@Test
	public void testMemory(){
		String  sql="select * from  user where id =? ";
		List<User> query = memory.query(sql, new BeanListHandler<User>(User.class), "1");
		System.out.println(query);
	}
}
