package com.tl.pro.travelkit.util;


import android.util.Log;

import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.internet.UrlSource;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 上传工具类
 * 单例模式
 * Created by Administrator on 2016/5/19.
 */
public class UploadUtil {

	private static UploadUtil uploadUtil;
	// 文件上传
	private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
	private static final String PREFIX = "----";
	private static final String LINE_END = "\r\n";
	private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型

	private UploadUtil() {
	}

	/**
	 * 单例模式获取上传工具类
	 *
	 * @return
	 */
	public static UploadUtil getInstance() {
		if (uploadUtil == null) {
			synchronized (UploadUtil.class) {
				if (uploadUtil == null) {
					uploadUtil = new UploadUtil();
				}
			}
		}
		return uploadUtil;
	}

	private static final String TAG = "UploadUtil";
	private int readTimeOut = 10 * 1000; // 读取超时
	private int connectTimeout = 10 * 1000; // 超时时间
	/***
	 * 请求使用多长时间
	 */
	private static int requestTime = 0;

//	private static final String CHARSET = "utf-8"; // 设置编码
	private static final String CHARSET = "UTF-8"; // 设置编码

	/***
	 * 上传成功
	 */
	public static final int UPLOAD_SUCCESS_CODE = 1;
	/**
	 * 文件不存在
	 */
	public static final int UPLOAD_FILE_NOT_EXISTS_CODE = 2;
	/**
	 * 服务器出错
	 */
	public static final int UPLOAD_SERVER_ERROR_CODE = 3;

	protected static final int WHAT_TO_UPLOAD = 1;
	protected static final int WHAT_UPLOAD_DONE = 2;

	/**
	 * android上传文件到服务器
	 *
	 * @param filePath   需要上传的文件的路径
	 * @param fileKey    在网页上<input type=file name=xxx/> xxx就是这里的fileKey
	 * @param requestURL 请求的URL
	 */
	public void uploadFile(String filePath, String fileKey, String requestURL,
	                       Map<String, String> param) {
		if (filePath == null) {
			sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE, "文件不存在");
			return;
		}
		try {
			File file = new File(filePath);
			uploadFile(file, fileKey, requestURL, param);
		} catch (Exception e) {
			sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE, "文件不存在");
			e.printStackTrace();
			return;
		}
	}

	public void uploadFile(ArrayList<String> filePaths, String fileKey, String requestURL,
	                       Map<String, String> param) {
		int index = 0;
		for (String tmpFilePath : filePaths) {
			uploadFile(tmpFilePath, fileKey + (index++), requestURL, param);
		}
	}


	/**
	 * android上传文件到服务器
	 *
	 * @param file       需要上传的文件
	 * @param fileKey    在网页上<input type=file name=xxx/> xxx就是这里的fileKey
	 * @param requestURL 请求的URL
	 */
	public void uploadFile(final File file, final String fileKey,
	                       final String requestURL, final Map<String, String> param) {
		if (file == null || (!file.exists())) {
			sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE, "文件不存在");
			return;
		}

		Log.i(TAG, "请求的URL=" + requestURL);
		Log.i(TAG, "请求的fileName=" + file.getName());
		Log.i(TAG, "请求的fileKey=" + fileKey);
		new Thread(new Runnable() { //开启线程上传文件
			@Override
			public void run() {
				toUploadFile(file, fileKey, requestURL, param);
			}
		}).start();
	}

	private void toUploadFile(File file, String fileKey, String requestURL, Map<String, String> param) {
		String result = null;
		requestTime = 0;

		long requestTime = System.currentTimeMillis();
		long responseTime = 0;

		try {
			HttpURLConnection conn = ServerConfigure.getMultiPartConnection(requestURL, BOUNDARY);

			/**
			 * 当文件不为空，把文件包装并且上传
			 */
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			StringBuffer sb = null;
			String params = "";

			/***
			 * 以下是用于上传参数
			 */
			if (param != null && param.size() > 0) {
				Iterator<String> it = param.keySet().iterator();
				while (it.hasNext()) {
					sb = new StringBuffer();
					String key = it.next();
					String value = param.get(key);
					sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
					sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END);
					sb.append(value).append(LINE_END);
					params = sb.toString();
					Log.i(TAG, key + "=" + params + "##");
					dos.write(params.getBytes());
					dos.flush();
				}
			}

			sb = new StringBuffer();
			/**
			 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
			 * filename是文件的名字，包含后缀名的 比如:abc.png
			 */
			sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
			sb.append("Content-Disposition:form-data; name=\"" + fileKey
					+ "\"; filename=\"" + file.getName() + "\"" + LINE_END);
			sb.append("Content-Type:image/pjpeg" + LINE_END); // 这里配置的Content-type很重要的 ，用于服务器端辨别文件的类型的
			//sb.append(LINE_END);
			params = sb.toString();
			sb = null;
			Log.i(TAG, file.getName() + "=" + params + "##");
			dos.write(params.getBytes());
			dos.flush();
			/**
			 * 上传文件
			 * */
			InputStream is = new FileInputStream(file);
			onUploadProcessListener.initUpload((int) file.length());
			byte[] bytes = new byte[1024];
			int len = 0;
			int curLen = 0;
			while ((len = is.read(bytes)) != -1) {
				curLen += len;
				dos.write(bytes, 0, len);
				onUploadProcessListener.onUploadProcess(curLen);
			}
			is.close();

			//dos.write(LINE_END.getBytes());
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
			dos.write(end_data);
			dos.flush();
			//
			// dos.write(tempOutputStream.toByteArray());
			/**
			 * 获取响应码 200=成功 当响应成功，获取响应的流
			 */
			int res = conn.getResponseCode();
			responseTime = System.currentTimeMillis();
			this.requestTime = (int) ((responseTime - requestTime) / 1000);
			Log.e(TAG, "response code:" + res);
			if (res == 200) {
				Log.e(TAG, "request success");
				InputStream input = conn.getInputStream();
				StringBuffer sb1 = new StringBuffer();
				int ss;
				while ((ss = input.read()) != -1) {
					sb1.append((char) ss);
				}
				result = sb1.toString();
				Log.e(TAG, "result : " + result);
				sendMessage(UPLOAD_SUCCESS_CODE, "上传结果：" + result);
				return;
			} else {
				Log.e(TAG, "request error");
				sendMessage(UPLOAD_SERVER_ERROR_CODE, "上传失败：code=" + res);
				return;
			}
		} catch (MalformedURLException e) {
			sendMessage(UPLOAD_SERVER_ERROR_CODE, "上传失败：error=" + e.getMessage());
			e.printStackTrace();
			return;
		} catch (IOException e) {
			sendMessage(UPLOAD_SERVER_ERROR_CODE, "上传失败：error=" + e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	public void upLoadFiles(ArrayList<String> filePaths) {

		HttpURLConnection conn = null;
		ArrayList<Thread> threads = new ArrayList<>();
		try {
			conn = ServerConfigure.getMultiPartConnection(UrlSource.UPLOAD_PHOTO_AND_DETAIL, BOUNDARY);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		final HttpURLConnection connection = conn;
		for (String tmpfPath : filePaths){
			final File tmpFile = new File(tmpfPath);
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					upLoadFile(tmpFile);
				}
			});
			threads.add(thread);
			thread.start();
		}
	}
	public void upLoadFile(File file){
		if (file == null) {
			return;
		}
		try {
//			HttpURLConnection conn = ServerConfigure.getMultiPartConnection(urls, BOUNDARY);
			URL url = new URL(ServerConfigure.SERVER_ADDRESS + UrlSource.UPLOAD_PHOTO_AND_DETAIL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10*1000);
			conn.setReadTimeout(60*1000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Charset", CHARSET);  //设置编码
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Content-type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

			/**
			 * 当文件不为空，把文件包装并且上传
			 */
			StringBuffer sb = new StringBuffer();
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINE_END);
			/**
			 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
			 * filename是文件的名字，包含后缀名的 比如:abc.png
			 */
			sb.append("Content-Disposition: form-data; name=\"image\"; filename=\""	+ file.getName() + "\"" + LINE_END);
			sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
			sb.append(LINE_END);

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.write(sb.toString().getBytes());
			InputStream is = new FileInputStream(file);
			byte[] bytes = new byte[1024];
			int len = 0;
			int current = 0;
			onUploadProcessListener.initUpload(file.length());
			while ((len = is.read(bytes)) != -1) {
				dos.write(bytes, 0, len);
				current += len;
				onUploadProcessListener.onUploadProcess(current);
			}
			is.close();
			dos.write(LINE_END.getBytes());

			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END + LINE_END).getBytes();
			dos.write(end_data);
			//dos.flush();
			onUploadProcessListener.onUploadDone(conn.getResponseCode(), "server return message " + "");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//return FAILURE;
	}

	public void createPart(HashMap<String, String> descInfo, ArrayList<String> filePaths) {
		HttpURLConnection connection = null;
		try {
			connection = ServerConfigure.getMultiPartConnection(UrlSource.UPLOAD_PHOTO_AND_DETAIL, BOUNDARY);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}
		ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();

		StringBuffer sBufferOut = new StringBuffer();

		//first line
		sBufferOut.append(PREFIX + BOUNDARY + LINE_END);
		//first content
//		for (String key : descInfo.keySet())
		{
			sBufferOut.append("Content-Disposition: form-data; name=\"text\"" + LINE_END);
			sBufferOut.append(LINE_END);

		//	sBufferOut.append(key).append(descInfo.get(key) + "value");
		}
		//sBufferOut.append(PREFIX + BOUNDARY + LINE_END);
		DataOutputStream dataOutputStream;
		try {
			dataOutputStream = new DataOutputStream(connection.getOutputStream());
			//写入文字信息
			//dataOutputStream.write(sBufferOut.toString().getBytes());

			sBufferOut = new StringBuffer();

			//file part
			for (String filePath : filePaths) {

				File file = new File(filePath);
				sBufferOut.append("Content-Disposition: form-data; name=\"image\"; filename=\"" + file.getName() + "\"" + LINE_END);
				sBufferOut.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END + LINE_END);
				//写入文件描述
				dataOutputStream.write(sBufferOut.toString().getBytes());

				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				int current = 0;
				//读取文件，写入网络
				onUploadProcessListener.initUpload(file.length());
				while ((len = is.read(bytes)) != -1) {
					dataOutputStream.write(bytes, 0, len);
					current += len;
					onUploadProcessListener.onUploadProcess(current);
				}
				is.close();
				//sb2 = new StringBuffer();
				//sBufferOut.append(PREFIX + BOUNDARY + LINE_END);
				//single part end，文件part结束
				//dataOutputStream.write(sBufferOut.toString().getBytes());
			}

			sBufferOut = new StringBuffer();
			String endPart = sBufferOut.append(PREFIX + BOUNDARY + PREFIX + LINE_END).toString();
			//whole part end, 本次part结束
			dataOutputStream.write(endPart.getBytes());
			onUploadProcessListener.onUploadDone(connection.getResponseCode(), "server message is " + "");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			connection.disconnect();
		}
	}

	/**
	 * 发送上传结果
	 *
	 * @param responseCode
	 * @param responseMessage
	 */
	private void sendMessage(int responseCode, String responseMessage) {
		onUploadProcessListener.onUploadDone(responseCode, responseMessage);
	}

	/**
	 * 下面是一个自定义的回调函数，用到回调上传文件是否完成
	 *
	 * @author shimingzheng
	 */
	public interface OnUploadProcessListener {
		/**
		 * 上传响应
		 *
		 * @param responseCode
		 * @param message
		 */
		void onUploadDone(int responseCode, String message);

		/**
		 * 上传中
		 *
		 * @param uploadSize
		 */
		void onUploadProcess(int uploadSize);

		/**
		 * 准备上传
		 *
		 * @param fileSize
		 */
		void initUpload(long fileSize);
	}

	private OnUploadProcessListener onUploadProcessListener;

	public void setOnUploadProcessListener(
			OnUploadProcessListener onUploadProcessListener) {
		this.onUploadProcessListener = onUploadProcessListener;
	}

	public int getReadTimeOut() {
		return readTimeOut;
	}

	public void setReadTimeOut(int readTimeOut) {
		this.readTimeOut = readTimeOut;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * 获取上传使用的时间
	 *
	 * @return
	 */
	public static int getRequestTime() {
		return requestTime;
	}

	public static interface uploadProcessListener {

	}

}
