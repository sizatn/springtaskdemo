package com.sizatn.springtaskdemo.base.service;

import org.apache.log4j.Logger;

public abstract class TaskService implements Runnable {
	
	private static final Logger log = Logger.getLogger(TaskService.class);
	
	private boolean run = true;
	private long period = 60L;
	private boolean oneTime = false;

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		try {
			execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		log.info("线程执行花费时间(毫秒):" + (endTime - startTime));
	}

	public abstract void execute() throws Exception;

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public boolean isOneTime() {
		return oneTime;
	}

	public void setOneTime(boolean oneTime) {
		this.oneTime = oneTime;
	}
}
