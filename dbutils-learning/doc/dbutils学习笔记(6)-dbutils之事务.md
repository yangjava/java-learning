## JDBC�����е�������
	�ڿ����У������ݿ�Ķ������߶�һ�����еĶ�������ִ�и��²���ʱҪ��֤�Զ�����²���
	Ҫôͬʱ�ɹ���Ҫô�����ɹ���
	����漰���Զ�����²�����������������ˡ�
	��������ҵ���е�ת�����⣬
	A�û���B�û�ת��100Ԫ������A�û���B�û���Ǯ���洢��Account��
	��ôA�û���B�û�ת��ʱ���漰��ͬʱ����Account���е�A�û���Ǯ��B�û���Ǯ��
	��SQL����ʾ���ǣ�
	
	1 update account set money=money-100 where name='A'
    2 update account set money=money+100 where name='B'
	
	
## ��dao��������

public interface AccountDao {

	
    /** 
     * ת�� 
     * @param fromname ת���û� 
     * @param toname  ת���û� 
     * @param money  ת�˽�� 
     */  
    public void transfer(String fromname,String toname,double money)throws Exception; 
	
	}
	
	/*AccountDao�����transfer�������Դ���ת��ҵ�񣬲��ұ�֤����ͬһ�������н��У�
	 ����AccountDao�����transfer�����Ǵ��������û�֮���ת��ҵ��ģ�
	 �Ѿ��漰�������ҵ�������Ӧ����ҵ�����������Ӧ�ó�����DAO��ģ�
	 �ڿ����У�DAO���ְ��Ӧ��ֻ�漰��������CRUD�����漰�����ҵ�������
	 �����ڿ�����DAO�����������ҵ��������һ�ֲ��õ���ơ�*/
	@Override
	public void transfer(String fromname, String toname, double money)
			throws Exception {
		Connection conn = null;
		try {
			conn = DataSourceUtils.getConnection();
			// ��������
			conn.setAutoCommit(false);
			/**
			 * �ڴ���QueryRunner����ʱ������������Դ��������Ϊ�˱�֤������SQL��ͬһ�������н��У�
			 * �����ֶ���ȡ���ݿ����ӣ�Ȼ����������SQLʹ��ͬһ�����ݿ�����ִ��
			 */
			QueryRunner runner = new QueryRunner();
			String sql1 = "update account set money=money-? where name=?";
			String sql2 = "update account set money=money+? where name=?";
			Object[] param1 = { money, fromname };
			Object[] param2 = { money, toname };
			runner.update(conn, sql1, param1);
			// ģ���������쳣������ع�
			// int x = 1/0;
			runner.update(conn, sql2, param2);
			// sql����ִ��֮����ύ����
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (conn != null) {
				// �����쳣֮��ͻع�����
				conn.rollback();
			}
		} finally {
			// �ر����ݿ�����
			conn.close();
		}

	}
	
	
	
	public class TransactionTest {
	
	
/*	 ����AccountDao�����transfer�������Դ���ת��ҵ�񣬲��ұ�֤����ͬһ�������н��У�
	����AccountDao�����transfer�����Ǵ��������û�֮���ת��ҵ��ģ�
	�Ѿ��漰�������ҵ�������Ӧ����ҵ�����������Ӧ�ó�����DAO��ģ�
	�ڿ����У�DAO���ְ��Ӧ��ֻ�漰��������CRUD�����漰�����ҵ�������
	�����ڿ�����DAO�����������ҵ��������һ�ֲ��õ���ơ�*/
	@Test
	public void testDaoTx() throws Exception{
		 AccountDao accountDao=new AccountDaoImpl();
		 accountDao.transfer("a", "b",100.0);
		 System.out.println("ʹ��һ��dao�����������");
	}
	
}

## ��servive�д�������

public interface AccountDao {
    
    /** 
     * �����˻���Ϣ�޸Ľ�� 
     * @param accout 
     */  
    public void updateAccout(Account accout) throws Exception;  
      
    /** 
     * �����û��������˻���Ϣ 
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
	
	/*	Ϊ���ܹ���������������ţ�����ʹ��ThreadLocal����и��죬
	ThreadLocalһ������������������洢�Ķ����ڵ�ǰ�̷߳�Χ�ڶ�����ȡ�ó�����
	��ThreadLocal����涫���������������Map�涫���ģ�
	Ȼ��ThreadLocal�����Map�ҵ���ǰ���̵߳��£�����Map��ֻ��������߳���*/
	public void updateAccount(String fromname, String toname, double money) {

		AccountDao ad = new AccountDaoImpl();

		try {
			ThreadLocalDataSource.startTransaction();// begin

			// �ֱ�õ�ת����ת���˻�����
			Account fromAccount = ad.findAccountByName(fromname);
			Account toAccount = ad.findAccountByName(toname);
            System.out.println("ת���˻�"+fromAccount);
            System.out.println("ת���˻�"+toAccount);
			// �޸��˻����ԵĽ��
			fromAccount.setMoney(fromAccount.getMoney() - money);
			toAccount.setMoney(toAccount.getMoney() + money);

			// ���ת�˲���
			ad.updateAccout(fromAccount);
			// int i = 10/0;
			ad.updateAccout(toAccount);

			ThreadLocalDataSource.commit();// �ύ����
		} catch (Exception e) {
			e.printStackTrace();
			// �����쳣֮��ͻع�����
			ThreadLocalDataSource.rollback();
		} finally {
			// �ر����ݿ�����
			ThreadLocalDataSource.close();
		}
	}

}


/*	Ϊ���ܹ���������������ţ�����ʹ��ThreadLocal����и��죬
	ThreadLocalһ������������������洢�Ķ����ڵ�ǰ�̷߳�Χ�ڶ�����ȡ�ó�����
	��ThreadLocal����涫���������������Map�涫���ģ�
	Ȼ��ThreadLocal�����Map�ҵ���ǰ���̵߳��£�����Map��ֻ��������߳���*/
	@Test
	public void testServiceTx() throws Exception{
		 AccountService accountService=new AccountService();
		 accountService.updateAccount("a", "b",100.0);
		 System.out.println("ʹ�ý����������");
	}