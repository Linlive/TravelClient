package com.tl.pro.travelkit.util.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;

/**
 * DES data encryption standard.
 * 数据加密
 *
 *
 * Created by Administrator on 2016/4/27.
 */
public class KitDESCoder extends KeyEncrypt {

	/**
	 * ALGORITHM 算法 <br>
	 * 可替换为以下任意一种算法，同时key值的size相应改变。
	 * <p>
	 * <pre>
	 * DES                  key size must be equal to 56
	 * DESede(TripleDES)    key size must be equal to 112 or 168
	 * AES                  key size must be equal to 128, 192 or 256,but 192 and 256 bits may not be available
	 * Blowfish             key size must be multiple of 8, and can only range from 32 to 448 (inclusive)
	 * RC2                  key size must be between 40 and 1024 bits
	 * RC4(ARCFOUR)         key size must be between 40 and 1024 bits
	 * </pre>
	 */
	public static final String ALGORITHM = "DES";

	/**
	 * 将明文通过DES方式加密后，再以BASE64进行加密。
	 *
	 * @param cleartext 明文
	 * @param publicKey  公钥
	 * @return 加密后的字符串
	 * @throws Exception
	 */
	public static String encrypt (String cleartext, String publicKey) throws Exception {

		byte[] bytes = encrypt(cleartext.getBytes("UTF-8"), initKey(publicKey));
		return encryptBASE64(bytes);
	}

	public static String encrypt (String lightValue) throws Exception {

		byte[] bytes = encrypt(lightValue.getBytes("UTF-8"), initKey(PUBLIC_KEY));
		return encryptBASE64(bytes);
	}

	/**
	 * 将密文通过BASE64方式解密后，再以进行DES解密。
	 *
	 * @param ciphertext 密文
	 * @param publicKey  公钥
	 * @return 加密所得字符串
	 * @throws Exception
	 */
	public static String decrypt (String ciphertext, String publicKey) throws Exception {

		byte[] passwordFirst = decryptBASE64(ciphertext);
		byte[] passwordSecond = decrypt(passwordFirst, initKey(publicKey));
		return new String(passwordSecond, "UTF-8");
	}

	public static String decrypt (String ciphertext) throws Exception {

		byte[] passwordFirst = decryptBASE64(ciphertext);
		byte[] passwordSecond = decrypt(passwordFirst, initKey(PUBLIC_KEY));
		return new String(passwordSecond, "UTF-8");
	}

	/**
	 * encode
	 *
	 * @param data the real value.
	 * @param key  key
	 * @return the password value.
	 * @throws Exception
	 */
	public static byte[] encrypt (byte[] data, Key key) throws Exception {

		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data);
	}

	public static byte[] encrypt (byte[] data, String key) throws Exception {

		return encrypt(data, toKey(key));
	}


	/**
	 * decode
	 *
	 * @param data the password value.
	 * @param key  key
	 * @return the real value
	 * @throws Exception
	 */
	public static byte[] decrypt (byte[] data, Key key) throws Exception {

		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(data);
	}

	public static byte[] decrypt (byte[] data, String key) throws Exception {

		return decrypt(data, toKey(key));
	}


	/**
	 * create key
	 *
	 * @return the key has been encoded.
	 * @throws Exception
	 */
	public static String initKey () throws Exception {

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
	public static String initKey (String seed) throws Exception {

		return initKey(seed, ALGORITHM);
	}

	/**
	 * change key to Key({@link Key})
	 *
	 * @param keyString **
	 * @return abstract key
	 * @throws Exception
	 */
	protected static Key toKey (String keyString) throws Exception {

		byte[] keyTmp = keyString.getBytes("UTF-8");
		byte[] keys = encryptMD5(keyTmp);
		DESKeySpec dks = new DESKeySpec(keys);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
		return keyFactory.generateSecret(dks);
	}
}
