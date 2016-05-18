package com.zhuang.safe361.dao;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.zhuang.safe361.R;
import com.zhuang.safe361.bean.ProcessInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 进程管理dao
 */
public class ProcessInfoProvider {
	/**
	 * 安卓5.0以前的方法
	 * 获取进程总数的方法
	 * @return
	 *//*
	public static int getProcessCount(Context context) {
		//1,获取activityManager
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//2,获取正在运行进程的集合
		List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
		//3,返回集合的总数
		return runningAppProcesses.size();
	}*/

	/**
	 * 安卓5.0以后的方法
	 * 获取进程总数的方法
	 * @return
	 */
	public static int getProcessCount(Context context) {
		//1,获取正在运行进程的集合
		List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses();
		//2,返回集合的总数
		return runningAppProcesses.size();
	}

	/**
	 * @param context
	 * @return 返回可用的内存数    bytes
	 */
	public static long getAvailSpace(Context context) {
		//1,获取activityManager
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//2,构建存储可用内存的对象
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		//3,给memoryInfo对象赋(可用内存)值
		activityManager.getMemoryInfo(memoryInfo);
		//4,获取memoryInfo中相应可用内存大小
		return memoryInfo.availMem;
	}


	public static long getTotalSpace(Context context) {
		//以下第四步需要API16以上
		/*//1,获取activityManager
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//2,构建存储可用内存的对象
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		//3,给memoryInfo对象赋(可用内存)值
		activityManager.getMemoryInfo(memoryInfo);
		//4,获取memoryInfo中相应可用内存大小
		return memoryInfo.totalMem;*/

		//内存大小写入文件中,读取proc/meminfo文件,读取第一行,获取数字字符,转换成bytes返回
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader("proc/meminfo");
			bufferedReader = new BufferedReader(fileReader);
			String lineOne = bufferedReader.readLine();
			//将字符串转换成字符的数组
			char[] charArray = lineOne.toCharArray();
			//循环遍历每一个字符,如果此字符的ASCII码在0到9的区域内,说明此字符有效
			StringBuffer stringBuffer = new StringBuffer();
			for (char c : charArray) {
				if (c >= '0' && c <= '9') {
					stringBuffer.append(c);
				}
			}
			return Long.parseLong(stringBuffer.toString()) * 1024;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileReader != null && bufferedReader != null) {
				try {
					fileReader.close();
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		return 0;
	}

	/**
	 * 安卓5.0之前的方法
	 * @param context 上下文环境
	 * @return 当前手机正在运行的进程的相关信息
	 *//*
	public static List<ProcessInfo> getProcessInfo(Context context) {
		List<ProcessInfo> processInfoList = new ArrayList<>();

		//1,activityManager管理者对象和PackageManager管理者对象,activityManager管理者获取正在运行进程的包名、使用内存大小
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//PackageManager管理者获取应用的信息，名称、图标、是否系统进程
		PackageManager packageManager = context.getPackageManager();
		//2,获取正在运行的进程的集合
		List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

		//3,循环遍历上诉集合,获取进程相关s信息(名称,包名,图标,使用内存大小,是否为系统进程)
		for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			ProcessInfo processInfo = new ProcessInfo();
			//4,获取进程的名称 == 应用的包名
			processInfo.packageName = runningAppProcessInfo.processName;
			//5,获取进程占用的内存大小(传递一个进程对应的pid数组)
			Debug.MemoryInfo[] processMemoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
			//6,返回数组中索引位置为0的对象,为当前进程的内存信息的对象
			Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			//7,获取已使用的大小
			processInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;

			try {
				ApplicationInfo applicationInfo = packageManager.getApplicationInfo(processInfo.packageName, 0);
				//8,获取应用的名称,图标，判断是否为系统进程
				processInfo.name = applicationInfo.loadLabel(packageManager).toString();
				processInfo.icon = applicationInfo.loadIcon(packageManager);
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					processInfo.isSystem = true;
				} else {
					processInfo.isSystem = false;
				}
			} catch (PackageManager.NameNotFoundException e) {
				//需要处理,如果应用没有名称，把包名给他
				processInfo.name = runningAppProcessInfo.processName;
				processInfo.icon = context.getResources().getDrawable(R.drawable.ic_launcher);
				processInfo.isSystem = true;
				e.printStackTrace();
			}
			processInfoList.add(processInfo);
		}
		return processInfoList;
	}*/


	/**
	 * 安卓5.0的方法
	 * @param context 上下文环境
	 * @return 当前手机正在运行的进程的相关信息
	 */
	public static List<ProcessInfo> getProcessInfo(Context context) {
		List<ProcessInfo> processInfoList = new ArrayList<>();

		//1,activityManager管理者对象和PackageManager管理者对象,activityManager管理者获取正在运行进程的包名、使用内存大小
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//PackageManager管理者获取应用的信息，名称、图标、是否系统进程
		PackageManager packageManager = context.getPackageManager();
		//2,获取正在运行的进程的集合
		List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses();

		//3,循环遍历上诉集合,获取进程相关s信息(名称,包名,图标,使用内存大小,是否为系统进程)
		for (AndroidAppProcess runningAppProcessInfo : runningAppProcesses) {
			ProcessInfo processInfo = new ProcessInfo();
			//4,获取进程的名称 == 应用的包名
			processInfo.packageName = runningAppProcessInfo.name;
			//5,获取进程占用的内存大小(传递一个进程对应的pid数组)
			Debug.MemoryInfo[] processMemoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
			//6,返回数组中索引位置为0的对象,为当前进程的内存信息的对象
			Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			//7,获取已使用的大小
			processInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;

			try {
				ApplicationInfo applicationInfo = packageManager.getApplicationInfo(processInfo.packageName, 0);
				//8,获取应用的名称,图标，判断是否为系统进程
				processInfo.name = applicationInfo.loadLabel(packageManager).toString();
				processInfo.icon = applicationInfo.loadIcon(packageManager);
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					processInfo.isSystem = true;
				} else {
					processInfo.isSystem = false;
				}
			} catch (PackageManager.NameNotFoundException e) {
				//需要处理,如果应用没有名称，把包名给他
				processInfo.name = runningAppProcessInfo.name;
				processInfo.icon = context.getResources().getDrawable(R.drawable.ic_launcher1);
				processInfo.isSystem = true;
				e.printStackTrace();
			}
			processInfoList.add(processInfo);
		}
		return processInfoList;
	}


	/**
	 * 杀进程方法
	 * @param context	上下文环境
	 * @param processInfo	杀死进程所在的javabean的对象
	 */
	public static void killProcess(Context context,ProcessInfo processInfo){
		//1,activityManager管理者对象
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//2.杀死指定包名进程(权限)
		activityManager.killBackgroundProcesses(processInfo.packageName);
	}

	/**
	 * 杀死所有进程
	 * @param context	上下文环境
	 */
	public static void killAllProcess(Context context){
		//1,获取正在运行进程的集合
		List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses();

		for (AndroidAppProcess runningAppProcess : runningAppProcesses) {
			//1,activityManager管理者对象
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			if(runningAppProcess.name.equals(context.getPackageName())){
				continue;
			}
			//2.杀死指定包名进程(权限)
			activityManager.killBackgroundProcesses(runningAppProcess.name);
		}

	}

}
