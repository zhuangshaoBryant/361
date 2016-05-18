package com.zhuang.safe361.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhuang.safe361.dao.ProcessInfoProvider;

public class KillProcessReceiver extends BroadcastReceiver {
	public KillProcessReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		ProcessInfoProvider.killAllProcess(context);
	}
}
