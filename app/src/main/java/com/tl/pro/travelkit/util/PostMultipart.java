package com.tl.pro.travelkit.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.internet.UrlSource;
import com.tl.pro.travelkit.util.listener.ProgressRequestListener;
import com.tl.pro.travelkit.util.listener.ProgressResponseListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import cn.edu.zafu.coreprogress.helper.ProgressHelper;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;


/**
 * Created by Administrator on 2016/5/19.
 */
public class PostMultipart {


	private static final String IMG_CLIENT_ID = UUID.randomUUID().toString();
	private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

	private static final OkHttpClient client = new OkHttpClient();

	/*
	public static void post(HashMap<String, String> param, ArrayList<String> paths){
		PostFormBuilder postFileBuilder = OkHttpUtils.post().params(param);
		postFileBuilder.url(ServerConfigure.SERVER_ADDRESS + UrlSource.UPLOAD_PHOTO_AND_DETAIL);
		for (String str : paths) {
			postFileBuilder.addFile("mfile", str, new File(str));
		}
		try {
			okhttp3.Response response = postFileBuilder.build().execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/

	public static void upLoad(final Context context, ArrayList<String> filePaths){
		for (String tmpPath : filePaths){
			upLoad(context, tmpPath);
		}
	}
	private static MultipartBuilder addFilePart(MultipartBuilder multipartBuilder, ArrayList<String> filePaths){
		for (String path : filePaths){
			File file = new File(path);
			multipartBuilder.addFormDataPart("photo_" + 0, file.getName(), RequestBody.create(null, file));
		}
		return multipartBuilder;
	}

	private static void addTextPart(MultipartBuilder multipartBuilder, HashMap<String, String> param) {
		for (String key : param.keySet()){
//			multipartBuilder.addFormDataPart(key, param.get(key));
			multipartBuilder.addFormDataPart(key, null, RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=gb2312"), param.get(key)));
		}
	}

	/**
	 *
 	 * @param params
	 * @param filePaths
	 * @param uiProgressListener
	 */
	public static void upLoadGoods(List<HashMap<String, String>> params, ArrayList<String> filePaths, UIProgressListener uiProgressListener){
		MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);

		for(HashMap<String, String> map : params){
			addTextPart(multipartBuilder, map);
		}
		addFilePart(multipartBuilder, filePaths);
		RequestBody requestBody = multipartBuilder.build();
		//进行包装，使其支持进度回调
		final Request request = new Request.Builder().
				url(ServerConfigure.SERVER_ADDRESS + UrlSource.UPLOAD_PHOTO_AND_DETAIL)
				.post(ProgressHelper.addProgressRequestListener(
						requestBody, uiProgressListener))
				.build();

		//开始请求

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				Log.e("TAG", "error " + e.getMessage());
			}
			@Override
			public void onResponse(Response response) throws IOException {
				Log.e("TAG", response.body().string());
			}
		});

	}
	public static void upLoad(final Context context, String filePath){
		OkHttpClient client = new OkHttpClient();
		File file = new File(filePath);
		if(!file.canRead()) {
			return;
		}
		//这个是ui线程回调，可直接操作UI
		final UIProgressListener uiProgressRequestListener = new UIProgressListener() {
			@Override
			public void onUIProgress(long bytesWrite, long contentLength, boolean done) {
				Log.e("TAG", "bytesWrite:" + bytesWrite);
				Log.e("TAG", "contentLength" + contentLength);
				Log.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
				Log.e("TAG", "done:" + done);
				Log.e("TAG", "================================");
				//ui层回调
				//uploadProgress.setProgress((int) ((100 * bytesWrite) / contentLength));
				Toast.makeText(context, bytesWrite + " " + contentLength + " " + done, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onUIStart(long bytesWrite, long contentLength, boolean done) {

				Toast.makeText(context,"start",Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onUIFinish(long bytesWrite, long contentLength, boolean done) {

				Toast.makeText(context,"end",Toast.LENGTH_SHORT).show();
			}
		};
		//构造上传请求，类似web表单
		RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
				.addFormDataPart("photo", file.getName(), RequestBody.create(null, file))
//				.addPart(Headers.of("Content-Disposition",
//						"form-data; name=\"goodsPictures\";filename=\"" + file.getName() + "\""),
//						RequestBody.create(MediaType.parse("application/octet-stream"), file))
				.build();

		//进行包装，使其支持进度回调
		final Request request = new Request.Builder().
				url(ServerConfigure.SERVER_ADDRESS + UrlSource.UPLOAD_PHOTO_AND_DETAIL)
				.post(ProgressHelper.addProgressRequestListener(
						requestBody, uiProgressRequestListener))
				.build();

		//开始请求
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				Log.e("TAG", "error " + e.getMessage());
			}
			@Override
			public void onResponse(Response response) throws IOException {
				Log.e("TAG", response.body().string());
			}
		});
	}

	//上传
	private void upload() {
		File file = new File("/sdcard/1.doc");
		//此文件必须在手机上存在，实际情况下请自行修改，这个目录下的文件只是在我手机中存在。


		//这个是非ui线程回调，不可直接操作UI
		final ProgressRequestListener progressListener = new ProgressRequestListener() {
			@Override
			public void onRequestProgress(long bytesWrite, long contentLength, boolean done) {
				Log.e("TAG", "bytesWrite:" + bytesWrite);
				Log.e("TAG", "contentLength" + contentLength);
				Log.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
				Log.e("TAG", "done:" + done);
				Log.e("TAG", "================================");
			}
		};


		//这个是ui线程回调，可直接操作UI
		final UIProgressRequestListener uiProgressRequestListener = new UIProgressRequestListener() {
			@Override
			public void onUIRequestProgress(long bytesWrite, long contentLength, boolean done) {
				Log.e("TAG", "bytesWrite:" + bytesWrite);
				Log.e("TAG", "contentLength" + contentLength);
				Log.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
				Log.e("TAG", "done:" + done);
				Log.e("TAG", "================================");
				//ui层回调
				//uploadProgress.setProgress((int) ((100 * bytesWrite) / contentLength));
				//Toast.makeText(getApplicationContext(), bytesWrite + " " + contentLength + " " + done, Toast.LENGTH_LONG).show();
			}
		};

		//构造上传请求，类似web表单
		RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
				.addFormDataPart("hello", "android")
				.addFormDataPart("photo", file.getName(), RequestBody.create(null, file))
				.addPart(Headers.of("Content-Disposition",
						"form-data; name=\"another\";filename=\"another.dex\""),
						RequestBody.create(MediaType.parse("application/octet-stream"), file))
				.build();

		//进行包装，使其支持进度回调
		final Request request = new Request.Builder()
				.url("http://121.41.119.107:81/test/result.php")
				.post(ProgressHelper.addProgressRequestListener(requestBody, uiProgressRequestListener))
				.build();
		//开始请求
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				Log.e("TAG", "error ", e);
			}

			@Override
			public void onResponse(Response response) throws IOException {
				Log.e("TAG", response.body().string());
			}
		});
	}
	// 下载
	private void download() {
		//这个是非ui线程回调，不可直接操作UI
		final ProgressResponseListener progressResponseListener = new ProgressResponseListener() {
			@Override
			public void onResponseProgress(long bytesRead, long contentLength, boolean done) {
				Log.e("TAG", "bytesRead:" + bytesRead);
				Log.e("TAG", "contentLength:" + contentLength);
				Log.e("TAG", "done:" + done);
				if (contentLength != -1) {
					//长度未知的情况下回返回-1
					Log.e("TAG", (100 * bytesRead) / contentLength + "% done");
				}
				Log.e("TAG", "================================");
			}
		};


		//这个是ui线程回调，可直接操作UI
		final UIProgressResponseListener uiProgressResponseListener = new UIProgressResponseListener() {
			@Override
			public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
				Log.e("TAG", "bytesRead:" + bytesRead);
				Log.e("TAG", "contentLength:" + contentLength);
				Log.e("TAG", "done:" + done);
				if (contentLength != -1) {
					//长度未知的情况下回返回-1
					Log.e("TAG", (100 * bytesRead) / contentLength + "% done");
				}
				Log.e("TAG", "================================");
				//ui层回调
				//downloadProgeress.setProgress((int) ((100 * bytesRead) / contentLength));
				//Toast.makeText(getApplicationContext(), bytesRead + " " + contentLength + " " + done, Toast.LENGTH_LONG).show();
			}
		};

		//构造请求
		final Request request1 = new Request.Builder()
				.url("http://121.41.119.107:81/test/1.doc")
				.build();

		//包装Response使其支持进度回调
		ProgressHelper.addProgressResponseListener(client, uiProgressResponseListener).newCall(request1).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				Log.e("TAG", "error ", e);
			}

			@Override
			public void onResponse(Response response) throws IOException {
				Log.e("TAG", response.body().string());
			}
		});
	}
}
