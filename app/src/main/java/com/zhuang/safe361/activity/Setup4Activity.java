package com.zhuang.safe361.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.zhuang.safe361.R;

public class Setup4Activity extends BaseSetupActivity {

	//private SharedPreferences mPreferences;父类已经实现

	private CheckBox cbProtectBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);

		cbProtectBox = (CheckBox) findViewById(R.id.cb_protect);
		
		//根据sp保存的数据来更新checkBox
		boolean protect = mPreferences.getBoolean("protect", false);
		if(protect){
			cbProtectBox.setText("防盗保护已经开启");
			cbProtectBox.setChecked(true);
		}else {
			cbProtectBox.setText("防盗保护已经关闭");
			cbProtectBox.setChecked(false);
		}
		
		// 当checkbox发生变化时,回调此方法
		cbProtectBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					cbProtectBox.setText("防盗保护已经开启");
					mPreferences.edit().putBoolean("protect", true).commit();
				}else {
					cbProtectBox.setText("防盗保护已经关闭");
					mPreferences.edit().putBoolean("protect", false).commit();
				}
			}
		});
	}

	@Override
	public void showNextPage() {
		startActivity(new Intent(this, LostFindActivity.class));
		finish();

		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动
		// 更新sp,表示已经展示过设置向导了,下次进来就不展示啦
		mPreferences.edit().putBoolean("configed", true).commit();
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
	}

}
