package com.tl.pro.travelkit.internet;

/**
 * Created by Administrator on 2016/5/29.
 */
public enum RequestMethod {

	POST("POST"), GET("GET");

	public String method;

	private RequestMethod(String method) {
		this.method = method;
	}

	@Override
	public String toString() {
		return "in " + this.getClass().getName() + ": " + method;
	}
}
