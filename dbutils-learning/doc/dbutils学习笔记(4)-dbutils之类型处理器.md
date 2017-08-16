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
	
	//BeanHandler:����ѯ����ĵ�һ�����ݣ���װ��user����
	@Test
	public void testBeanHandler() throws SQLException {
		QueryRunner queryRunner = new QueryRunner(DataSource.getDataSource());
		String sql = "select * from user where id = ?";
		User user = (User) queryRunner.query(sql, new BeanHandler<User>(
				User.class), "1");
		System.out.println(user);
	}
	
    //����ѯ�����ÿһ�з�װ��user����Ȼ���ٴ���list����
	
	@Test
	public void testBeanListHandler() throws SQLException {
		QueryRunner runner = new QueryRunner(DataSource.getDataSource());
		String sql = "select * from user";
		List<User> list = (List<User>) runner.query(sql,
				new BeanListHandler<User>(User.class));
		System.out.println(list.size() + ":" + list);
	}
	
	//�ѽ�����еĵ�һ������ת�ɶ�������
	@Test
    public void testArrayHandler() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
        String sql = "select * from user where id = ?";
        Object[] result = runner.query(sql, new ArrayHandler(),"1");
        System.out.println(result[0]);
        System.out.println(result[1]);
    }
	
	//�ѽ�����е�ÿһ�����ݶ�ת��һ�����飬�ٴ�ŵ�List��
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
	
	//���������ĳһ�е����ݴ�ŵ�List��
	@Test
    public void testColumnListHandler() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
        String sql = "select * from user ";
        List<String> list = (List<String>)runner.query(sql, new ColumnListHandler<String>("username"));
        for (int i = 0; i < list.size(); i++) {
        	 System.out.println(list.get(i));
		}
    }
	
	//����ѯ����ĵ�һ�����ݷ�װ��map��ϣ�key==������value==��ֵ��
	@Test
    public void testMapHandler() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
        String sql = "select * from user where id = ?";
        Map<String, Object> result = runner.query(sql, new MapHandler(),"1");
        System.out.println(result.get("id"));
        System.out.println(result.get("username"));
    }
	//����ѯ�����ÿһ�з�װ��map���ϣ�key==������value==��ֵ�����ٽ�map���ϴ���List����
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
	 // ����ѯ�����ÿһ�����ݣ���װ��User�����ٴ���map�����У�key==������value==��ֵ��
	@Test
    public void testBeanMapHandler() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
          String sql = "select * from user ";
          Map<Integer,User> map= (Map<Integer,User>)runner.query(sql, new BeanMapHandler<Integer, User>(User.class));
          User user=map.get(1);
          System.out.println(user);
    }
	
	//KeyedHandler
	//��������е�ÿһ�����ݶ���װ��һ��Map��ٰ���Щmap�ٴ浽һ��map���keyΪָ����key�� 
	// ����ѯ�Ľ����ÿһ�����ݣ���װ��map1��key==������value==��ֵ ����Ȼ��map1���ϣ��ж��������map2���ϣ�ֻ��һ����
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
	
	//  ScalarHandler:��װ����count��avg��max��min��sum��������������ִ�н��
	@Test
    public void testScalarHandler() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
          String sql = "select count(*) from user ";
          int totalrecord = ((Long)runner.query(sql, new ScalarHandler<Long>(1))).intValue();
          System.out.println(totalrecord);
    }
	//ArrayHandler��ѯ���� �� ScalarHandler��ͬЧ��
	@Test
    public void testScalarHandler2() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
          String sql = "select count(*) from user ";
          Object[] result = runner.query(sql, new ArrayHandler());
          int totalrecord =  ((Long) result[0]).intValue();
          System.out.println(totalrecord);
          
/*          Object[] result = runner.query(sql, new ArrayHandler());
          long totalrecord = (Long) result[0]; 
          // ��ѯ��������ܼ�¼������Ϊjava.lang.Long���ͷ��ص�
          int num = (int) totalrecord;
          System.out.println(num);*/
    }
	
	
}
