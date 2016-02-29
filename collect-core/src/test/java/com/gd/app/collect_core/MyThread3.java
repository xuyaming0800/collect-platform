package com.gd.app.collect_core;

import java.util.concurrent.CountDownLatch;

import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;

public class MyThread3 extends Thread {
	//private Logger logger = LogManager.getLogger(this.getClass());
    private CountDownLatch latch;
  public MyThread3(CountDownLatch latch){
	  super();
	  this.latch=latch;
  }
  public void run(){
	  BusinessRunException e=new BusinessRunException(BusinessExceptionEnum.IMAGE_INFO_ERROR);
	  System.out.println(e.getSqlExpEnum().getMessage());
	  latch.countDown();
	  
  }

}
