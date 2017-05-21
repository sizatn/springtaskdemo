package com.sizatn.springtaskdemo.task;

import java.io.FileNotFoundException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Log4jConfigurer;

import com.sizatn.springtaskdemo.base.service.TaskService;
import com.sizatn.springtaskdemo.utils.ContextUtils;

public class Start {

	static {
		try {
			Log4jConfigurer.initLogging("classpath:log4j.xml");
		} catch (FileNotFoundException ex) {
			System.err.println("加载log4j配置失败" + ex.getMessage());
		}
	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
		ContextUtils.getInstance().setApplicationContext(context);

		String[] beans = context.getBeanNamesForType(TaskService.class);
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
		int poolSize = 0;
		for (String b : beans) {
			TaskService ts = (TaskService) context.getBean(b);
			if (!ts.isRun())
				continue;
			if (ts.isOneTime()) {
				new Thread(ts).start();
			} else {
				executor.scheduleWithFixedDelay(ts, 3, ts.getPeriod(), TimeUnit.SECONDS);
				poolSize++;
			}
		}
		executor.setCorePoolSize(poolSize + 1);

	}
}
