package com.zhuang.safe361.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuang.safe361.R;

public class LostFindActivity extends Activity {

	private SharedPreferences mPreferences;
	private ImageView ivProtect;//是否开启保护的锁图标
	private TextView tvSafePhone; //设置安全联系认得电话号码
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_find);

		mPreferences = getSharedPreferences("config", MODE_PRIVATE);

		boolean configed = mPreferences.getBoolean("configed", false);
		// 判断是否进入过设置向导,默认为false
		if(configed){
			setContentView(R.layout.activity_lost_find);
			// 根据sp更新安全号码
			tvSafePhone = (TextView) findViewById(R.id.tv_safe_phone);
			String phone = mPreferences.getString("safe_phone", "");
			tvSafePhone.setText(phone);
			// 根据sp更新保护锁
			ivProtect = (ImageView) findViewById(R.id.iv_protect);
			boolean protect = mPreferences.getBoolean("protect", false);
			if(protect){
				ivProtect.setImageResource(R.drawable.lock);
			}else {
				ivProtect.setImageResource(R.drawable.unlock);
			}
		}else {
			//跳转到设置向导页
			startActivity(new Intent(LostFindActivity.this,Setup1Activity.class));
			finish();
		}
	}
	
	/**
	 * 重新进入设置向导
	 * @param view
	 */
	public void reEnter(View view){
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
	}

}
