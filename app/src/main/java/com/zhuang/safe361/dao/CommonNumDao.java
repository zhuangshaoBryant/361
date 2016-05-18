package com.zhuang.safe361.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuang on 2016/5/10.
 */
public class CommonNumDao {
	//1,指定访问数据库的路径
	public String path = "data/data/com.zhuang.safe361/files/commonnum.db";

	//开启数组
	public List<Group> getGroup(){
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.query("classlist",new String[]{"name","idx"},null,null,null,null,null);
		List<Group> groupList = new ArrayList<>();
		while(cursor.moveToNext()){
			Group group = new Group();
			group.name = cursor.getString(0);
			group.idx = cursor.getString(1);
			group.childList = getChild(group.idx);
			groupList.add(group);
		}
		cursor.close();
		db.close();
		return groupList;
	}

	//获取每一个组中孩子节点的数据
	public List<Child> getChild(String idx){
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from table" + idx+";",null);
		List<Child> childList = new ArrayList<>();
		while(cursor.moveToNext()){
			Child child = new Child();
			child._id = cursor.getString(0);
			child.number = cursor.getString(1);
			child.name = cursor.getString(2);
			childList.add(child);
		}
		cursor.close();
		db.close();
		return childList;
	}


	public class Group{
		public String name;
		public String idx;
		public List<Child> childList;
	}

	public class Child{
		public String _id;
		public String number;
		public String name;
	}
}

