package com.zhuang.safe361.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;

import com.zhuang.safe361.R;
import com.zhuang.safe361.dao.SmsBackUp;

import java.io.File;

/**
 * 高级工具
 * @author zhuang
 *
 */
public class AdvanceToolsActivity extends Activity {

	private ProgressBar pb_bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advance_tools);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
	}

	/**
	 * 短信备份点击事件
	 */
	public void onClickSmsBackUP(View view){
		showSmsBackUpDialog();
	}

	/**
	 * 创建进度条对话框
	 */
	private void showSmsBackUpDialog() {
		//1,创建一个带进度条的对话框
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setTitle("短信备份");
		//2,指定进度条的样式为水平
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//3、展示进度条
		progressDialog.show();
		//pb_bar.setVisibility(View.VISIBLE);
		//4、调用备份短信方法
		new Thread(){
			@Override
			public void run() {
				String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sms.xml";
				SmsBackUp.backup(getApplicationContext(),path,new SmsBackUp.CallBack() {
					@Override
					public void setMax(int max) {
						//由开发者自己决定,使用对话框还是进度条
						progressDialog.setMax(max);
						//pb_bar.setMax(max);
					}

					@Override
					public void setProgress(int index) {
						progressDialog.setProgress(index);
						//pb_bar.setProgress(index);
					}
				});

				progressDialog.dismiss();
			}
		}.start();
	}

	/**
	 * 归属地查询点击事件
	 */
	public void numberAddressQuery(View view){
		startActivity(new Intent(this,AddressQueryActivity.class));
	}

	/**
	 * 常用号码查询点击事件
	 */
	public void onClickCommonNumberQuery(View view){
		startActivity(new Intent(this,CommonNumberQueryActivity.class));
	}

	/**
	 * 程序锁点击事件
	 */
	public void onClickAppLock(View view){
		startActivity(new Intent(this,AppLockActivity.class));
	}
}
