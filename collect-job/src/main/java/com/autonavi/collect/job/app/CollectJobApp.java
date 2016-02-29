package com.autonavi.collect.job.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 * 
 */
public class CollectJobApp {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		// 如果配置文件中将startQuertz bean的lazy-init设置为false 则不用实例化
		// context.getBean("startQuertz")
	}
}
