package com.zhuang.safe361.activity;

import android.content.Intent;
import android.os.Bundle;

import com.zhuang.safe361.R;

public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup33);
	}


	@Override
	public void showNextPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		
		//两个界面切换的动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);//进入动画和退出动画
	}

	@Override
	public void showPreviousPage() {
		
	}

}
