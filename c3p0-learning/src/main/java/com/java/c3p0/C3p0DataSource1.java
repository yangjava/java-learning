package com.java.c3p0;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3p0DataSource1 {
	
	private static ComboPooledDataSource cpds;  
	
	static {
		try {
			Properties props = new Properties();
			InputStream in = Thread.class
					.getResourceAsStream("/config.properties");
			props.load(in);
			in.close();
			cpds = new ComboPooledDataSource();
			cpds.setDriverClass(props.getProperty("driverClass"));
			cpds.setJdbcUrl(props.getProperty("jdbcUrl"));
			cpds.setUser(props.getProperty("user"));
			cpds.setPassword(props.getProperty("password"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public static ComboPooledDataSource getDataSource(){  
        return cpds;  
    }  
    public static Connection getConnection(){  
        Connection conn = null;  
        try {  
            conn = cpds.getConnection();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return conn;  
    }  
      
    //释放资源  
    public static void realeaseResource(ResultSet rs,PreparedStatement ps,Connection conn){  
        if(null != rs){  
            try {  
                rs.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
          
        if(null != ps){  
            try {  
                ps.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
  
        try {  
            conn.close();  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  
}
