package com.zhuang.safe361;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import com.zhuang.safe361.bean.BlackNumberInfo;
import com.zhuang.safe361.dao.BlackNumberDao;

import java.util.List;
import java.util.Random;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
	public ApplicationTest() {
		super(Application.class);
	}

	public Context mContext;

	@Override
	protected void setUp() throws Exception {
		this.mContext = getContext();
		super.setUp();
	}

	public void testAdd(){
		BlackNumberDao dao = BlackNumberDao.getInstance(mContext);
		Random random = new Random();
		for (int i = 0;i<200;i++){
			Long number = 13300000000l + i;
			dao.add(number + "", String.valueOf(random.nextInt(3) + 1));
		}
	}

	public void testDelete(){
		BlackNumberDao dao = BlackNumberDao.getInstance(mContext);
		boolean delete = dao.delete("13300000000");
		assertEquals(true,delete);
	}

	public void testFind(){
		BlackNumberDao dao = BlackNumberDao.getInstance(mContext);
		String number = dao.getMode("13300000001");
		System.out.println(number);
	}

	public void testFindAll(){
		BlackNumberDao dao = BlackNumberDao.getInstance(mContext);
		List<BlackNumberInfo> list = dao.findAll();
		for (BlackNumberInfo blackNumberInfo : list) {
			System.out.println(blackNumberInfo.getNumber()+";"+blackNumberInfo.getMode());
		}
	}
}