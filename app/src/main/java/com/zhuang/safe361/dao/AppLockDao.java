package com.zhuang.safe361.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuang on 2016/5/12.
 */
public class AppLockDao {
	private AppLockOpenHelper appLockOpenHelper;
	private Context context;

	private AppLockDao (Context context){
		this.context = context;
		appLockOpenHelper = new AppLockOpenHelper(context);
	}

	private static AppLockDao mAppLockDao = null;

	public static AppLockDao getInstance(Context context){
		if(mAppLockDao==null){
			mAppLockDao = new AppLockDao(context);
		}
		return mAppLockDao;
	}

	/**
	 * 插入方法
	 */
	public boolean insert(String packagename){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("packagename",packagename);
		long applock = db.insert("applock", null, contentValues);
		db.close();
		context.getContentResolver().notifyChange(Uri.parse("content://applock/change"),null);
		if(applock == -1){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 删除方法
	 */
	public boolean delete(String packagename){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		int applock = db.delete("applock", "packagename=?", new String[]{packagename});
		db.close();
		context.getContentResolver().notifyChange(Uri.parse("content://applock/change"),null);
		if(applock==0){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 查询所有
	 */
	public List<String> findAll(){
		SQLiteDatabase db = appLockOpenHelper.getReadableDatabase();
		Cursor cursor = db.query("applock",new String[]{"packagename"},null,null,null,null,null,null);
		List<String> lockPackageList = new ArrayList<>();
		while(cursor.moveToNext()){
			lockPackageList.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return lockPackageList;
	}
}
