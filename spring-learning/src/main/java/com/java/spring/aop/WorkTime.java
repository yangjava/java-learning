package com.java.spring.aop;

import org.apache.log4j.Logger;

public class WorkTime {
	
	  Logger LOGGER=Logger.getLogger(this.getClass().getName());
	
      public  void  work(String name){
    	  Long nowTime=System.currentTimeMillis();
    	  LOGGER.info(name+"开始工作");  
    	  try {
			Thread.sleep(3000);
		   } catch (InterruptedException e) {
		  }
    	  Long lastTime=System.currentTimeMillis();
    	  LOGGER.info(name+"结束工作"); 
    	  LOGGER.info(name+"工作时长"+(lastTime-nowTime)/1000+"秒"); 
      }
	
}
