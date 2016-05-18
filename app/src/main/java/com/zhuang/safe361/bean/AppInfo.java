package com.zhuang.safe361.bean;

import android.graphics.drawable.Drawable;

/**
 * 软件管理列表model
 */
public class AppInfo {
	//	名称,包名,图标,(内存,sd卡),(系统,用户)
	public String name;
	public String packageName;
	public Drawable icon;
	public boolean isSdCard;
	public boolean isSystem;

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public boolean isSdCard() {
		return isSdCard;
	}

	public void setSdCard(boolean isSdCard) {
		this.isSdCard = isSdCard;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSystem() {
		return isSystem;
	}

	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
}
