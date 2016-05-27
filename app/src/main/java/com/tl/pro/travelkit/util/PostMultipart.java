package com.tl.pro.travelkit.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tl.pro.travelkit.bean.GoodsDo;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.internet.ServerTalk;
import com.tl.pro.travelkit.internet.UrlSource;
import com.tl.pro.travelkit.util.listener.ProgressResponseListener;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
		int i = 0;
		for (String path : filePaths){
			i++;
			File file = new File(path);
			multipartBuilder.addFormDataPart("photo_" + i, file.getName(), RequestBody.create(null, file));
		}
		return multipartBuilder;
	}

	private static void addTextPart(MultipartBuilder multipartBuilder, HashMap<String, String> param) {
		for (String key : param.keySet()){
//			multipartBuilder.addFormDataPart(key, param.get(key));
			multipartBuilder.addFormDataPart(key, null, RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf8"), param.get(key)));
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

	public static List<GoodsDo> getGoods(){

		HttpURLConnection connection;
		try {
			connection = ServerConfigure.getConnection("/getGoods", ServerConfigure.Request.POST);
			ServerTalk.writeToServer(connection.getOutputStream(), "body");

			if(!(connection.getResponseCode() == ServerConfigure.SERVER_OK)){
				return null;
			}
			String serString = ServerTalk.readFromServer(connection.getInputStream());
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(serString);
			boolean jo = je.isJsonObject();
			List<GoodsDo> list = new ArrayList<>();
			if(jo){
				JsonObject dataObj = je.getAsJsonObject();
				if(dataObj.has("data")){
					JsonElement s = dataObj.get("data");
					if(s.isJsonArray()){
						JsonArray jsonArray = s.getAsJsonArray();
						System.out.println(jsonArray.toString());

						Iterator<JsonElement> it = jsonArray.iterator();
						while (it.hasNext()){
							JsonElement element = it.next();
							Gson gson = new Gson();
							GoodsDo goodsDo = gson.fromJson(element, GoodsDo.class);
							System.out.println("while--" + gson.toString());
							list.add(goodsDo);
						}

					}
					//System.out.println(goodsArray.toString());
				}
			}
			System.out.println("list size = " + list.size());

//			JSONObject dataObj = new JSONObject(serString);
//			if(!dataObj.has("data")){
//				return null;
//			}
//			JSONArray array = new JSONArray(dataObj.getJSONArray("data"));
//
//
//			L.e("PostMultpart aray = ", array.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
