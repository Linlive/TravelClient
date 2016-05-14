package com.tl.pro.travelkit.util.encryption;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES advance encryption standard.
 * 高级加密
 *
 * Created by Administrator on 2016/4/27.
 */
public class KitAESCoder extends KeyEncrypt {

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
	public static final String ALGORITHM = "AES";

	public static final int SALT_LEN = 16;

//	private static final String PRIVATE_KEY = "abcdefghabcdefgh";
	private static final String PRIVATE_KEY = "KIT_SERVER_ABCDE";
	//实例化加密类需要的的参数
	private static final String INITIALIZE_PARAM = "AES/CBC/PKCS5Padding";
	private static AlgorithmParameters param;
	private static final String MAGIC_STRING = "PBKDF2WithHmacSHA1";//"PBEWITHMD5andDES";//
	private static final int KEY_LEN_BITS = 128; // see notes below where this is used.
	private static final int ITERATIONS = 65536;
	private static final int MAX_FILE_BUF = 1024;

	/**
	 * 将明文通过AES方式加密后，再以BASE64进行加密，转换为BASE64编码
	 *
	 * @param cleartext 明文
	 * @param privateKey  密钥
	 * @return 加密后的字符串
	 * @throws Exception
	 */
	public static String encrypt(String cleartext, String privateKey) throws Exception{
		byte[] bytes = encrypt(cleartext.getBytes("UTF-8"), privateKey);
		return encryptBASE64(bytes);
	}
	public static String encrypt(String cleartext) throws Exception{
		return encrypt(cleartext, PRIVATE_KEY);
	}

	/**
	 * http://www.cnblogs.com/lianghui66/archive/2013/03/07/2948494.html
	 * */
	public static String encrypt(String cleartext, boolean flag) throws Exception{

		String text = "abcdefg";   //要加密的字符串

		String key = "lianghuilonglong"; //私钥   AES固定格式为128/192/256 bits.即：16/24/32bytes。DES固定格式为128bits，即8bytes。

		String iv   = "aabbccddeeffgghh";//初始化向量参数，AES 为16bytes. DES 为8bytes.



		Key keySpec = new SecretKeySpec(key.getBytes(), "AES");    //两个参数，第一个为私钥字节数组， 第二个为加密方式 AES或者DES

		IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //实例化加密类，参数为加密方式，要写全

		cipher.init(Cipher.ENCRYPT_MODE,  keySpec, ivSpec);           //初始化，此方法可以采用三种方式，按服务器要求来添加。（1）无第三个参数（2）第三个参数为SecureRandom random = new SecureRandom();中random对象，随机数。(AES不可采用这种方法)（3）采用此代码中的IVParameterSpec
//		cipher.init(Cipher.ENCRYPT_MODE, keySpec);

//		SecureRandom random = new SecureRandom();

//		cipher.init(Cipher.ENCRYPT_MODE, keySpec, random);
		return encryptBASE64(cipher.doFinal());
//		return encrypt(cleartext, PUBLIC_KEY);
	}

	/**
	 * 将密文通过BASE64方式解密后，再以进行AES解密。
	 *
	 * @param ciphertext 密文
	 * @param privateKey  密钥
	 * @return 加密所得字符串
	 * @throws Exception
	 */
	public static String decrypt(String ciphertext, String privateKey) throws Exception{
		byte[] passwordFirst = decryptBASE64(ciphertext);
		byte[] passwordSecond = decrypt(passwordFirst, privateKey);
		return new String(passwordSecond, "UTF-8");
	}
	public static String decrypt(String ciphertext) throws Exception{
		return decrypt(ciphertext, PRIVATE_KEY);
	}


	/**
	 * encode
	 *
	 * @param data the real value.
	 * @param keyIv  key
	 * @return the password value.
	 * @throws Exception
	 */
	public static byte[] encrypt (byte[] data, String keyIv) throws Exception {

		IvParameterSpec ivSpace = new IvParameterSpec(keyIv.getBytes());
		Cipher cipher = Cipher.getInstance(INITIALIZE_PARAM);
		Key k = inKey(keyIv);
		param = cipher.getParameters();
		cipher.init(Cipher.ENCRYPT_MODE, k, ivSpace);
		return cipher.doFinal(data);
	}

	/**
	 * decode
	 *
	 * @param data the password value.
	 * @param keyIv  key
	 * @return the real value
	 * @throws Exception
	 */
	public static byte[] decrypt (byte[] data, String keyIv) throws Exception {

		IvParameterSpec ivSpace = new IvParameterSpec(keyIv.getBytes());
		Cipher cipher = Cipher.getInstance(INITIALIZE_PARAM);
		Key k = inKey(keyIv);
		param = cipher.getParameters();
		cipher.init(Cipher.DECRYPT_MODE, k, ivSpace);
		return cipher.doFinal(data);
	}

	/**
	 * create key
	 *
	 * @return the key has been encoded.
	 * @throws Exception
	 */
	public static String initKey() throws Exception {
		return initKey(null);
	}
	/**
	 * create key
	 *
	 * @param seed the seed
	 * @return the key has been encoded.
	 * @throws Exception
	 */
	public static String initKey(String seed) throws Exception {

		return initKey(seed, ALGORITHM);
	}

	//key 私钥
	private static Key inKey(String key) throws UnsupportedEncodingException {
		//两个参数，第一个为私钥字节数组， 第二个为加密方式 AES或者DES
		return new SecretKeySpec(key.getBytes("utf-8"), ALGORITHM);
	}
	/**
	 * change key to Key({@link Key})
	 *
	 * @param keyString **
	 * @return abstract key
	 * @throws Exception
	 */
	protected static Key toKey (String keyString) throws Exception{

		SecretKeyFactory factory;
		byte[] mSalt = new byte[SALT_LEN];
		factory = SecretKeyFactory.getInstance(MAGIC_STRING);
		char[] array = keyString.toCharArray();
		KeySpec spec = new PBEKeySpec(array, mSalt, ITERATIONS, KEY_LEN_BITS);
		SecretKey tmp = factory.generateSecret(spec);
		return new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
	}

	private static char[] bytesToChar(byte[] bytes){
		Charset cs = Charset.forName ("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate (bytes.length);
		bb.put (bytes);
		bb.flip ();
		CharBuffer cb = cs.decode (bb);
		return cb.array();
	}
}
