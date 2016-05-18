package com.zhuang.safe361.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zhuang.safe361.R;
import com.zhuang.safe361.utils.StreamUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {

	private TextView tvVersion;//版本号
	private TextView tvProgress;//下载进度
	
	private String mVersionName;//版本名
	private int mVersionCode;//版本号
	private String mDesc;//版本描述
	private String mDownloadUrl;//下载地址
	
	private SharedPreferences mPreferences;
	private RelativeLayout rlRoot;//根布局
	
	protected static final int CODE_UPDATE_DIALOG = 0;
	protected static final int CODE_URL_ERROR = 1;
	protected static final int CODE_NET_ERROR = 2;
	protected static final int CODE_JSON_ERROR = 3;
	protected static final int CODE_ENTER_HOME = 4;// 进入主页面
	
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
				showUpdateDailog();
				break;
			case CODE_URL_ERROR:
				Toast.makeText(SplashActivity.this, "url错误", Toast.LENGTH_SHORT)
						.show();
				enterHome();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT)
						.show();
				enterHome();
				break;
			case CODE_JSON_ERROR:
				Toast.makeText(SplashActivity.this, "数据解析错误",
						Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case CODE_ENTER_HOME:
				enterHome();
				break;


			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tvVersion = (TextView) findViewById(R.id.tv_version);
		tvVersion.setText("版本名："+ getVersionName());
		
		tvProgress = (TextView) findViewById(R.id.tv_progress);//默认是隐藏的
		rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
		
		copyDB("address.db");// 拷贝归属地查询数据库
		copyDB("commonnum.db");// 拷贝常用号码查询数据库

		mPreferences = getSharedPreferences("config", MODE_PRIVATE);
		//判断是否需要更新
		boolean autoUpdate = mPreferences.getBoolean("auto_update", true);
		if(autoUpdate){
			checkVersion();
		}else {
			mHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);
		}
		//渐变的动画效果
		AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
		anim.setDuration(2000);
		rlRoot.startAnimation(anim);
		
	}
	
	/**
	 * 将归属地的数据库从assets里拷贝到data/data/com.zhuang.safe361/file/address.db
	 * @param dbName
	 */
	private void copyDB(String dbName) {
		File destFile = new File(getFilesDir(), dbName);//在绝对路径下新建文件
		if(destFile.exists()){
			System.out.println("数据库已经存在");
			return;
		}
		FileOutputStream outputStream = null;
		InputStream inputStream = null;
		
		try {
			 outputStream = new FileOutputStream(destFile);
			 inputStream = getAssets().open(dbName);
			 
			 int len =0;
			 byte[] buffer = new byte[1024];
			 while((len = inputStream.read(buffer))!= -1){
				 outputStream.write(buffer,0,len);
			 }
			 
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				outputStream.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * 显示升级对话框
	 */
	protected void showUpdateDailog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("最新版本：" + mVersionName);//对话框标题
		builder.setMessage(mDesc);//设置信息
		builder.setPositiveButton("立即更新", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("立即更新");
				downLoad();//下载文件
			}
		});
		
		builder.setNegativeButton("稍后更新", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("稍后更新");
				enterHome();//进入主页
			}
		});
		//设置取消监听，点击返回键时触发
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();//进入主页
			}
		});
		
		builder.show();
	}

	/**
	 * 进入主页
	 */
	protected void enterHome() {
		Intent intent = new Intent(this,HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 下载apk文件
	 */
	protected void downLoad() {
		//判断是否有SD卡
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			tvProgress.setVisibility(View.VISIBLE);//显示进度
			
			String target = Environment.getExternalStorageDirectory() + "/update.apk";
			
			//使用xUtils工具
			HttpUtils utils = new HttpUtils();
			utils.download(mDownloadUrl, target, new RequestCallBack<File>() {
				//下载文件的进度
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					super.onLoading(total, current, isUploading);
					tvProgress.setText("下载进度：" + current * 100 / total + "%");
				}
				//下载成功时调用
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					//以下是调用系统的安装activity
//					<activity android:name=".PackageInstallerActivity"
//			                android:configChanges="orientation|keyboardHidden"
//			                android:theme="@style/Theme.Transparent">
//			            <intent-filter>
//			                <action android:name="android.intent.action.VIEW" />
//			                <category android:name="android.intent.category.DEFAULT" />
//			                <data android:scheme="content" />
//			                <data android:scheme="file" />
//			                <data android:mimeType="application/vnd.android.package-archive" />
//			            </intent-filter>
//			        </activity>
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setDataAndType(Uri.fromFile(arg0.result), "application/vnd.android.package-archive");
					
					startActivityForResult(intent, 0);//用户取消安装的话，会返回结果，回调onActivityResult方法
				}
				//下载失败
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
				}
			});
		}else {
			Toast.makeText(SplashActivity.this, "没有找到SD卡!", Toast.LENGTH_SHORT).show();
		}
	}
	
	//如果用户取消安装的话,startActivityForResult回调此方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 获取版本名
	 */
	public String getVersionName(){
		PackageManager packageManager = getPackageManager();
		try {
			//获取包的信息
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			int versionCode = packageInfo.versionCode;
			String versionName = packageInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取本地app版本号
	 */
	public int getVersionCode(){
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			int versionCode = packageInfo.versionCode;
			return versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 从服务器获取版本信息进行校验
	 */
	private void checkVersion(){
		final long startTime = System.currentTimeMillis();
		
		//启动子线程异步加载数据
		new Thread(){
			@Override
			public void run() {
				Message msg = Message.obtain();
				HttpURLConnection conn = null;
				try {
					URL url = new URL("http://10.0.2.2:8080/update.json");
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");// 设置请求方法
					conn.setConnectTimeout(5000);//设置连接超时，5秒
					conn.setReadTimeout(5000);//设置响应超时，连接上了,但服务器迟迟不给响应
					conn.connect();//连接服务器
					
					int responseCode = conn.getResponseCode();//获取响应码
					if(responseCode == 200){
						InputStream inputStream = conn.getInputStream();
						String result = StreamUtils.readFromStream(inputStream);//从网络中获取字符串
						System.out.println("网络返回:" + result);
						//解析JSON
						JSONObject jo = new JSONObject(result);
						mVersionCode = jo.getInt("versionCode");
						mVersionName = jo.getString("versionName");
						mDesc = jo.getString("description");
						mDownloadUrl = jo.getString("downloadUrl");
						
						if(mVersionCode > getVersionCode()){
							//服务器的VersionCode大于本地的VersionCode
							// 说明有更新, 弹出升级对话框
							msg.what = CODE_UPDATE_DIALOG;
						}else{
							// 没有版本更新
							msg.what = CODE_ENTER_HOME;
						}
					}
					
				} catch (MalformedURLException e) {
					//url网络错误
					msg.what = CODE_URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					// 网络错误
					msg.what = CODE_NET_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					// json解析错误
					msg.what = CODE_JSON_ERROR;
					e.printStackTrace();
				}finally{
					long endTime = System.currentTimeMillis();
					long timeUsed = endTime - startTime;
					if(timeUsed < 2000){
						// 强制休眠一段时间,保证闪屏页展示2秒钟
						try {
							Thread.sleep(2000 - timeUsed);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
			        mHandler.sendMessage(msg);
					if(conn!=null){
						conn.disconnect();//关闭网络连接
					}
				}
			}
		}.start();
	} 
	
	
	

}











