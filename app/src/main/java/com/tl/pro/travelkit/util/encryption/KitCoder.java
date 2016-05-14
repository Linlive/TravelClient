package com.tl.pro.travelkit.util.encryption;

import android.util.Base64;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 基础的加密.
 * <p>
 * Created by Administrator on 2016/4/27.
 */
public abstract class KitCoder {


	public static final String KEY_SHA = "SHA";
	public static final String KEY_MD5 = "MD5";

	/**
	 * MAC算法可选以下多种算法
	 * <p>
	 * <pre>
	 * HmacMD5
	 * HmacSHA1
	 * HmacSHA256
	 * HmacSHA384
	 * HmacSHA512
	 * </pre>
	 */
	public static final String KEY_MAC = "HmacMD5";

	/**
	 * encoded by BASE64
	 *
	 * @param key the key to be encode.
	 * @return the value that have been decoded.
	 */
	public static String encryptBASE64 (byte[] key) {
		return new String(Base64.encode(key, 1));
//		return (new BASE64Encoder()).encodeBuffer(key);
	}

	/**
	 * decoded by BASE64
	 *
	 * @param key It can be used to decoding a password value.
	 * @return the bytes of value.
	 * @throws IOException
	 */
	public static byte[] decryptBASE64 (String key) throws IOException {

		return Base64.decode(key, 1);
//		return (new BASE64Decoder()).decodeBuffer(key);
	}

	public static String encryptMD5(String data) throws Exception{

		byte[] dataBytes = data.getBytes("UTF-8");
		byte[] md5Bytes = encryptMD5(dataBytes);
		return encryptBASE64(md5Bytes);
	}

	/**
	 * encoded by MD5
	 *
	 * @param data the data to be encode.
	 * @return the data that have been encoded.
	 * @throws NoSuchAlgorithmException throw {@link NoSuchAlgorithmException} when the algorithm is incorrect.
	 */
	public static byte[] encryptMD5 (byte[] data) throws NoSuchAlgorithmException {

		MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
		md5.update(data);
		return md5.digest();
	}

	public static String encryptSHA(String data) throws Exception{

		byte[] dataBytes = data.getBytes("UTF-8");
		byte[] md5Bytes = encryptMD5(dataBytes);
//		res = new String(md5Bytes, "UTF-8");
		return encryptBASE64(md5Bytes);
	}
	/**
	 * encoded by SHA
	 *
	 * @param data the data to be encode.
	 * @return the data that have been encoded.
	 * @throws NoSuchAlgorithmException throw {@link NoSuchAlgorithmException} when the algorithm is incorrect.
	 */
	public static byte[] encryptSHA (byte[] data) throws NoSuchAlgorithmException {

		MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
		sha.update(data);

		return sha.digest();
	}

	/**
	 * encoded by HMAC
	 *
	 * @param data the data to be encode.
	 * @param key  the password key
	 * @return the value have been encoded.
	 * @throws Exception this Exception have more than one message.
	 */
	public static byte[] encryptHMAC (byte[] data, String key) throws Exception {

		SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), KEY_MAC);
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return mac.doFinal(data);
	}

	/**
	 * init the key of HMAC.
	 *
	 * @return the key that has been initialized.
	 * @throws NoSuchAlgorithmException throw {@link NoSuchAlgorithmException} when the algorithm is incorrect.
	 */
	public static String initMacKey () throws NoSuchAlgorithmException {

		KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);
		SecretKey secretKey = keyGenerator.generateKey();
		return encryptBASE64(secretKey.getEncoded());
	}
}
