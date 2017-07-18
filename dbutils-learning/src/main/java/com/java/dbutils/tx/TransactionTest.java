package com.java.dbutils.tx;

import org.junit.Test;

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
	
	
	
	
	
	
	
	
}
