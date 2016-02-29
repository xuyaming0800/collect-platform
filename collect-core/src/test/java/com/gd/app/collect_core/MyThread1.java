package com.gd.app.collect_core;

import java.util.concurrent.CountDownLatch;

import com.autonavi.collect.service.TaskCollectUtilService;

public class MyThread1 extends Thread {
	 private TaskCollectUtilService taskCollectUtilService;
	 private String userName;
	 private CountDownLatch latch;
	 public MyThread1(TaskCollectUtilService taskCollectUtilService,String userName, CountDownLatch latch){
		 super();
		 this.taskCollectUtilService=taskCollectUtilService;
		 this.userName=userName;
		 this.latch=latch;
	 }
	 public void run(){
		 Long id=taskCollectUtilService.getUserIdCache(userName);
		 latch.countDown();
	 }

}
