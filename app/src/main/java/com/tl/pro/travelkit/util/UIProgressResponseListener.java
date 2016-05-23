package com.tl.pro.travelkit.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.tl.pro.travelkit.util.listener.ProgressResponseListener;
import com.tl.pro.travelkit.util.log.L;

import java.lang.ref.WeakReference;

import cn.edu.zafu.coreprogress.listener.ProgressListener;
import cn.edu.zafu.coreprogress.listener.impl.model.ProgressModel;

/**
 * Created by Administrator on 2016/5/20.
 */
public abstract class UIProgressResponseListener implements ProgressResponseListener, ProgressListener {
	private static final int RESPONSE_UPDATE = 0x02;

	//处理UI层的Handler子类
	private static class UIHandler extends Handler {
		//弱引用
		private final WeakReference<UIProgressResponseListener> mUIProgressResponseListenerWeakReference;

		public UIHandler(Looper looper, UIProgressResponseListener uiProgressResponseListener) {
			super(looper);
			mUIProgressResponseListenerWeakReference = new WeakReference<UIProgressResponseListener>(uiProgressResponseListener);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case RESPONSE_UPDATE:
					UIProgressResponseListener uiProgressResponseListener = mUIProgressResponseListenerWeakReference.get();
					if (uiProgressResponseListener != null) {
						//获得进度实体类
						ProgressModel progressModel = (ProgressModel) msg.obj;
						//回调抽象方法
						uiProgressResponseListener.onUIResponseProgress(progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
					}
					break;
				default:
					super.handleMessage(msg);
					break;
			}
		}
	}

	//主线程Handler
	private final Handler mHandler = new UIHandler(Looper.getMainLooper(), this);

	@Override
	public void onResponseProgress(long bytesRead, long contentLength, boolean done) {
		//通过Handler发送进度消息
		Message message = Message.obtain();
		message.obj = new ProgressModel(bytesRead, contentLength, done);
		message.what = RESPONSE_UPDATE;
		mHandler.sendMessage(message);
	}
	@Override
	public void onProgress(long currentBytes, long contentLength, boolean done) {
		L.e("UI----", "onProgress" + "response");
	}

	/**
	 * UI层回调抽象方法
	 *
	 * @param bytesRead     当前读取响应体字节长度
	 * @param contentLength 总字节长度
	 * @param done          是否读取完成
	 */
	public abstract void onUIResponseProgress(long bytesRead, long contentLength, boolean done);
}
