package com.zhuang.safe361.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhuang.safe361.R;
import com.zhuang.safe361.bean.AppInfo;
import com.zhuang.safe361.dao.AppInfoProvider;
import com.zhuang.safe361.dao.AppLockDao;

import java.util.ArrayList;
import java.util.List;

public class AppLockActivity extends Activity {

	private Button bt_unlock,bt_lock;
	private LinearLayout ll_unlock,ll_lock;
	private TextView tv_unlock,tv_lock;
	private ListView lv_unlock,lv_lock;
	private List<AppInfo> mAppInfoList;
	private List<AppInfo> mUnLockList;
	private List<AppInfo> mLockList;

	private MyAdapter mLockAdapter;
	private MyAdapter mUnLockAdapter;

	private TranslateAnimation mTranslateAnimation;

	private Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			mLockAdapter = new MyAdapter(true);
			lv_lock.setAdapter(mLockAdapter);

			mUnLockAdapter = new MyAdapter(false);
			lv_unlock.setAdapter(mUnLockAdapter);
		}
	};
	private AppLockDao mDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		initUI();
		initData();
		initAnimation();
	}

	/**
	 * 初始化平移动画的方法(平移自身的一个宽度大小)
	 */
	private void initAnimation() {
		mTranslateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF,0,
				Animation.RELATIVE_TO_SELF,1,
				Animation.RELATIVE_TO_SELF,0,
				Animation.RELATIVE_TO_SELF,0);
		mTranslateAnimation.setDuration(500);
	}

	private void initData() {
		new Thread(){

			@Override
			public void run() {
				mAppInfoList = AppInfoProvider.getAppInfoList(getApplication());
				mLockList = new ArrayList<AppInfo>();
				mUnLockList = new ArrayList<AppInfo>();
				//3.获取数据库中已加锁应用包名的的结合
				mDao = AppLockDao.getInstance(getApplicationContext());
				List<String> lockPackageList = mDao.findAll();
				for (AppInfo appInfo : mAppInfoList) {
					if(lockPackageList.contains(appInfo.packageName)){
						mLockList.add(appInfo);
					}else{
						mUnLockList.add(appInfo);
					}
				}
				myHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	private void initUI() {
		bt_unlock = (Button) findViewById(R.id.bt_unlock);
		bt_lock = (Button) findViewById(R.id.bt_lock);

		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		ll_lock = (LinearLayout) findViewById(R.id.ll_lock);

		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		tv_lock = (TextView) findViewById(R.id.tv_lock);

		lv_unlock = (ListView) findViewById(R.id.lv_unlock);
		lv_lock = (ListView) findViewById(R.id.lv_lock);

		bt_lock.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//1.已加锁列表显示,未加锁列表隐藏
				ll_lock.setVisibility(View.VISIBLE);
				ll_unlock.setVisibility(View.GONE);
				//2.未加锁变成浅色图片,已加锁变成深色图片
				bt_unlock.setBackgroundResource(R.drawable.tab_left_default);
				bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);
			}
		});

		bt_unlock.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//1.已加锁列表隐藏,未加锁列表显示
				ll_lock.setVisibility(View.GONE);
				ll_unlock.setVisibility(View.VISIBLE);
				//2.未加锁变成深色图片,已加锁变成浅色图片
				bt_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
				bt_lock.setBackgroundResource(R.drawable.tab_right_default);
			}
		});
	}

	class MyAdapter extends BaseAdapter{
		private boolean isLock;

		/**
		 * @param isLock	用于区分已加锁和未加锁应用的标示	true已加锁数据适配器	false未加锁数据适配器
		 */
		public MyAdapter(boolean isLock){
			this.isLock = isLock;
		}
		@Override
		public int getCount() {
			if(isLock){
				tv_lock.setText("已加锁应用:"+mLockList.size());
				return mLockList.size();
			}else{
				tv_unlock.setText("未加锁应用:"+mUnLockList.size());
				return mUnLockList.size();
			}
		}

		@Override
		public AppInfo getItem(int position) {
			if(isLock){
				return mLockList.get(position);
			}else{
				return mUnLockList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView==null){
				convertView = View.inflate(getApplicationContext(),R.layout.listview_islock_item,null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_lock);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			final AppInfo appInfo = getItem(position);

			final View animationView = convertView;

			holder.iv_icon.setBackgroundDrawable(appInfo.icon);
			holder.tv_name.setText(appInfo.name);
			if(isLock){
				holder.iv_lock.setBackgroundResource(R.drawable.lock);
			}else{
				holder.iv_lock.setBackgroundResource(R.drawable.unlock);
			}

			holder.iv_lock.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//添加动画效果,动画默认是非阻塞的,所以执行动画的同时,动画以下的代码也会执行
					animationView.startAnimation(mTranslateAnimation);
					//对动画执行过程做事件监听,监听到动画执行完成后,再去移除集合中的数据,操作数据库,刷新界面
					mTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
							//动画开始的是调用方法
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							//动画执行结束后调用方法
							if(isLock){
								//已加锁------>未加锁过程
								mLockList.remove(appInfo);
								mUnLockList.add(appInfo);
								//数据库里删除
								mDao.delete(appInfo.packageName);
								//刷新数据适配器
								mLockAdapter.notifyDataSetChanged();
							}else{
								//未加锁------>已加锁过程
								mLockList.add(appInfo);
								mUnLockList.remove(appInfo);
								mDao.insert(appInfo.packageName);
								mUnLockAdapter.notifyDataSetChanged();
							}
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
							//动画重复时候调用方法
						}
					});
				}
			});

			return convertView;
		}
	}

	public class ViewHolder{
		ImageView iv_icon;
		TextView tv_name;
		ImageView iv_lock;
	}


}
