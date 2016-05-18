package com.zhuang.safe361.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.zhuang.safe361.dao.ProcessInfoProvider;

public class LockScreenService extends Service {

	private InnerReceiver innerReceiver;

	public LockScreenService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		//锁屏action
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver,intentFilter);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		if(innerReceiver!=null){
			unregisterReceiver(innerReceiver);
		}
		super.onDestroy();
	}

	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			ProcessInfoProvider.killAllProcess(context);
		}
	}
}
