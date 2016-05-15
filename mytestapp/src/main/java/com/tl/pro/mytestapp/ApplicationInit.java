package com.tl.pro.mytestapp;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Administrator on 2016/5/11.
 */
public class ApplicationInit extends Application {
    @Override
    public void onCreate() {
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        super.onCreate();
    }
}
