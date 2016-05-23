package com.tl.pro.travelkit.util.listener;

/**
 * Created by Administrator on 2016/5/20.
 */
public interface ProgressResponseListener {
	void onResponseProgress(long bytesRead, long contentLength, boolean done);
}
