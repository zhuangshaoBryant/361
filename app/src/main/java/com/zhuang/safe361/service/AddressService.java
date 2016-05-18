package com.zhuang.safe361.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhuang.safe361.R;
import com.zhuang.safe361.dao.AddressDao;

/**
 * 来电提醒归属地的服务
 * 
 * @author zhuang
 * 
 */
public class AddressService extends Service {

	private TelephonyManager telephonyManager;
	private MyListener listener;
	private OutCallReceiver receiver;
	private WindowManager mWindowManager;
	private SharedPreferences mPreferences;
	private View view;
	private int winWidth;
	private int winHeight;
	private int startX,startY;
	private WindowManager.LayoutParams params;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mPreferences = getSharedPreferences("config", MODE_PRIVATE);

		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);// 监听来电的状态

		/*
		 * 在服务里写广播接收者，只有服务打开的时候，广播接收者才工作，所以要动态的注册广播接收者， 这样取消归属地显示的时候就不会有去电显示了
		 */
		receiver = new OutCallReceiver();
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, intentFilter);

	}

	class MyListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 电话铃声响了
				String address = AddressDao.getAddress(incomingNumber);
				// Toast.makeText(AddressService.this, address,
				// Toast.LENGTH_LONG).show();
				showToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:// 电话闲置，就是挂断电话后
				if (mWindowManager != null && view != null) {
					mWindowManager.removeView(view);// 从window中移除view,电话挂完不在显示Toast对话框
					view = null;
				}

			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(receiver);
	}

	/**
	 * 去电广播接收者
	 */
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String number = getResultData();// 获取去电电话
			String address = AddressDao.getAddress(number);
			// Toast.makeText(context, address, Toast.LENGTH_LONG).show();
			showToast(address);
		}

	}

	/**
	 * 自定义归属地浮窗
	 */
	public void showToast(String text) {
		mWindowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		winWidth = mWindowManager.getDefaultDisplay().getWidth();
		winHeight = mWindowManager.getDefaultDisplay().getHeight();

		 params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;

		params.gravity = Gravity.LEFT + Gravity.TOP;// 将重心位置设置为左上方,也就是(0,0)从左上方开始,而不是默认的重心位置

		params.setTitle("Toast");

		int lastX = mPreferences.getInt("lastX", 0);
		int lastY = mPreferences.getInt("lastY", 0);

		// 设置浮窗的位置
		params.x = lastX;
		params.y = lastY;

		int style = mPreferences.getInt("address_style", 0);

		int[] bgs = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };

		view = View.inflate(this, R.layout.toast_address, null);

		TextView tvText = (TextView) view.findViewById(R.id.tv_number);
		tvText.setText(text);
		view.setBackgroundResource(bgs[style]);// 根据存储的样式更新背景
		mWindowManager.addView(view, params);
		
		 

		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();
					int dx = endX - startX;
					int dy = endY - startY;
					
					// 更新浮窗位置
					params.x += dx;
					params.y += dy;
					
					// 防止坐标偏离屏幕
					if(params.x < 0){
						params.x = 0;
					}
					if(params.y <0){
						params.y = 0;
					}
					
					// 防止坐标偏离屏幕,虽然在屏幕上不能移出去，但是他的坐标在一直增大，那么在归属地设置位置的地方就会从位置坐标来绘制，从而看不到了
					if(params.x > winWidth - v.getWidth()){
						params.x = winWidth - v.getWidth();
					}
					if(params.y > winHeight - v.getHeight()){
						params.y = winHeight - v.getHeight();
					}
					
					
					mWindowManager.updateViewLayout(v, params);
					
					// 重新初始化起点坐标
					startX = endX;
					startY = endY;
					
					break;
				case MotionEvent.ACTION_UP:
					// 记录坐标点
					Editor editor = mPreferences.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
					break;

				default:
					break;
				}
				return false;
			}
		});
	}

}
