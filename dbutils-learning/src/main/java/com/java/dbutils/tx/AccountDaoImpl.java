package com.java.dbutils.tx;

import java.sql.Connection;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

public class AccountDaoImpl implements AccountDao {

/*	 AccountDao的这个transfer方法可以处理转账业务，并且保证了在同一个事务中进行，
	 但是AccountDao的这个transfer方法是处理两个用户之间的转账业务的，
	 已经涉及到具体的业务操作，应该在业务层中做，不应该出现在DAO层的，
	 在开发中，DAO层的职责应该只涉及到基本的CRUD，不涉及具体的业务操作，
	 所以在开发中DAO层出现这样的业务处理方法是一种不好的设计。*/
	@Override
	public void transfer(String fromname, String toname, double money)
			throws Exception {
		Connection conn = null;
		try {
			conn = DataSourceUtils.getConnection();
			// 开启事务
			conn.setAutoCommit(false);
			/**
			 * 在创建QueryRunner对象时，不传递数据源给它，是为了保证这两条SQL在同一个事务中进行，
			 * 我们手动获取数据库连接，然后让这两条SQL使用同一个数据库连接执行
			 */
			QueryRunner runner = new QueryRunner();
			String sql1 = "update account set money=money-? where name=?";
			String sql2 = "update account set money=money+? where name=?";
			Object[] param1 = { money, fromname };
			Object[] param2 = { money, toname };
			runner.update(conn, sql1, param1);
			// 模拟程序出现异常让事务回滚
			// int x = 1/0;
			runner.update(conn, sql2, param2);
			// sql正常执行之后就提交事务
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (conn != null) {
				// 出现异常之后就回滚事务
				conn.rollback();
			}
		} finally {
			// 关闭数据库连接
			conn.close();
		}

	}

	@Override
	public void updateAccout(Account account) throws Exception {
		QueryRunner qr = new QueryRunner();
		String sql = "update account set money=? where name=?";
		qr.update(ThreadLocalDataSource.getConnection(), sql,
				account.getMoney(), account.getName());
	}

	@Override
	public Account findAccountByName(String name) throws Exception {
		QueryRunner qr = new QueryRunner();
		String sql = "select * from account where name=?";
		return qr.query(ThreadLocalDataSource.getConnection(), sql,
				new BeanHandler<Account>(Account.class), name);
	}

}
