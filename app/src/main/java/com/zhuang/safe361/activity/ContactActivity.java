package com.zhuang.safe361.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.zhuang.safe361.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactActivity extends Activity {

	private ListView lvListView;
	private ArrayList<HashMap<String, String>> readContactList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		lvListView = (ListView) findViewById(R.id.lv_list);

		readContactList = readContact();
		lvListView.setAdapter(new SimpleAdapter(this, readContactList,
				R.layout.contact_list_item, new String[] { "name", "phone" },
				new int[] { R.id.tv_name, R.id.tv_phone }));

		lvListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = readContactList.get(position).get("phone");
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	}

	public ArrayList<HashMap<String, String>> readContact() {
		//1、获取两个表的Uri
		Uri rawContactUri = Uri
				.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		// 2、从raw_contacts中读取联系人的id("contact_id")
		Cursor rawContactCursor = getContentResolver().query(rawContactUri,
				new String[] { "contact_id" }, null, null, null);
		if (rawContactCursor != null) {
			while (rawContactCursor.moveToNext()) {
				//得到联系人id
				String contactIdString = rawContactCursor.getString(0);
				
				//3、根据contact_id从data表中查询出相应的电话号码和联系人名称, 实际上查询的是视图view_data
				Cursor dataCursor = getContentResolver().query(dataUri,
						new String[] { "data1", "mimetype" }, "contact_id=?",
						new String[] { contactIdString }, null);
				if (dataCursor != null) {
					HashMap<String, String> map = new HashMap<String, String>();
					while (dataCursor.moveToNext()) {
						String data1 = dataCursor.getString(0);
						String mimetype = dataCursor.getString(1);
						//判断是什么类型的数据
						if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
							map.put("phone", data1);
						} else if ("vnd.android.cursor.item/name"
								.equals(mimetype)) {
							map.put("name", data1);
						}
					}

					list.add(map);
				}

				dataCursor.close();
			}
		}

		rawContactCursor.close();
		return list;

	}
}
