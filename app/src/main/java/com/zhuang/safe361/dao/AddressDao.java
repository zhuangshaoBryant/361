package com.zhuang.safe361.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
	// 注意,该路径必须是data/data目录的文件,否则数据库访问不到
	private static final String PATH = "data/data/com.zhuang.safe361/files/address.db";

	public static String getAddress(String number) {
		String result = "未知号码";
		// 获取数据库对象
		SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);

		if (number.matches("^1[3-8]\\d{9}$")) {// 正则表达式，手机号码特点: 1 +
												// (3,4,5,6,7,8) + (9位数字)
			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { number.substring(0, 7) });
			if (cursor.moveToNext()) {
				result = cursor.getString(0);
			}
			cursor.close();
		} else if (number.matches("^\\d+$")) {// 匹配数字
			switch (number.length()) {
			case 3:
				result = "报警电话";
				break;
			case 4:
				result = "模拟器";
				break;
			case 5:
				result = "客服电话";
				break;
			case 7:
			case 8:
				result = "本地电话";
				break;

			default:
				// 01088881234
				// 048388888888,长途电话，第一位为0，但是数据库里第一位不为0，如010XXX,数据库里为10XXX
				if (number.startsWith("0") && number.length() > 10) {
					// 有些区号是4位,有些区号是3位(包括0)

					// 先查4位区号
					Cursor cursor = database.rawQuery(
							"select location from data2 where area =?",
							new String[] { number.substring(1, 4) });
					if (cursor.moveToNext()) {
						result = cursor.getString(0);
					} else {
						cursor.close();
						// 再查3位区号
						cursor = database.rawQuery(
								"select location from data2 where area =?",
								new String[] { number.substring(1, 3) });
						if (cursor.moveToNext()) {
							result = cursor.getString(0);
						}
					}
					cursor.close();
				}
				break;
			}
		}

		database.close();//关闭数据库
		return result;
	}
}
