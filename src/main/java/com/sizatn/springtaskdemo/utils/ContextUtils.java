package com.sizatn.springtaskdemo.utils;

import org.springframework.context.ApplicationContext;

public class ContextUtils {
	
	private static ContextUtils instance = null;
	private static ApplicationContext context;

	private ContextUtils() {
	}

	public static ContextUtils getInstance() {
		if (instance == null) {
			instance = new ContextUtils();
		}
		return instance;
	}

	public void setApplicationContext(ApplicationContext context) {
		ContextUtils.context = context;
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

}
