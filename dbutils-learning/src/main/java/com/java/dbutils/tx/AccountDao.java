package com.java.dbutils.tx;

public interface AccountDao {

	
    /** 
     * 转账 
     * @param fromname 转出用户 
     * @param toname  转入用户 
     * @param money  转账金额 
     */  
    public void transfer(String fromname,String toname,double money)throws Exception;  
      
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
