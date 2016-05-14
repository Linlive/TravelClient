package com.tl.pro.travelkit.util.encryption;

import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * 主要提供给子类初始化key
 *
 * Created by Administrator on 2016/4/28.
 */
public class KeyEncrypt extends KitCoder{

	public static final String PUBLIC_KEY = "KIT_SERVER_KEY";
	/**
	 * create key
	 *
	 * @return the key has been encoded.
	 * @throws Exception
	 */
	protected static String initKey() throws Exception{
		return initKey(null);
	}

	/**
	 * create key
	 * 根据seed生成与seed的md5值相关的随机数，然后通过BASE64算法
	 * 将得到的字节数组换成BASE64字符
	 *
	 * @param seed the seed
	 * @return the key has been encoded.
	 * @throws Exception
	 */
	protected static String initKey(String seed) throws Exception{
		return initKey(seed, null);
	}
	/**
	 *
	 * @param seed seed
	 * @param algorithm which algorithm should be encrypted.
	 * @return the key
	 * @throws Exception
	 */
	protected static String initKey(String seed, String algorithm) throws Exception{
		if(algorithm == null){
			return null;
		}
		SecureRandom secureRandom;
		if(seed == null){
			secureRandom = new SecureRandom();
		} else {
			secureRandom = new SecureRandom(encryptMD5(seed.getBytes("UTF-8")));
		}
		KeyGenerator kg = KeyGenerator.getInstance(algorithm);
		kg.init(secureRandom);
		SecretKey secretKey = kg.generateKey();
		return encryptBASE64(secretKey.getEncoded());
	}
}
