
工具类里面现在没有必要提供release()方法，因为我们是使用DBUtils操作数据库，
即调用DBUtils的update()和query()方法操作数据库，
它操作完数据库之后，会自动释放掉连接。


// 使用dbutils完成数据库的CRUD
public class DbutilsTest {
	
	@Test
	public void testFindUserById() throws SQLException{
		QueryRunner queryRunner=new QueryRunner(DataSource.getDataSource());
		String sql="select * from user where id = ?";
		User user=(User)queryRunner.query(sql, new BeanHandler<User>(User.class),"1");
		System.out.println(user);
	}
	
	
	
	 @Test
	    public void insert() throws SQLException {
	        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
	        String sql = "insert into user(id,username,password,sex,birthday,address) values(?,?,?,?,?,?)";
	        Object[] params = {2, "admin2", "123", "1", new Date(),"南京"};
	        int insert = runner.update(sql, params);
	        System.out.println(insert);
	    }

	    @Test
	    public void update() throws SQLException {
	        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
	        String sql = "update user set username=? where id=?";
	        Object[] params = {"admin0", 2};
	        int update =runner.update(sql, params);
	        System.out.println(update);
	    }

	    @Test
	    public void delete() throws SQLException {
	        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
	        String sql = "delete from user where id=?";
	        int delete =runner.update(sql, 2);
	        System.out.println(delete);
	    }


	    @Test
	    public void findAll() throws SQLException {
	        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
	        String sql = "select * from user";
	        List<User> list = (List<User>) runner.query(sql, new BeanListHandler<User>(User.class));
	        System.out.println(list.size()+":"+list);
	    }

	    @Test
	    public void batch() throws SQLException {
	        QueryRunner runner = new QueryRunner(DataSource.getDataSource());
	        String sql = "insert into user(id,username,password,sex,birthday,address) values(?,?,?,?,?,?)";
	        Object[][] params = new Object[3][5];
	        for (int i = 0; i < params.length; i++) {
	            params[i] = new Object[]{i+3, "admin"+i, "123", "1", new Date(),"南京"};
	        }
	        int[] batch = runner.batch(sql, params);
	        System.out.println(batch.length);
	    }
	

}