package com.tl.pro.travelkit.util.image;

import android.content.Context;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/4/17.
 */
public class URLConnectImageDownloader extends BaseImageDownloader {
	public URLConnectImageDownloader(Context context) {
		super(context);
	}

	public URLConnectImageDownloader(Context context, int connectTimeout, int readTimeout) {
		super(context, connectTimeout, readTimeout);
	}

	@Override
	public InputStream getStream(String imageUri, Object extra) throws IOException {


		return super.getStream(imageUri, extra);
	}
}
