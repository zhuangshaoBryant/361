package com.zhuang.safe361.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.zhuang.safe361.R;
import com.zhuang.safe361.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView sivSIM;//自定义的SIM卡绑定
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		
		sivSIM = (SettingItemView) findViewById(R.id.siv_sim);
		
		String sim = mPreferences.getString("sim", null);
		
		if(!TextUtils.isEmpty(sim)){
			sivSIM.setChecked(true);//下面会显示已绑定
		}else {
			sivSIM.setChecked(false);
		}
		
		//SIM卡设置监听
		sivSIM.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(sivSIM.isChecked()){
					sivSIM.setChecked(false);
					mPreferences.edit().remove("sim").commit();//删除已绑定的SIMka
				}else {
					sivSIM.setChecked(true);
					//安卓自带的，可以获取SIM卡的序列号
					TelephonyManager tManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String simSerialNumber = tManager.getSimSerialNumber();//获取SIM卡的序列号
					mPreferences.edit().putString("sim", simSerialNumber).commit();
				}
			}
		});
		
	}


	@Override
	public void showNextPage() {
		String sim = mPreferences.getString("sim", null);
		//必须绑定SIM卡，否则不允许下一页
		if(TextUtils.isEmpty(sim)){
			Toast.makeText(Setup2Activity.this, "为了您的安全，请务必绑定SIM卡", Toast.LENGTH_SHORT).show();
			return;
		}
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
		
		overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
	}

}
