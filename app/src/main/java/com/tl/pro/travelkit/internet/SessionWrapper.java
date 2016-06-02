package com.tl.pro.travelkit.internet;

/**
 * 获取session
 * Created by Administrator on 2016/6/2.
 */
public class SessionWrapper {
	private String code;
	private String userId;
	private String sessionId;
	private boolean status;
	private String extraInfo;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUserId () {

		return userId;
	}

	public void setUserId (String userId) {

		this.userId = userId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public boolean isSuccess(){
		return status;
	}
}
