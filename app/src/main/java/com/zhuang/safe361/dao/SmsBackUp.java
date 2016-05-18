package com.zhuang.safe361.dao;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zhuang on 2016/5/4.
 */
public class SmsBackUp {

	private static int index = 0;

	/**
	 * @param context  对象上下文环境
	 * @param path     备份文件夹路径
	 * @param callBack 进度条所在的对话框对象用于备份过程中进度的更新
	 */
	public static void backup(Context context, String path, CallBack callBack) {
		Cursor cursor = null;
		FileOutputStream fileOutputStream = null;
		try {
			//1,获取备份短信写入的文件
			File file = new File(path);
			//2,获取内容解析器,获取短信数据库中数据
			cursor = context.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address", "date", "type", "body"}, null, null, null);
			//3、文件输出流
			fileOutputStream = new FileOutputStream(file);
			//4,序列化数据库中读取的数据,放置到xml中
			XmlSerializer xmlSerializer = Xml.newSerializer();
			xmlSerializer.setOutput(fileOutputStream, "utf-8");
			xmlSerializer.startDocument("utf-8", true);

			xmlSerializer.startTag(null, "smss");

			//5,备份短信总数指定
			//A 如果传递进来的是对话框,指定对话框进度条的总数
			//B	如果传递进来的是进度条,指定进度条的总数
			if (callBack != null) {
				callBack.setMax(cursor.getCount());
			}

			//6,读取数据库中的每一行的数据写入到xml中
			while (cursor.moveToNext()) {
				xmlSerializer.startTag(null, "sms");

				xmlSerializer.startTag(null, "address");
				xmlSerializer.text(cursor.getString(0));
				xmlSerializer.endTag(null, "address");

				xmlSerializer.startTag(null, "date");
				xmlSerializer.text(cursor.getString(1));
				xmlSerializer.endTag(null, "date");

				xmlSerializer.startTag(null, "type");
				xmlSerializer.text(cursor.getString(2));
				xmlSerializer.endTag(null, "type");

				xmlSerializer.startTag(null, "body");
				xmlSerializer.text(cursor.getString(3));
				xmlSerializer.endTag(null, "body");

				xmlSerializer.endTag(null, "sms");

				//7,每循环一次就需要去让进度条叠加
				index++;
				Thread.sleep(500);

				//ProgressDialog可以在子线程中更新相应的进度条的改变
				if (callBack != null) {
					callBack.setProgress(index);
				}
			}

			xmlSerializer.endTag(null, "smss");
			xmlSerializer.endDocument();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (cursor != null && fileOutputStream != null) {
					cursor.close();
					fileOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	//回调
//1.定义一个接口
//2,定义接口中未实现的业务逻辑方法(短信总数设置,备份过程中短信百分比更新)
//3.传递一个实现了此接口的类的对象(至备份短信的工具类中),接口的实现类,一定实现了上诉两个为实现方法(就决定了使用对话框,还是进度条)
//4.获取传递进来的对象,在合适的地方(设置总数,设置百分比的地方)做方法的调用

	/**
	 * 此类是观察者模式，可以决定用对话框还是进度条，相当于一个代理
	 */
	public interface CallBack {

		public void setMax(int max);

		public void setProgress(int index);
	}


}


