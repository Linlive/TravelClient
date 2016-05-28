package com.tl.pro.travelkit.bean;

/**
 * Created by Administrator on 2016/5/29.
 */
public class CartDo {
	private String userId;
	private GoodsDo goodsDo;
	private int goodsCount;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public GoodsDo getGoodsDo() {
		return goodsDo;
	}

	public void setGoodsDo(GoodsDo goodsDo) {
		this.goodsDo = goodsDo;
	}

	public int getGoodsCount() {
		return goodsCount;
	}

	public void setGoodsCount(int goodsCount) {
		this.goodsCount = goodsCount;
	}
}
