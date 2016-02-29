package com.gd.app.collect_core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.collect.manager.SyncTaskBusinessMananger;

public class MyThread extends Thread {
	private Logger logger = LogManager.getLogger(this.getClass());
	private String name;
    private List<ResultEntity> l=new ArrayList<ResultEntity>();
    private SyncTaskBusinessMananger syncTaskBusinessMananger;
    private CountDownLatch latch;
  public MyThread(String name,List<ResultEntity> l,SyncTaskBusinessMananger syncTaskBusinessMananger,CountDownLatch latch){
	  super();
	  this.name=name;
	  this.l=l;
	  this.syncTaskBusinessMananger=syncTaskBusinessMananger;
	  this.latch=latch;
  }
  public void run(){
	  for(ResultEntity entity:l){
		  System.out.println("线程["+this.name+"]发送消息");
		  try {
			syncTaskBusinessMananger.getSendCollectInfoJsonToAuditQueue().execute(entity);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	  }
	  latch.countDown();
	  
  }

}
