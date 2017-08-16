package com.java.dbutils;

import java.sql.Connection;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DataSource {

	
	private static ComboPooledDataSource cpds;  
	
	static {
			cpds = new ComboPooledDataSource("mySource");
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
    /*
     * 工具类里面现在没有必要提供release()方法，因为我们是使用dbutils操作数据库，
     * 即调用dbutils的update()和query()方法操作数据库，他操作完数据库之后，会自动释放掉连接。
     */
    
    
}
