package com.tl.pro.travelkit.bean;

/**
 * Created by Administrator on 2016/6/4.
 */
public class MyPage {

	private int pageNo;
	private int pageSize;
	private int startColumn;

	public int getPageNo () {

		return pageNo;
	}

	public void setPageNo (int pageNo) {

		this.pageNo = pageNo;
	}

	public int getPageSize () {

		return pageSize;
	}

	public void setPageSize (int pageSize) {

		this.pageSize = pageSize;
	}

	public int getStartColumn () {

		return startColumn;
	}

	public void setStartColumn (int startColumn) {

		this.startColumn = startColumn;
	}
}
