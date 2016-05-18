package com.zhuang.safe361.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.zhuang.safe361.R;
import com.zhuang.safe361.dao.CommonNumDao;

import java.util.List;

public class CommonNumberQueryActivity extends Activity {

	private ExpandableListView elv_common_number;
	private List<CommonNumDao.Group> mGroup;
	private MyAdapter myAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number_query);
		initUI();
		initData();
	}

	/**
	 * 给可扩展ListView准备数据,并且填充
	 */
	private void initData() {
		CommonNumDao commonNumDao = new CommonNumDao();
		mGroup = commonNumDao.getGroup();
		myAdapter = new MyAdapter();
		elv_common_number.setAdapter(myAdapter);

		elv_common_number.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				startCall(myAdapter.getChild(groupPosition,childPosition).number);
				return false;
			}
		});
	}

	public void startCall(String number){
		//开启系统的打电话界面
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:"+number));
		startActivity(intent);
	}

	private void initUI() {
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
	}


	class MyAdapter extends BaseExpandableListAdapter{

		@Override
		public int getGroupCount() {
			return mGroup.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mGroup.get(groupPosition).childList.size();
		}

		@Override
		public CommonNumDao.Group getGroup(int groupPosition) {
			return mGroup.get(groupPosition);
		}

		@Override
		public CommonNumDao.Child getChild(int groupPosition, int childPosition) {
			return mGroup.get(groupPosition).childList.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setText("        "+getGroup(groupPosition).name);
			textView.setTextColor(Color.parseColor("#FFFF4174"));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
			textView.setPadding(10,10,10,10);
			return textView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),R.layout.elv_child_item,null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_number = (TextView) view.findViewById(R.id.tv_number);

			tv_name.setText(getChild(groupPosition,childPosition).name);
			tv_number.setText(getChild(groupPosition,childPosition).number);

			return view;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			//设置为true，才可以点击child
			return true;
		}
	}
}
