package com.tl.pro.travelkit.internet;

/**
 * 封装服务器到客户端的数据
 * Created by Administrator on 2016/5/5.
 */
public class ServerInfoObj<T> {

	private int errorCode;
	private Boolean operateSuccess;
	private String digestMessage;
	private T data;

	public int getErrorCode () {

		return errorCode;
	}

	public void setErrorCode (int errorCode) {

		this.errorCode = errorCode;
	}

	public String getDigestMessage () {

		return digestMessage;
	}

	public void setDigestMessage (String digestMessage) {

		this.digestMessage = digestMessage;
	}

	public Boolean getOperateSuccess () {

		return operateSuccess;
	}
	public Boolean isSuccess(){
		return this.operateSuccess;
	}
	public void setOperateSuccess (Boolean operateSuccess) {

		this.operateSuccess = operateSuccess;
	}

	public T getData () {

		return data;
	}

	public void setData (T data) {

		this.data = data;
	}
}
