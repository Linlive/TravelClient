package com.tl.pro.travelkit.internet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 服务器相关配置
 * Created by Administrator on 2016/4/24.
 */
public class ServerConfigure {


	//a li yun server
	//public static final String SERVER_ADDRESS = "http://42.96.138.253";
	public static final String SERVER_ADDRESS = "http://192.168.1.222:8080";

	private static final String CHARSET = "utf-8"; //设置编码
	public static final String DATA_IN_SERVER_FLAG = "map";
	public enum Request {

		POST("POST"), GET("GET");
		private String method;

		Request(String method){
			this.method = method;
		}

		@Override
		public String toString() {
			return "in " + this.getClass().getName() + ": " + method;
		}
	}

	/**
	 * connect to server time out.
	 * unit is milliseconds
	 */
	private static final int REQUEST_TIME_OUT = 10000;

	/**
	 * read message from server time out.
	 * unit is milliseconds
	 */
	private static final int READ_TIME_OUT = 600000;

	/**
	 * 获取一个http链接，只能从网络获取数据，不能传输数据到服务器上
	 *
	 * @param urlString url
	 * @return a connection created by the specified urlString.
	 * @throws MalformedURLException if url is invalid
	 */
	public static HttpURLConnection getConnection(String urlString) throws MalformedURLException {
		return getConnection(urlString, Request.GET, false, true);
	}

	/**
	 * 获取一个http链接，该链接可以与服务器进行数据对流
	 *
	 * @param urlString url
	 * @param method    the method of request
	 * @return a connection created by the specified urlString.
	 * @throws MalformedURLException if url is invalid
	 */
	public static HttpURLConnection getConnection(String urlString, Request method) throws
			MalformedURLException {
		return getConnection(urlString, method, true, true);
	}

	/**
	 * 获取一个http链接，通过参数，按指定的要求返回相应的链接对象。
	 *
	 * @param urlString url
	 * @param method    the method of request.
	 * @param input     whether should get some information from the special connection or not.
	 * @param output    whether should write some information into the special connection or not.
	 * @return a connection created by the specified urlString.
	 * @throws MalformedURLException if url is invalid
	 */
	public static HttpURLConnection getConnection(String urlString, Request method, boolean input,
	                                              boolean output) throws MalformedURLException {
		HttpURLConnection httpURLConnection = null;
		URL url = new URL(SERVER_ADDRESS + urlString);

		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(REQUEST_TIME_OUT);
			httpURLConnection.setReadTimeout(READ_TIME_OUT);
			httpURLConnection.setRequestMethod(method.method);
			httpURLConnection.setUseCaches(true);
			httpURLConnection.setDoInput(input);
			httpURLConnection.setDoOutput(output);
			httpURLConnection.setRequestProperty("Charset", CHARSET);  //设置编码
			httpURLConnection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			httpURLConnection.setRequestProperty("accept", "*/*");
			httpURLConnection.setRequestProperty("connection", "Keep-Alive");
			httpURLConnection.addRequestProperty("user-agent", "Android/4.0");
			/**
			 * reset the http request head just in case.
			 */
			if ("POST".equals(method.method)) {
				httpURLConnection.setUseCaches(false);
				httpURLConnection.setDoOutput(true);
			}
			httpURLConnection.setRequestProperty("Content-type",
					"application/x-java-serialized-object");
			//httpURLConnection.connect();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//nop
		}
		return httpURLConnection;
	}

	// 文件上传
	private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
	public static HttpURLConnection getMultiPartConnection(String urlString, String boundary) throws MalformedURLException {
		HttpURLConnection httpURLConnection = null;
		URL url = new URL(SERVER_ADDRESS + urlString);

		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(REQUEST_TIME_OUT);
			httpURLConnection.setReadTimeout(READ_TIME_OUT);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("Charset", CHARSET);  //设置编码
			httpURLConnection.setRequestProperty("connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Content-type", CONTENT_TYPE + ";boundary=" + boundary);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//nop
		}
		return httpURLConnection;
	}
}
