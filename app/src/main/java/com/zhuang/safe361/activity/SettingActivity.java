package com.zhuang.safe361.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.zhuang.safe361.R;
import com.zhuang.safe361.service.AddressService;
import com.zhuang.safe361.service.BlackNumberService;
import com.zhuang.safe361.service.WatchDogService;
import com.zhuang.safe361.utils.ServiceStatusUtils;
import com.zhuang.safe361.view.SettingClickView;
import com.zhuang.safe361.view.SettingItemView;

public class SettingActivity extends Activity {

	private SettingItemView sivUpdate;// 设置升级
	private SettingItemView sivAdress;// 归属地显示设置
	private SettingClickView scvAdressStyle;// 归属地显示风格设置
	private SettingClickView scvAdressLocation;// 归属地显示位置设置
	private SettingItemView siv_blacknumber;// 黑名单拦截设置
	private SettingItemView siv_app_lock;// 程序锁拦截设置

	private SharedPreferences mPreferences;// 记录是否开启自动更新

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		mPreferences = getSharedPreferences("config", MODE_PRIVATE);

		initUpdateView();
		initAddressView();
		initAddressStyle();
		initAddressLocation();
		initBlacknumber();
		initAppLock();

	}

	/**
	 * 初始化程序锁方法
	 */
	private void initAppLock() {
		siv_app_lock = (SettingItemView) findViewById(R.id.siv_app_lock);

		if (ServiceStatusUtils.isServiceRunning(this,
				"com.zhuang.safe361.service.WatchDogService")){
			siv_app_lock.setChecked(true);
		}else {
			siv_app_lock.setChecked(false);
		}

		siv_app_lock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_app_lock.isChecked()) {
					siv_app_lock.setChecked(false);
					stopService(new Intent(SettingActivity.this,
							WatchDogService.class));
				} else {
					siv_app_lock.setChecked(true);
					startService(new Intent(SettingActivity.this,
							WatchDogService.class));
				}
			}
		});
	}

	/**
	 * 拦截黑名单短信电话
	 */
	private void initBlacknumber() {
		siv_blacknumber = (SettingItemView) findViewById(R.id.siv_blacknumber);

		if (ServiceStatusUtils.isServiceRunning(this,
				"com.zhuang.safe361.service.BlackNumberService")){
			siv_blacknumber.setChecked(true);
		}else {
			siv_blacknumber.setChecked(false);
		}

		siv_blacknumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_blacknumber.isChecked()) {
					siv_blacknumber.setChecked(false);
					stopService(new Intent(SettingActivity.this,
							BlackNumberService.class));
				} else {
					siv_blacknumber.setChecked(true);
					startService(new Intent(SettingActivity.this,
							BlackNumberService.class));
				}
			}
		});
	}

	/**
	 * 初始化归属地开关
	 */
	private void initAddressView() {
		sivAdress = (SettingItemView) findViewById(R.id.siv_address);
		
		// 归属地显示设置是要看AdressService是否运行，若运行，则表示开启
		if (ServiceStatusUtils.isServiceRunning(this,
				"com.zhuang.safe361.service.AddressService")){
			sivAdress.setChecked(true);
		}else {
			sivAdress.setChecked(false);
		}

		sivAdress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sivAdress.isChecked()) {
					sivAdress.setChecked(false);
					stopService(new Intent(SettingActivity.this,
							AddressService.class));
				} else {
					sivAdress.setChecked(true);
					startService(new Intent(SettingActivity.this,
							AddressService.class));
				}
			}
		});
	}

	/**
	 * 初始化自动更新开关
	 */
	public void initUpdateView() {
		sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
		boolean autoUpdate = mPreferences.getBoolean("auto_update", true);// 第二个参数表示，如果不存在，默认为tue

		if (autoUpdate) {
			// sivUpdate.setDesc("自动更新已开启");
			sivUpdate.setChecked(true);
		} else {
			// sivUpdate.setDesc("自动更新已关闭");
			sivUpdate.setChecked(false);
		}

		// 将整体设置监听，将布局里的checkBox设置无法获取焦点
		sivUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断当前的勾选状态
				if (sivUpdate.isChecked()) {
					// 设置不勾选
					sivUpdate.setChecked(false);
					// 更新sp
					Editor editor = mPreferences.edit();
					editor.putBoolean("auto_update", false).commit();
				} else {
					sivUpdate.setChecked(true);
					Editor editor = mPreferences.edit();
					editor.putBoolean("auto_update", true).commit();
				}
			}
		});
	}
	
	final String[] items = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
	
	/**
	 * 修改提示框显示风格
	 */
	public void initAddressStyle(){
		scvAdressStyle = (SettingClickView) findViewById(R.id.scv_address_style);
		
		scvAdressStyle.setTitle("归属地提示框风格");
		int style = mPreferences.getInt("address_style", 0);// 读取保存的style
		scvAdressStyle.setDesc(items[style]);
		scvAdressStyle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSingleChooseDailog();
			}
		});
	}
	
	/**
	 * 弹出选择风格单选框
	 */
	public void showSingleChooseDailog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("归属地提示风格");
		int style = mPreferences.getInt("address_style", 0);// 读取保存的style
		
		builder.setSingleChoiceItems(items, style, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mPreferences.edit().putInt("address_style", which).commit();// 保存选择的风格
				dialog.dismiss();// 点击完让dialog消失
				scvAdressStyle.setDesc(items[which]);// 更新组合控件的描述信息
			}
		});
		
		builder.setPositiveButton("取消", null);
		builder.show();
	}
	
	/**
	 * 修改提示框显示位置
	 */
	public void initAddressLocation(){
		scvAdressLocation = (SettingClickView) findViewById(R.id.scv_address_location);
		
		scvAdressLocation.setTitle("归属地提示框显示位置");
		scvAdressLocation.setDesc("设置归属地提示框的显示位置");
		scvAdressLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this,DragViewActivity.class));
			}
		});
	}
	

}
