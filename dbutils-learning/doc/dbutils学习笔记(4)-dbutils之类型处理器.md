package com.java.dbutils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.BeanMapHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.KeyedHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;

public class DbutilsTest {
	
	//BeanHandler:将查询结果的第一行数据，封装到user对象
	@Test
	public void testBeanHandler() throws SQLException {
		QueryRunner queryRunner = new QueryRunner(DataSource.getDataSource());
		String sql = "select * from user where id = ?";
		User user = (User) queryRunner.query(sql, new BeanHandler<User>(
				User.class), "1");
		System.out.println(user);
	}
	
    //将查询结果的每一行封装到user对象，然后，再存入list集合
	
	@Test
	public void testBeanListHandler() throws SQLException {
		QueryRunner runner = new QueryRunner(DataSource.getDataSource());
		String sql = "select * from user";
		List<User> list = (List<User>) runner.query(sql,
				new BeanListHandler<User>(User.class));
		System.out.println(list.size() + ":" + list);
	}
	
	//把结果集中的第一行数据转成对象数组
	@Test
    public void testArrayHandler() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
        String sql = "select * from user where id = ?";
        Object[] result = runner.query(sql, new ArrayHandler(),"1");
        System.out.println(result[0]);
        System.out.println(result[1]);
    }
	
	//把结果集中的每一行数据都转成一个数组，再存放到List中
	@Test
    public void testArrayListHandler() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
        String sql = "select * from user ";
        List<Object[]> result = runner.query(sql, new ArrayListHandler());
        for (int i = 0; i < result.size(); i++) {
        	 System.out.println(result.get(i)[0]);
        	 System.out.println(result.get(i)[1]);
        	 System.out.println(result.get(i)[2]);
		}
    }
	
	//将结果集中某一列的数据存放到List中
	@Test
    public void testColumnListHandler() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
        String sql = "select * from user ";
        List<String> list = (List<String>)runner.query(sql, new ColumnListHandler<String>("username"));
        for (int i = 0; i < list.size(); i++) {
        	 System.out.println(list.get(i));
		}
    }
	
	//将查询结果的第一行数据封装到map结合（key==列名，value==列值）
	@Test
    public void testMapHandler() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
        String sql = "select * from user where id = ?";
        Map<String, Object> result = runner.query(sql, new MapHandler(),"1");
        System.out.println(result.get("id"));
        System.out.println(result.get("username"));
    }
	//将查询结果的每一行封装到map集合（key==列名，value==列值），再将map集合存入List集合
	@Test
    public void testMapListHandler() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
        String sql = "select * from user ";
        List<Map<String, Object>> result = runner.query(sql, new MapListHandler());
        for (int i = 0; i < result.size(); i++) {
        	System.out.println(result.get(i).get("id"));
            System.out.println(result.get(i).get("username"));
		}
    }
	 // 将查询结果的每一行数据，封装到User对象，再存入map集合中（key==列名，value==列值）
	@Test
    public void testBeanMapHandler() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
          String sql = "select * from user ";
          Map<Integer,User> map= (Map<Integer,User>)runner.query(sql, new BeanMapHandler<Integer, User>(User.class));
          User user=map.get(1);
          System.out.println(user);
    }
	
	//KeyedHandler
	//将结果集中的每一行数据都封装到一个Map里，再把这些map再存到一个map里，其key为指定的key。 
	// 将查询的结果的每一行数据，封装到map1（key==列名，value==列值 ），然后将map1集合（有多个）存入map2集合（只有一个）
	@Test
    public void testKeyedHandler() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
          String sql = "select * from user ";
          Map<Integer, Map<String, Object>> map = (Map<Integer, Map<String, Object>>) runner.query(sql, new KeyedHandler<Integer>("id"));
          for (Map.Entry<Integer, Map<String, Object>> me : map.entrySet()) {
              int id = me.getKey();
              System.out.println("id="+id);
              for (Map.Entry<String, Object> entry : me.getValue().entrySet()) {
                  String name = entry.getKey();
                  Object value = entry.getValue();
                  System.out.println(name+"="+value);
              }
          }
	}
	
	//  ScalarHandler:封装类似count、avg、max、min、sum。。。。函数的执行结果
	@Test
    public void testScalarHandler() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
          String sql = "select count(*) from user ";
          int totalrecord = ((Long)runner.query(sql, new ScalarHandler<Long>(1))).intValue();
          System.out.println(totalrecord);
    }
	//ArrayHandler查询条数 和 ScalarHandler相同效果
	@Test
    public void testScalarHandler2() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
          String sql = "select count(*) from user ";
          Object[] result = runner.query(sql, new ArrayHandler());
          int totalrecord =  ((Long) result[0]).intValue();
          System.out.println(totalrecord);
          
/*          Object[] result = runner.query(sql, new ArrayHandler());
          long totalrecord = (Long) result[0]; 
          // 查询结果——总记录数是作为java.lang.Long类型返回的
          int num = (int) totalrecord;
          System.out.println(num);*/
    }
	
	
}
