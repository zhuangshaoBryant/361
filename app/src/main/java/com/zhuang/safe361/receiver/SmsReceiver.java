package com.zhuang.safe361.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.gsm.SmsMessage;

import com.zhuang.safe361.R;
import com.zhuang.safe361.service.LocationService;

public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//pdu为承载着一条短信的所有短信。一条短信为140个英文字符长度，在这个长度范围内，即需一个pdu即可。
		//超出这个范围，即要分割成多个pdu数组。
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		for (Object object : objects) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
			String originatingAddress = message.getOriginatingAddress();//获取发信人地址
			String messageBody = message.getMessageBody();//短信内容
			
			if("#*alarm*#".equals(messageBody)){
				// 播放报警音乐, 即使手机调为静音,也能播放音乐, 因为使用的是媒体声音的通道,和铃声无关
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				player.setVolume(1f, 1f);
				player.setLooping(true);
				player.start();
				abortBroadcast();//中断短信的传递, 从而系统短信app就收不到内容了
			}else if("#*location*#".equals(messageBody)){
				//获取经纬度坐标
				Intent i = new Intent(context,LocationService.class);
				context.startService(i);//开启服务
				SharedPreferences mPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
				//从sp里获取位置坐标
				String location = mPreferences.getString("location", "getting location...");
				System.out.println(location);
				abortBroadcast();//中断短信的传递, 从而系统短信app就收不到内容了
			}else if("#*location*#".equals(messageBody)){
				System.out.println("远程清除数据");
				abortBroadcast();//中断短信的传递, 从而系统短信app就收不到内容了
			}else if("#*lockscreen*#".equals(messageBody)){
				System.out.println("远程锁屏");
				abortBroadcast();//中断短信的传递, 从而系统短信app就收不到内容了
			}
		}
	}

}
