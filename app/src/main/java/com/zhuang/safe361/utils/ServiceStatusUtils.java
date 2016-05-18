package com.zhuang.safe361.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

import java.util.List;

public class ServiceStatusUtils {
	
	/**
	 * 判断服务是否在运行
	 * @return
	 */
	public static boolean isServiceRunning (Context context,String serviceName){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		 List<RunningServiceInfo> runningService = am.getRunningServices(100);
		 
		 for (RunningServiceInfo runningServiceInfo : runningService) {
			String className = runningServiceInfo.service.getClassName();// 获取服务的名称
			if(className.equals(serviceName)){
				return true;
			}
		}
		return false;
	}
}
