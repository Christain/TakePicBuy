package com.unionbigdata.takepicbuy.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.unionbigdata.takepicbuy.baseclass.BaseActivity;

import java.util.Stack;

public class AppManage {
	private Stack<BaseActivity> activities;
	private volatile static AppManage instance;

	public static AppManage getInstance() {
		if (instance == null) {
			synchronized (AppManage.class) {
				if (instance == null) {
					instance = new AppManage();
				}
			}
		}
		return instance;
	}

	private AppManage() {
		activities = new Stack<BaseActivity>();
	}

	public void addActivity(BaseActivity activity) {
		if (activity != null) {
			activities.add(activity);
		}
	}
	

	public void removeActivity(BaseActivity activity) {
		if (activity != null) {
			activities.remove(activity);
		}
	}

	public void finishActivity(BaseActivity activity) {
		if (activity != null) {
			activities.remove(activity);
		}
		activity.finishActivity();
		activity = null;
	}

	public BaseActivity getCurrentActivity() {
		BaseActivity activity = activities.lastElement();
		return activity;
	}
	

	@SuppressWarnings("deprecation")
	public void exit(Context context) {
		try {
			for (BaseActivity activity : activities) {
				if (activity != null) {
					activity.finishActivity();
				}
			}
			activities.clear();
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			manager.restartPackage(context.getPackageName());
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clearEverything(Context context){
		for (BaseActivity activity : activities) {
			if (activity != null) {
				activity.finishActivity();
			}
		}
		activities.clear();
	}

}
