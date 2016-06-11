/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.tl.pro.travelkit;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.avos.avoscloud.AVOSCloud;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ApplicationInit extends Application {

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
//		if (Constants.Config.DEVELOPER_MODE && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)) {
//			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
//			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
//		}

		super.onCreate();

		initImageLoader(getApplicationContext());
		//initMsg(this);
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();

		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app


		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}
	public static void initMsg(Context context){
		// 初始化参数依次为 this, AppId, AppKey
		AVOSCloud.initialize(context,"{{e4tGn0UE8UqWXKFnmLOTtnJm-gzGzoHsz}}","{{Ij6O5nFtdHMYBk4geihkKRXF}}");
		AVOSCloud.useAVCloudCN();
		AVOSCloud.initialize(context, "683jigxkqb10jrirelvd9vcn9ywbq2o436lfz1kngsvigm27",
				"ualzl8f8pxmryous77m3gf2z0dyhrhk6xdb7zkiu6flc0jxy");
//		ChatManager.setDebugEnabled(true);// tag leanchatlib
		AVOSCloud.setDebugLogEnabled(true);  // set false when release
//		ChatManager.getInstance().init(this);
//		ThirdPartUserUtils.setThirdPartUserProvider(new CustomUserProvider());
	}
}