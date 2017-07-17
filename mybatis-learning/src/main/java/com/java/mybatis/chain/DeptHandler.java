package com.java.mybatis.chain;
//部门经理
public class DeptHandler extends ConsumeHandler{
	 @Override
	    public void doHandler(String user, double free) {
	        if (free < 1000) {
	            
	            if (user.equals("zy")) {
	                System.out.println("给予报销:" + free);
	            } else {
	                System.out.println("报销不通过");
	            }
	            
	        } else {
	            if (getNextHandler() != null) {
	                
	                getNextHandler().doHandler(user, free);
	            }
	        }
	        
	    }
}
