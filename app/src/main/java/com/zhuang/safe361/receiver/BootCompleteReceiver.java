package com.zhuang.safe361.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences mPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean protect = mPreferences.getBoolean("protect", false);
		//只有在防盗保护开启的前提下才进行sim卡判断
		if(protect){
			String sim = mPreferences.getString("sim", null);
			
			if(!TextUtils.isEmpty(sim)){
				// 获取当前手机的sim卡
				TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				String currentSIM = tManager.getSimSerialNumber()+"11";
				if(sim.equals(currentSIM)){
					System.out.println("安全");
				}else {
					System.out.println("不安全");
					String safephone = mPreferences.getString("safe_phone", "");
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(safephone, null, "SIM card changed", null, null);
				}
			}
		}
		
	}

}
