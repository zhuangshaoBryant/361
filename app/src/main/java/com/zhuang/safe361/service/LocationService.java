package com.zhuang.safe361.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service {

	private MyLocationListener listener;
	private LocationManager locationManager;
	private SharedPreferences mPreferences;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mPreferences = getSharedPreferences("config", MODE_PRIVATE);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		// 获取精准的位置提供者
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(true);// 是否允许付费,比如使用3g网络定位
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String bestProvider = locationManager.getBestProvider(criteria, true);

		listener = new MyLocationListener();
		// 参1表示位置提供者,参2表示最短更新时间,参3表示最短更新距离
		locationManager.requestLocationUpdates(bestProvider, 0, 0, listener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		locationManager.removeUpdates(listener);// 当activity销毁时,停止更新位置, 节省电量
	}

	class MyLocationListener implements LocationListener {

		// 位置发生变化
		@Override
		public void onLocationChanged(Location location) {

			System.out.println("位置改变时调用");
			// 将获取的经纬度保存在sp中
			mPreferences
					.edit()
					.putString(
							"location",
							"j:" + location.getLongitude() + ";w:"
									+ location.getLatitude()).commit();
			stopSelf();// 每次收到短信开启服务，获取坐标保存sp，然后关闭服务，省电
		}

		// 位置提供者状态发生变化，如GPS信号由有变无的过程
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		// 用户打开gps
		@Override
		public void onProviderEnabled(String provider) {

		}

		// 用户关闭gps
		@Override
		public void onProviderDisabled(String provider) {

		}

	}

}
