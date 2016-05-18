package com.zhuang.safe361.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zhuang.safe361.R;

public class Setup3Activity extends BaseSetupActivity {

	private EditText etPhoneEditText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		
		etPhoneEditText = (EditText) findViewById(R.id.et_phone);
		String phone = mPreferences.getString("safe_phone", "");
		etPhoneEditText.setText(phone);
	}


	@Override
	public void showNextPage() {
		String phone = etPhoneEditText.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			Toast.makeText(Setup3Activity.this, "安全号码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		mPreferences.edit().putString("safe_phone", phone).commit();
		startActivity(new Intent(this, Setup4Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
	}
	
	public void selectContact(View view){
		Intent intent = new Intent(this,ContactActivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			String phoneString = data.getStringExtra("phone").trim();
			String phoneString2 = phoneString.replaceAll("-", "").replaceAll(" ", "");
			etPhoneEditText.setText(phoneString2);
		}
	}

}
