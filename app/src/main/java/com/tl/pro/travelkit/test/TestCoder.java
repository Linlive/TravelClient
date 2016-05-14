package com.tl.pro.travelkit.test;

import android.test.FlakyTest;

import com.tl.pro.travelkit.util.encryption.KitAESCoder;


/**
 * Created by Administrator on 2016/5/4.
 */
public class TestCoder {

	public static void main(String[] args) throws Exception {
		String password = "cc";
		String aes = KitAESCoder.encrypt(password);
		System.out.println("AES 加密密文：" + aes);
		System.out.println("解密\t" + KitAESCoder.decrypt(aes));
	}
	@FlakyTest
	public void t() {
		String password = "cc";
		String aes = null;
		try {
			aes = KitAESCoder.encrypt(password);

			System.out.println("AES 加密密文：" + aes);
			System.out.println("解密\t" + KitAESCoder.decrypt(aes));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
