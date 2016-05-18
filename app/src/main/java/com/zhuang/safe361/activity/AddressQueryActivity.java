package com.zhuang.safe361.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zhuang.safe361.R;
import com.zhuang.safe361.dao.AddressDao;

public class AddressQueryActivity extends Activity {

	private EditText numberEditText;//归属地号码
	private TextView resultTextView;//查询结果
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_query);
		
		numberEditText = (EditText) findViewById(R.id.et_number);
		resultTextView = (TextView) findViewById(R.id.tv_result);
		
		// 监听EditText的变化
		numberEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String result = AddressDao.getAddress(s.toString());
				resultTextView.setText(result);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	public void queryOnClick(View view){
		
		String number = numberEditText.getText().toString().trim();
		if(!TextUtils.isEmpty(number)){
			String result = AddressDao.getAddress(number);
			resultTextView.setText(result);
		}else {
			vibrate();
		}
		
	}
	
	/**
	 * 添加震动的功能
	 */
	public void vibrate(){
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(2000);//震动两秒
		vibrator.vibrate(new long[]{1001,2000,1000,3000}, -1);
		// 先等待1秒,再震动2秒,再等待1秒,再震动3秒,
		// 参2等于-1表示只执行一次,不循环,
		// 参2等于0表示从头循环,
		// 参2表示从第几个位置开始循环
	}


}
