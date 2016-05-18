package com.zhuang.safe361.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.zhuang.safe361.R;
import com.zhuang.safe361.service.LockScreenService;
import com.zhuang.safe361.utils.ServiceStatusUtils;

public class ProcessSettingActivity extends Activity {

	private CheckBox cb_show_system;
	private SharedPreferences sp;
	private CheckBox cb_lock_clear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);
		sp = getSharedPreferences("config.xml", Context.MODE_PRIVATE);
		initSystemShow();
		initLockScreenClear();
	}

	private void initLockScreenClear() {
		cb_lock_clear = (CheckBox) findViewById(R.id.cb_lock_clear);

		//对之前存储过的状态进行回显
		boolean isRunning = ServiceStatusUtils.isServiceRunning(this,"com.zhuang.safe361.service.LockScreenService");
		cb_lock_clear.setChecked(isRunning);

		if (isRunning) {
			cb_lock_clear.setText("锁屏清理已开启");
		}else{
			cb_lock_clear.setText("锁屏清理已关闭");
		}

		cb_lock_clear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_lock_clear.setText("锁屏清理已开启");
					startService(new Intent(ProcessSettingActivity.this, LockScreenService.class));
				}else{
					cb_lock_clear.setText("锁屏清理已关闭");
					stopService(new Intent(ProcessSettingActivity.this, LockScreenService.class));
				}

			}
		});
	}

	private void initSystemShow() {
		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);

		//对之前存储过的状态进行回显
		boolean showSystem = sp.getBoolean("showSystem",true);
		cb_show_system.setChecked(showSystem);

		if (showSystem) {
			cb_show_system.setText("显示系统进程");
		}else{
			cb_show_system.setText("隐藏系统进程");
		}

		cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_show_system.setText("显示系统进程");
				}else{
					cb_show_system.setText("隐藏系统进程");
				}
				sp.edit().putBoolean("showSystem",isChecked).commit();
			}
		});
	}


}
