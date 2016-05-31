package com.tl.pro.travelkit.bean;

import java.sql.Timestamp;

/**
 * 订单数据实体
 * Created by Administrator on 2016/5/21.
 */
public class IndentDo {

	private String indentNumber;
	private String buyerId;
	private String shopKeeperId;

	private Timestamp createdTime;
	private int indentState;

	private String goodsId;
	private int goodsSize;
	private int goodsType;

	//状态值
	private String indentStateValue;

	public enum State {
		CREATE_SUCCESS(1), CREATE_FAILED(2), WAITTING_PAY(3), PAY_SUCCESS(4),  PAY_FAILED(5), REMOVED(6), DELETED(7);

		int value;
		State(){

		}
		State(int value){
			this.value = value;
		}
		public int getValue() {
			return value;
		}
	}

	public IndentDo() {

	}

	public String getIndentNumber() {

		return indentNumber;
	}

	public void setIndentNumber(String indentNumber) {

		this.indentNumber = indentNumber;
	}

	public String getBuyerId() {

		return buyerId;
	}

	public void setBuyerId(String buyerId) {

		this.buyerId = buyerId;
	}

	public String getShopKeeperId() {

		return shopKeeperId;
	}

	public void setShopKeeperId(String shopKeeperId) {

		this.shopKeeperId = shopKeeperId;
	}

	public Timestamp getCreatedTime() {

		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {

		this.createdTime = createdTime;
	}

	public int getIndentState() {

		return indentState;
	}

	public void setIndentState(int indentState) {

		this.indentState = indentState;
	}

	public String getGoodsId() {

		return goodsId;
	}

	public void setGoodsId(String goodsId) {

		this.goodsId = goodsId;
	}

	public int getGoodsSize() {

		return goodsSize;
	}

	public void setGoodsSize(int goodsSize) {

		this.goodsSize = goodsSize;
	}

	public int getGoodsType() {

		return goodsType;
	}

	public void setGoodsType(int goodsType) {

		this.goodsType = goodsType;
	}

	public String getIndentStateValue() {

		return indentStateValue;
	}

	public void setIndentStateValue(String indentStateValue) {

		this.indentStateValue = indentStateValue;
	}
}
