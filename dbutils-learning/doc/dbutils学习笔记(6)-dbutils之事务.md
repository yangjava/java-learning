## JDBC开发中的事务处理
	在开发中，对数据库的多个表或者对一个表中的多条数据执行更新操作时要保证对多个更新操作
	要么同时成功，要么都不成功，
	这就涉及到对多个更新操作的事务管理问题了。
	比如银行业务中的转账问题，
	A用户向B用户转账100元，假设A用户和B用户的钱都存储在Account表，
	那么A用户向B用户转账时就涉及到同时更新Account表中的A用户的钱和B用户的钱，
	用SQL来表示就是：
	
	1 update account set money=money-100 where name='A'
    2 update account set money=money+100 where name='B'
	
	
## 在dao处理事务

public interface AccountDao {

	
    /** 
     * 转账 
     * @param fromname 转出用户 
     * @param toname  转入用户 
     * @param money  转账金额 
     */  
    public void transfer(String fromname,String toname,double money)throws Exception; 
	
	}
	
	/*AccountDao的这个transfer方法可以处理转账业务，并且保证了在同一个事务中进行，
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
	
	
	
	public class TransactionTest {
	
	
/*	 上面AccountDao的这个transfer方法可以处理转账业务，并且保证了在同一个事务中进行，
	但是AccountDao的这个transfer方法是处理两个用户之间的转账业务的，
	已经涉及到具体的业务操作，应该在业务层中做，不应该出现在DAO层的，
	在开发中，DAO层的职责应该只涉及到基本的CRUD，不涉及具体的业务操作，
	所以在开发中DAO层出现这样的业务处理方法是一种不好的设计。*/
	@Test
	public void testDaoTx() throws Exception{
		 AccountDao accountDao=new AccountDaoImpl();
		 accountDao.transfer("a", "b",100.0);
		 System.out.println("使用一个dao进行事务操作");
	}
	
}

## 在servive中处理事务

public interface AccountDao {
    
    /** 
     * 根据账户信息修改金额 
     * @param accout 
     */  
    public void updateAccout(Account accout) throws Exception;  
      
    /** 
     * 根据用户名查找账户信息 
     * @param name 
     * @return 
     * @throws Exception 
     */  
    public Account findAccountByName(String name)throws Exception;  
}
	public class AccountDaoImpl implements AccountDao {
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


/*	为了能够让事务处理更加优雅，我们使用ThreadLocal类进行改造，
	ThreadLocal一个容器，向这个容器存储的对象，在当前线程范围内都可以取得出来，
	向ThreadLocal里面存东西就是向它里面的Map存东西的，
	然后ThreadLocal把这个Map挂到当前的线程底下，这样Map就只属于这个线程了*/
	@Test
	public void testServiceTx() throws Exception{
		 AccountService accountService=new AccountService();
		 accountService.updateAccount("a", "b",100.0);
		 System.out.println("使用进行事务操作");
	}