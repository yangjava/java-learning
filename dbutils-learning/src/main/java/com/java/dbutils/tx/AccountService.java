package com.java.dbutils.tx;

public class AccountService {
	
	/*	为了能够让事务处理更加优雅，我们使用ThreadLocal类进行改造，
	ThreadLocal一个容器，向这个容器存储的对象，在当前线程范围内都可以取得出来，
	向ThreadLocal里面存东西就是向它里面的Map存东西的，
	然后ThreadLocal把这个Map挂到当前的线程底下，这样Map就只属于这个线程了*/
	public void updateAccount(String fromname, String toname, double money) {

		AccountDao ad = new AccountDaoImpl();

		try {
			ThreadLocalDataSource.startTransaction();// begin

			// 分别得到转出和转入账户对象
			Account fromAccount = ad.findAccountByName(fromname);
			Account toAccount = ad.findAccountByName(toname);
            System.out.println("转出账户"+fromAccount);
            System.out.println("转入账户"+toAccount);
			// 修改账户各自的金额
			fromAccount.setMoney(fromAccount.getMoney() - money);
			toAccount.setMoney(toAccount.getMoney() + money);

			// 完成转账操作
			ad.updateAccout(fromAccount);
			// int i = 10/0;
			ad.updateAccout(toAccount);

			ThreadLocalDataSource.commit();// 提交事务
		} catch (Exception e) {
			e.printStackTrace();
			// 出现异常之后就回滚事务
			ThreadLocalDataSource.rollback();
		} finally {
			// 关闭数据库连接
			ThreadLocalDataSource.close();
		}
	}

}
