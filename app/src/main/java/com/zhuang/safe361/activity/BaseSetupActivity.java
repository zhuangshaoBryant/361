package com.zhuang.safe361.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * 设置引导页的基类, 不需要在清单文件中注册, 因为不需要界面展示 功能，SetupActivity1234里都要用到的功能
 * 
 * @author zhuang
 * 
 */
public abstract class BaseSetupActivity extends Activity {

	private GestureDetector mGestureDetector;
	public SharedPreferences mPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPreferences = getSharedPreferences("config", MODE_PRIVATE);
		mGestureDetector = new GestureDetector(this,
				new SimpleOnGestureListener() {

					/**
					 * 监听手势滑动事件 e1表示滑动的起点,e2表示滑动终点 velocityX表示水平速度
					 * velocityY表示垂直速度
					 */
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {

						// 判断纵向滑动幅度是否过大, 过大的话不允许切换界面
						if (Math.abs(e1.getRawY() - e2.getRawY()) > 100) {
							return false;
						}

						// 判断滑动是否过慢
						if (Math.abs(velocityX) < 50) {
							Toast.makeText(BaseSetupActivity.this, "滑的太慢了",
									Toast.LENGTH_SHORT).show();
							return true;
						}

						//向左滑
						if (e1.getRawX() - e2.getRawX() > 100) {
							
							showNextPage();
							return true;
						}
						
						//向右滑
						if (e2.getRawX() - e1.getRawX() > 100) {
							showPreviousPage();
							return true;
						}

						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});
	}

	/**
	 * 展示下一页, 子类必须实现，此处是滑动
	 */
	public abstract void showNextPage();

	/**
	 * 展示上一页, 子类必须实现
	 */
	public abstract void showPreviousPage();

	// 点击下一页，此处是按钮点击
	public void next(View view) {
		showNextPage();
	}

	// 点击上一页
	public void previous(View view) {
		showPreviousPage();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);// 委托手势识别器处理触摸事件
		return super.onTouchEvent(event);
	}
}
