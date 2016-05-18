package com.zhuang.safe361.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuang.safe361.R;
import com.zhuang.safe361.bean.AppInfo;
import com.zhuang.safe361.dao.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;


public class AppManagerActivity extends Activity implements View.OnClickListener {

	private ListView lv_app_list;
	private TextView tv_des;
	private List<AppInfo> mAppInfoList;
	private List<AppInfo> mSystemList;
	private List<AppInfo> mCustomerList;
	private MyAdapter myAdapter;
	private AppInfo mAppInfo;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			myAdapter = new MyAdapter();
			lv_app_list.setAdapter(myAdapter);

			if (tv_des != null && mCustomerList != null) {
				tv_des.setText("用户应用(" + mCustomerList.size() + ")");
			}
		}
	};
	private PopupWindow mPopupWindow;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		initTitle();
		initList();
	}

	@Override
	protected void onResume() {
		//重新获取数据
		getData();
		super.onResume();
	}

	private void getData() {
		new Thread() {
			@Override
			public void run() {
				mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
				mSystemList = new ArrayList<AppInfo>();
				mCustomerList = new ArrayList<AppInfo>();

				for (AppInfo appInfo : mAppInfoList) {
					if (appInfo.isSystem) {
						//系统应用
						mSystemList.add(appInfo);
					} else {
						//用户应用
						mCustomerList.add(appInfo);
					}
				}
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	private void initList() {
		lv_app_list = (ListView) findViewById(R.id.lv_app_list);
		tv_des = (TextView) findViewById(R.id.tv_des);

		new Thread() {
			@Override
			public void run() {
				mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
				mSystemList = new ArrayList<AppInfo>();
				mCustomerList = new ArrayList<AppInfo>();

				for (AppInfo appInfo : mAppInfoList) {
					if (appInfo.isSystem) {
						//系统应用
						mSystemList.add(appInfo);
					} else {
						//用户应用
						mCustomerList.add(appInfo);
					}
				}
				mHandler.sendEmptyMessage(0);
			}
		}.start();

		//灰色条目的变化
		lv_app_list.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			//滚动过程中调用方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//AbsListView中view就是listView对象
				//firstVisibleItem第一个可见条目索引值
				//visibleItemCount当前一个屏幕的可见条目数
				//总共条目总数
				if (mCustomerList != null && mSystemList != null) {
					if (firstVisibleItem < mCustomerList.size() + 1) {
						tv_des.setText("用户应用(" + mCustomerList.size() + ")");
					} else {
						tv_des.setText("系统应用(" + mSystemList.size() + ")");
					}
				}
			}
		});

		//点击启动卸载分享的功能
		lv_app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 || position == mCustomerList.size() + 1) {
					return;
				} else {
					if (position < mCustomerList.size() + 1) {
						mAppInfo = mCustomerList.get(position - 1);
					} else {
						mAppInfo = mSystemList.get(position - mCustomerList.size() - 2);
					}
					showPopupWindow(view);
				}
			}
		});
	}

	private void showPopupWindow(View view) {
		View popupView = View.inflate(this, R.layout.popupwindow_layout, null);

		TextView tv_uninstall = (TextView) popupView.findViewById(R.id.tv_uninstall);
		TextView tv_start = (TextView) popupView.findViewById(R.id.tv_start);
		TextView tv_share = (TextView) popupView.findViewById(R.id.tv_share);

		tv_uninstall.setOnClickListener(this);
		tv_start.setOnClickListener(this);
		tv_share.setOnClickListener(this);

		//透明动画
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(500);
		alphaAnimation.setFillAfter(true);

		//缩放动画
		ScaleAnimation scaleAnimation = new ScaleAnimation(
				0, 1,
				0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(500);
		scaleAnimation.setFillAfter(true);

		//动画集合
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);

		//1,创建窗体对象,指定宽高
		mPopupWindow = new PopupWindow(popupView,
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT,
				true);
		//2,设置一个透明背景(new ColorDrawable()),如果不设置背景按返回键就不能回退
		mPopupWindow.setBackgroundDrawable(new ColorDrawable());
		//3,指定窗体位置
		mPopupWindow.showAsDropDown(view, 100, -view.getHeight() - 30);
		//4,popupView执行动画
		popupView.startAnimation(animationSet);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_uninstall:
				if (mAppInfo.isSystem) {
					Toast.makeText(getApplicationContext(), "系统应用不能卸载", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent("android.intent.action.DELETE");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setData(Uri.parse("package:" + mAppInfo.getPackageName()));
					startActivity(intent);
				}
				break;
			case R.id.tv_start:
				//通过桌面去启动指定包名应用
				PackageManager packageManager = getPackageManager();
				//通过Launch开启制定包名的意图,去开启应用
				Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(mAppInfo.getPackageName());
				if (launchIntentForPackage != null) {
					startActivity(launchIntentForPackage);
				} else {
					Toast.makeText(getApplicationContext(), "此应用不能开启", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.tv_share:
				showShare();
				break;
		}

		//点击了窗体后消失窗体
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}

	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize();

 // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		//oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.ssdk_oks_share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("我分享的应用http://www.baidu.com");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		//oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
		oks.show(this);
	}

	/**
	 * 获取磁盘可用大小
	 */
	private void initTitle() {
		TextView tv_memory = (TextView) findViewById(R.id.tv_memory);
		TextView tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);

		//1,获取磁盘(ROM,区分于手机运行内存)可用大小,磁盘路径
		String path = Environment.getDataDirectory().getAbsolutePath();
		//2,获取sd卡可用大小,sd卡路径
		String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		//3,获取以上两个路径下文件夹的可用大小
		String memoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(path));
		String sdMemoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(sdPath));

		tv_memory.setText("磁盘可用：" + memoryAvailSpace);
		tv_sd_memory.setText("sd卡可用：" + sdMemoryAvailSpace);
	}

	/**
	 * 返回值结果单位为byte = 8bit,最大结果为2147483647 bytes
	 *
	 * @param path
	 * @return 返回指定路径可用区域的byte类型值
	 */
	public long getAvailSpace(String path) {
		//获取可用磁盘大小类
		StatFs statFs = new StatFs(path);
		//获取可用区块个数
		long count = statFs.getAvailableBlocks();
		//获取区块的大小，如windows默认区块大小为4kb
		long size = statFs.getBlockSize();
		//区块大小*可用区块个数 == 可用空间大小
		return count * size;
	}


	class MyAdapter extends BaseAdapter {

		//获取数据适配器中条目类型的总数,修改成两种(纯文本,图片+文字)
		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}

		//指定索引指向的条目类型,返回0,代表纯文本条目的状态码,返回1,代表图片+文本条目状态码
		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == mCustomerList.size() + 1) {
				return 0;
			} else {
				return 1;
			}
		}

		//listView中添加两个描述条目,还有两个文字部分
		@Override
		public int getCount() {
			return mCustomerList.size() + mSystemList.size() + 2;
		}

		@Override
		public AppInfo getItem(int position) {
			if (position == 0 || position == mCustomerList.size() + 1) {
				return null;
			} else if (position < mCustomerList.size() + 1) {
				return mCustomerList.get(position - 1);
			} else {
				//返回系统应用对应条目的对象
				return mSystemList.get(position - mCustomerList.size() - 2);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);

			if (type == 0) {
				//展示灰色纯文本条目
				ViewTitleHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(), R.layout.listview_app_title, null);
					holder = new ViewTitleHolder();
					holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
					convertView.setTag(holder);
				} else {
					holder = (ViewTitleHolder) convertView.getTag();
				}
				if (position == 0) {
					holder.tv_title.setText("用户应用(" + mCustomerList.size() + ")");
				} else {
					holder.tv_title.setText("系统应用(" + mSystemList.size() + ")");
				}
				return convertView;
			} else {
				//展示图片+文字
				ViewHolder holder = null;
				if (convertView == null) {
					holder = new ViewHolder();
					convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item, null);
					holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
					holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
					holder.tv_path = (TextView) convertView.findViewById(R.id.tv_path);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.iv_icon.setBackgroundDrawable(getItem(position).getIcon());
				holder.tv_name.setText(getItem(position).getName());
				if (getItem(position).isSdCard) {
					holder.tv_path.setText("sd卡应用");
				} else {
					holder.tv_path.setText("手机应用");
				}
				return convertView;
			}
		}
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_path;
	}

	static class ViewTitleHolder {
		TextView tv_title;
	}
}
