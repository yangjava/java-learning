package com.java.c3p0;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3p0DataSource2 {
   
	private static ComboPooledDataSource cpds;  
	
	static {
			cpds = new ComboPooledDataSource();
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
