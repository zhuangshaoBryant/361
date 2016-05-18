package com.zhuang.safe361.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhuang.safe361.bean.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuang on 2016/4/26.
 */
public class BlackNumberDao {
	public BlackNumberOpenHelper helper;

	private BlackNumberDao(Context context) {
		helper = new BlackNumberOpenHelper(context);
	}

	private static BlackNumberDao blackNumberDao = null;

	public static BlackNumberDao getInstance(Context context) {
		if (blackNumberDao == null) {
			blackNumberDao = new BlackNumberDao(context);
		}
		return blackNumberDao;
	}

	/**
	 * @param number 黑名单号码
	 * @param mode   拦截模式
	 */
	public boolean add(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("number", number);
		contentValues.put("mode", mode);
		//rowId返回插入后的新行，如果是-1，表示失败
		long rowId = db.insert("blacknumber", null, contentValues);
		db.close();
		if (rowId == -1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 通过电话号码删除
	 *
	 * @param number 电话号码
	 */
	public boolean delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		//rowNumber表示改变了多少行,rowNumber=0表示删除失败
		int rowNumber = db.delete("blacknumber", "number=?", new String[]{number});
		db.close();
		if (rowNumber == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 通过电话号码修改拦截的模式
	 *
	 * @param number
	 */
	public boolean update(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("mode", mode);
		//rowNumber表示改变了多少行,rowNumber=0表示删除失败
		int rowNumber = db.update("blacknumber", contentValues, "number=?", new String[]{number});
		db.close();
		if (rowNumber == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 返回一个黑名单号码拦截模式
	 *
	 * @return
	 */
	public int getMode(String number) {
		int mode = 0;
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number=?", new String[]{number}, null, null, null);
		if (cursor.moveToNext()) {
			mode = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return mode;
	}

	/**
	 * 查询所有的黑名单
	 *
	 * @return
	 */
	public List<BlackNumberInfo> findAll() {
		SQLiteDatabase db = helper.getWritableDatabase();
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		Cursor cursor = db.query("blacknumber", new String[]{"number", "mode"}, null, null, null, null, "_id desc");
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setNumber(cursor.getString(0));
			blackNumberInfo.setMode(cursor.getString(1));
			list.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return list;
	}

	/**
	 * @return 数据库中数据的总条目个数, 返回0代表没有数据或异常
	 */
	public int getCount() {
		SQLiteDatabase db = helper.getWritableDatabase();
		int count = 0;
		Cursor cursor = db.rawQuery("select count(*) from blacknumber;", null);
		if (cursor.moveToNext()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}

	/**
	 * 每次查询20条数据
	 *
	 * @param index 查询的索引值
	 */
	public List<BlackNumberInfo> find (int index) {
		SQLiteDatabase db = helper.getWritableDatabase();
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		Cursor cursor = db.rawQuery(
				"select number,mode from blacknumber order by _id desc limit ?,20;",
				new String[]{index + ""});
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setNumber(cursor.getString(0));
			blackNumberInfo.setMode(cursor.getString(1));
			list.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return list;
	}
}
