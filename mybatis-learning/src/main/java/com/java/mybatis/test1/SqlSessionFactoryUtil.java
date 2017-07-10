package com.java.mybatis.test1;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

//
public class SqlSessionFactoryUtil {

	private static SqlSessionFactory sqlSessionFactory = null;
	private static Logger LOGGER = Logger
			.getLogger(SqlSessionFactoryUtil.class);

	private static final Class<SqlSessionFactoryUtil> CLASS_LOCK = SqlSessionFactoryUtil.class;

	private SqlSessionFactoryUtil() {

	}

	public static SqlSessionFactory initSqlSessionFactory(String resource) {
		LOGGER.info("init SqlSessionFactory");
		InputStream inputStream = null;
		try {
			inputStream = Resources.getResourceAsStream(resource);
		} catch (IOException e) {
			LOGGER.info("加载mybatis配置文件异常" + e.getMessage());
		}
		synchronized (CLASS_LOCK) {
			if (sqlSessionFactory == null) {
				sqlSessionFactory = new SqlSessionFactoryBuilder()
						.build(inputStream);
			}
		}
		return sqlSessionFactory;

	}
	
	public static SqlSession openSqlSession(String resource){
		if(sqlSessionFactory==null){
			initSqlSessionFactory(resource);
		}
		return sqlSessionFactory.openSession();
	}
	
}
