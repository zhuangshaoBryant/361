package com.zhuang.safe361.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.zhuang.safe361.R;
import com.zhuang.safe361.dao.ProcessInfoProvider;
import com.zhuang.safe361.receiver.MyAppWidgetProvider;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateWidgetService extends Service {

	private Timer mTimer;
	private InnerReceiver mInnerReceiver;

	public UpdateWidgetService() {
	}

	@Override
	public void onCreate() {
		startTimer();

		//注册开锁,解锁广播接受者
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

		mInnerReceiver = new InnerReceiver();
		registerReceiver(mInnerReceiver,intentFilter);
		super.onCreate();
	}

	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
				startTimer();
			}else{
				cancelTimerTask();
			}
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 2秒一次的定时任务现在正在运行
	 */
	public void startTimer(){
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				updataAppWidget();
			}
		}, 0, 2000);
	}

	public void cancelTimerTask(){
		if(mTimer != null){
			mTimer.cancel();
			mTimer=null;
		}
	}

	public void updataAppWidget(){
		//1.获取AppWidget对象
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		//2.获取窗体小部件布局转换成的view对象(定位应用的包名,当前应用中的那块布局文件)
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
		//3.给窗体小部件布view对象,内部控件赋值
		remoteViews.setTextViewText(R.id.tv_process_count,"进程总数:  " + ProcessInfoProvider.getProcessCount(this));
		//4.显示可用内存大小
		String strAvailSpace = Formatter.formatFileSize(this,ProcessInfoProvider.getAvailSpace(this));
		remoteViews.setTextViewText(R.id.tv_process_memory,"可用内存:  "+strAvailSpace);

		//点击窗体小部件,进入应用
		//1:在那个控件上响应点击事件2:延期的意图
		Intent intent = new Intent("android.intent.action.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.ll_root,pendingIntent);

		Intent broadCastIntent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
		PendingIntent broadcast = PendingIntent.getBroadcast(this,0,broadCastIntent,PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.btn_clear,broadcast);

		//上下文环境,窗体小部件对应广播接受者的字节码文件
		ComponentName componentName = new ComponentName(this, MyAppWidgetProvider.class);
		appWidgetManager.updateAppWidget(componentName,remoteViews);
	}

	@Override
	public void onDestroy() {
		if(mInnerReceiver != null){
			unregisterReceiver(mInnerReceiver);
		}
		//调用onDestroy即关闭服务,关闭服务的方法在移除最后一个窗体小部件的时调用,定时任务也没必要维护
		cancelTimerTask();
		super.onDestroy();
	}
}
