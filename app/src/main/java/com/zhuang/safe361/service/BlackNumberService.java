package com.zhuang.safe361.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.zhuang.safe361.dao.BlackNumberDao;

import java.lang.reflect.Method;

public class BlackNumberService extends Service {
	private BlackNumberDao mDao;
	private InnerSmsReceiver mInnerSmsReceiver;
	private TelephonyManager mTelephonyManager;
	private MyPhoneStateListener myPhoneStateListener;
	private MyContentObserver myContentObserver;

	public BlackNumberService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {

		mDao = BlackNumberDao.getInstance(getApplicationContext());

		//监听短信状态
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(1000);

		mInnerSmsReceiver = new InnerSmsReceiver();
		registerReceiver(mInnerSmsReceiver,intentFilter);

		//监听电话状态
		mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		mTelephonyManager.listen(myPhoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}



	class InnerSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//获取短信内容,获取发送短信电话号码,如果此电话号码在黑名单中,并且拦截模式也为1(短信)或者3(所有),拦截短信
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[])object);
				String originatingAddress = sms.getOriginatingAddress();
				int mode = mDao.getMode(originatingAddress);
				if(mode==1 || mode==3){
					abortBroadcast();
				}
			}
		}
	}

	class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state){
				case TelephonyManager.CALL_STATE_IDLE:
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					endCall(incomingNumber);
					break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	private void endCall(String phone) {
		int mode = mDao.getMode(phone);
		if(mode ==2 || mode == 3){
			//ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
			//ServiceManager此类android对开发者隐藏,所以不能去直接调用其方法,需要反射调用
			try {
				//1,获取ServiceManager字节码文件
				Class<?> clazz = Class.forName("android.os.ServiceManager");
				//2,获取方法
				Method method = clazz.getMethod("getService",String.class);
				//3,反射调用此方法
				IBinder iBinder = (IBinder) method.invoke(null,Context.TELEPHONY_SERVICE);
				//4,调用获取aidl文件对象方法
				ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
				//5,调用在aidl中隐藏的endCall方法
				iTelephony.endCall();
				/*
				内容提供者:对外提供数据库的访问方式
				内容解析器:用内容提供者提供的访问方式Uri,访问数据库(增删改查)
				内容观察者:观察数据库的变化,一旦数据发生改变,调用相应方法

				通过内容观察者,观察数据库的插入,一旦有插入,则做删除此条插入数据操作
				 */
				//6、在内容解析器上,去注册内容观察者,通过内容观察者,观察数据库(Uri决定那张表那个库)的变化
				myContentObserver = new MyContentObserver(new Handler(),phone);
				getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"),true,myContentObserver);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class MyContentObserver extends ContentObserver{

		private String phone;

		public MyContentObserver(Handler handler,String phone) {
			super(handler);
			this.phone = phone;
		}

		//数据库中指定calls表发生改变的时候会去调用方法
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			//为什么不直接用下面这句，因为这句会删除掉以前的记录，对于本次的不删除，即先删除在插入
			getContentResolver().delete(Uri.parse("content://call_log/calls"),"number = ?",new String[]{phone});
		}
	}

	@Override
	public void onDestroy() {
		//注销广播
		if(mInnerSmsReceiver!=null){
			unregisterReceiver(mInnerSmsReceiver);
		}

		////注销内容观察者
		if(myContentObserver != null){
			getContentResolver().unregisterContentObserver(myContentObserver);
		}

		//取消对电话状态的监听
		if(myPhoneStateListener != null){
			mTelephonyManager.listen(myPhoneStateListener,PhoneStateListener.LISTEN_NONE);
		}

		super.onDestroy();
	}
}
