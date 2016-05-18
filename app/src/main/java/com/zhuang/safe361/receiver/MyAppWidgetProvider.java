package com.zhuang.safe361.receiver;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhuang.safe361.service.UpdateWidgetService;

@SuppressLint("NewApi")
public class MyAppWidgetProvider extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}

	/**
	 * 创建第一个窗体小部件的方法
	 * @param context
	 */
	@Override
	public void onEnabled(Context context) {
		//开启服务(onCreate)
		context.startService(new Intent(context, UpdateWidgetService.class));
		super.onEnabled(context);
	}

	/**
	 * 创建多一个窗体小部件调用方法
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		//开启服务(onCreate)
		context.startService(new Intent(context, UpdateWidgetService.class));
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	/**
	 * 当窗体小部件宽高发生改变的时候调用方法,创建小部件的时候,也调用此方法
	 */
	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
		//开启服务(onCreate)
		context.startService(new Intent(context, UpdateWidgetService.class));
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
	}

	/**
	 * 删除一个窗体小部件调用方法
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	/**
	 * 删除最后一个窗体小部件调用方法
	 */
	@Override
	public void onDisabled(Context context) {
		//关闭服务
		context.stopService(new Intent(context,UpdateWidgetService.class));
		super.onDisabled(context);
	}
}
