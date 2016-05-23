package com.tl.pro.travelkit.util.listener;

/**
 * Created by Administrator on 2016/5/20.
 */
public interface ProgressRequestListener {
	void onRequestProgress(long bytesWritten, long contentLength, boolean done);
}
