package com.zhuang.safe361.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuang.safe361.R;
import com.zhuang.safe361.bean.BlackNumberInfo;
import com.zhuang.safe361.dao.BlackNumberDao;

import java.util.List;

public class BlackNumberActivity extends Activity {

    private ListView lv_blacknumber;
	private int mode = 1;
	private BlackNumberDao mDao;
	private Button bt_add;
	private List<BlackNumberInfo> mBlackNumberList;
	private int mCount;
	private MyAdapter mAdapter;
	private boolean mIsLoad = false;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(mAdapter == null){
				mAdapter = new MyAdapter();
				lv_blacknumber.setAdapter(mAdapter);
			}else{
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mBlackNumberList.size();
		}

		@Override
		public Object getItem(int position) {
			return mBlackNumberList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = View.inflate(getApplicationContext(),R.layout.listview_blacknumber_item,null);
				holder = new ViewHolder();
				holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
				holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}

			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//数据库和集合删除数据
					mDao.delete(mBlackNumberList.get(position).getNumber());
					mBlackNumberList.remove(position);
					//通知数据适配器更新
					if(mAdapter != null){
						mAdapter.notifyDataSetChanged();
					}
				}
			});

			holder.tv_phone.setText(mBlackNumberList.get(position).getNumber());
			String mode = mBlackNumberList.get(position).getMode();
			switch (mode){
				case "1":
					holder.tv_mode.setText("拦截短信");
					break;
				case "2":
					holder.tv_mode.setText("拦截电话");
					break;
				case "3":
					holder.tv_mode.setText("拦截所有");
					break;
			}
			return convertView;
		}
	}

	static class ViewHolder{
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blacknumber);
		initUI();
	    initData();
    }

	private void initData() {
		//获取数据库中所有电话号码
		new Thread(){
			@Override
			public void run() {
				//1,获取操作黑名单数据库的对象
				mDao = BlackNumberDao.getInstance(getApplicationContext());
				mBlackNumberList = mDao.find(0);//开始加载20条数据
				mCount = mDao.getCount();
				////3,通过消息机制告知主线程可以去使用包含数据的集合
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}


	/**
     * 初始化UI
     */
    private void initUI() {
	    lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
	    bt_add = (Button) findViewById(R.id.bt_add);

	    bt_add.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    showDialog();
		    }
	    });

	    lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
		    //滚动过程中,状态发生改变调用方法()
		     @Override
		    public void onScrollStateChanged(AbsListView view, int scrollState) {
//				OnScrollListener.SCROLL_STATE_FLING	飞速滚动
//				OnScrollListener.SCROLL_STATE_IDLE	 空闲状态
//				OnScrollListener.SCROLL_STATE_TOUCH_SCROLL	拿手触摸着去滚动状态
			     if(mBlackNumberList != null){
				     /*此处做了一个优化，每次让ListView加载20页，在数据库里查询语句加一条，limit（？，20），
					 * 每次只加载20条数目，于是再判断
					 * 条件一:滚动到停止状态
					 * 条件二:最后一个条目可见(最后一个条目的索引值>=数据适配器中集合的总条目个数-1)
					 */
					/*mIsLoad防止重复加载的变量
					如果当前正在加载mIsLoad就会为true,本次加载完毕后,再将mIsLoad改为false
					如果下一次加载需要去做执行的时候,会判断上诉mIsLoad变量,是否为false,如果为true,就需要等待上一次加载完成,将其值
					改为false后再去加载*/
				     if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
						     lv_blacknumber.getLastVisiblePosition() >= mBlackNumberList.size()-1 && !mIsLoad){
					     //如果短信总条目个数大于集合大小的时,才可以去继续加载更多
					     if(mCount>mBlackNumberList.size()){
						     //加载下一页数据
						     new Thread(){
							     @Override
							     public void run() {
								     mDao = BlackNumberDao.getInstance(getApplication());
								     //从集合大小的个数开始，再次加载20条
								     List<BlackNumberInfo> moreData = mDao.find(mBlackNumberList.size());
								     mBlackNumberList.addAll(moreData);
								     mHandler.sendEmptyMessage(0);
							     }
						     }.start();
					     }
				     }
			     }
		    }

		    //滚动过程中调用方法
		    @Override
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		    }
	    });
    }

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		final AlertDialog dialog = builder.create();
		View view = View.inflate(getApplicationContext(),R.layout.dialog_add_blacknumber,null);
		dialog.setView(view,0,0,0,0);

		final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rb_group);

		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancle = (Button) view.findViewById(R.id.btn_cancel);

		//监听其选中条目的切换过程
		rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId){
					case R.id.rb_sms:
						//拦截短信
						mode = 1;
						break;
					case R.id.rb_phone:
						//拦截电话
						mode = 2;
						break;
					case R.id.rb_all:
						//拦截所有
						mode = 3;
						break;
				}
			}
		});

		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString().trim();
				if(!TextUtils.isEmpty(phone)){
					//数据库插入当前输入的拦截电话号码，集合也同步
					mDao.add(phone,mode+"");
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
					blackNumberInfo.setNumber(phone);
					blackNumberInfo.setMode(mode+"");
					//将对象插入到集合的最顶部
					mBlackNumberList.add(0,blackNumberInfo);
					//通知数据适配器刷新(数据适配器中的数据有改变了)
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}

					dialog.dismiss();
				}else{
					Toast.makeText(getApplicationContext(),"请输入拦截号码",Toast.LENGTH_SHORT).show();
				}
			}
		});

		bt_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}


}
