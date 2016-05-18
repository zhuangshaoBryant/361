package com.zhuang.safe361.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhuang.safe361.R;

public class DragViewActivity extends Activity {

	private TextView tvTopTextView;
	private TextView tvBottomTextView;
	private ImageView ivDragImageView;
	private SharedPreferences mPreferences;
	private int top;// 状态栏高度
	private int startX;
	private int startY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_view);

		mPreferences = getSharedPreferences("config", MODE_PRIVATE);

		tvTopTextView = (TextView) findViewById(R.id.tv_top);
		tvBottomTextView = (TextView) findViewById(R.id.tv_bottom);
		ivDragImageView = (ImageView) findViewById(R.id.iv_drag);

		final int winWidth = getWindowManager().getDefaultDisplay().getWidth();// 屏幕宽度和高度
		final int winHeight = getWindowManager().getDefaultDisplay()
				.getHeight();

		int lastX = mPreferences.getInt("lastX", 20);
		int lastY = mPreferences.getInt("lastY", 200);

		// 根据图片位置,决定提示框显示和隐藏
		if (lastY > winHeight / 2) {
			tvTopTextView.setVisibility(View.VISIBLE);
			tvBottomTextView.setVisibility(View.INVISIBLE);
		} else {
			tvTopTextView.setVisibility(View.INVISIBLE);
			tvBottomTextView.setVisibility(View.VISIBLE);
		}

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivDragImageView
				.getLayoutParams();// 获取布局对象
		params.leftMargin = lastX;// 设置左边距
		params.topMargin = lastY;// 设置top边距
		ivDragImageView.setLayoutParams(params);

		ivDragImageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 初始化起点坐标，getX是触摸点相对本身View的距离，此处应该是相对屏幕距离，因此是getRawX
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					// 计算移动偏移量
					int dx = endX - startX;
					int dy = endY - startY;

					// 更新左上右下距离,getLeft是矩形左边到父布局左边的距离，因此此处是以矩形为相对位置参考
					int l = ivDragImageView.getLeft() + dx;
					int t = ivDragImageView.getTop() + dy;
					int r = ivDragImageView.getRight() + dx;
					int b = ivDragImageView.getBottom() + dy;

					// 判断是否超出屏幕边界, 注意状态栏的高度
					if (l < 0 || r > winWidth || t < 0 || b > winHeight - top) {
						break;
					}

					// 根据图片位置,决定提示框显示和隐藏
					if (t > winHeight / 2) {
						tvTopTextView.setVisibility(View.VISIBLE);
						tvBottomTextView.setVisibility(View.INVISIBLE);
					} else {
						tvTopTextView.setVisibility(View.INVISIBLE);
						tvBottomTextView.setVisibility(View.VISIBLE);
					}

					// 更新界面
					ivDragImageView.layout(l, t, r, b);

					// 重新初始化起点坐标
					startX = endX;
					startY = endY;

					break;
				case MotionEvent.ACTION_UP:
					Editor editor = mPreferences.edit();
					editor.putInt("lastX", ivDragImageView.getLeft());
					editor.putInt("lastY", ivDragImageView.getTop());
					editor.commit();
					break;

				default:
					break;
				}
				return false;
			}
		});

		// 设置双击居中
		final long[] m = new long[2];
		ivDragImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.arraycopy(m, 1, m, 0, m.length - 1);
				m[m.length - 1] = SystemClock.uptimeMillis();
				if ((SystemClock.uptimeMillis() - m[0]) < 500) {
					// 把图片居中
					ivDragImageView.layout(
							winWidth / 2 - ivDragImageView.getWidth() / 2, ivDragImageView.getTop(),
							winWidth / 2 + ivDragImageView.getWidth() / 2, ivDragImageView.getBottom());
				}
			}
		});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			// //1、计算屏幕总高度宽度
			// int winWidth = getWindowManager().getDefaultDisplay().getWidth();
			// int winHeight =
			// getWindowManager().getDefaultDisplay().getHeight();

			// 2、计算应用区域高度宽度
			Rect outRect = new Rect();
			this.getWindow().getDecorView()
					.getWindowVisibleDisplayFrame(outRect);
			top = outRect.top;// 状态栏高度
			// int winWidth2 = outRect.width();
			// int winHeight2 = outRect.height();
			//
			// //3、计算内容区域高度宽度
			// Rect outRect2 = new Rect();
			// this.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect2);
			// int winWidth3 = outRect.width();
			// int winHeight3 = outRect.height();

		}
	}

}
