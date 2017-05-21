package com.sizatn.springtaskdemo.service;

import org.apache.log4j.Logger;

import com.sizatn.springtaskdemo.base.service.TaskService;

public class SyncDataService extends TaskService {

	private static final Logger log = Logger.getLogger(SyncDataService.class);

	@Override
	public void execute() throws Exception {
		log.info("我在运行……");
	}

}
