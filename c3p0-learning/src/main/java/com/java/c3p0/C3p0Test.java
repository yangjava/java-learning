package com.java.c3p0;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3p0Test {

	@Test
	public void testDataSource1() throws Exception{
		ComboPooledDataSource dataSource = C3p0DataSource1.getDataSource();
		System.out.println(dataSource);
		Connection connection = dataSource.getConnection();
		String sql="SELECT * FROM user WHERE id = ? ";
		PreparedStatement ps =connection.prepareStatement(sql); 
		ps.setString(1, "1");
		ResultSet rs= ps.executeQuery();  
		 while(rs.next()){  
             int id = rs.getInt(1);  
             String username = rs.getString(2);  
             System.out.println("id: " + id + " , username:" + username);  
         }  
		 C3p0DataSource1.realeaseResource(rs, ps, connection);
	}
	
	@Test
	public void testDataSource2() throws SQLException{
		ComboPooledDataSource dataSource = (ComboPooledDataSource) C3p0DataSource2.getDataSource();
		System.out.println(dataSource);
		Connection connection = dataSource.getConnection();
		String sql="SELECT * FROM user WHERE id = ? ";
		PreparedStatement ps =connection.prepareStatement(sql); 
		ps.setString(1, "1");
		ResultSet rs= ps.executeQuery();  
		 while(rs.next()){  
             int id = rs.getInt(1);  
             String username = rs.getString(2);  
             System.out.println("id: " + id + " , username:" + username);  
         } 
		 C3p0DataSource2.realeaseResource(rs, ps, connection);
	}
	
	@Test
	public void testDataSource3() throws SQLException{
		ComboPooledDataSource dataSource = (ComboPooledDataSource) C3p0DataSource3.getDataSource();
		System.out.println(dataSource);
		Connection connection = dataSource.getConnection();
		String sql="SELECT * FROM user WHERE id = ? ";
		PreparedStatement ps =connection.prepareStatement(sql); 
		ps.setString(1, "1");
		ResultSet rs= ps.executeQuery();  
		 while(rs.next()){  
             int id = rs.getInt(1);  
             String username = rs.getString(2);  
             System.out.println("id: " + id + " , username:" + username);  
         } 
		 C3p0DataSource3.realeaseResource(rs, ps, connection);
	}
}
