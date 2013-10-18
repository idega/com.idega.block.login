package com.idega.block.login.bean;

import java.io.Serializable;

public class BankLoginInfo implements Serializable {

	private static final long serialVersionUID = 4227367359298152958L;

	private boolean success;

	private String orderRef;

	public BankLoginInfo() {
		super();
	}

	public BankLoginInfo(boolean success) {
		this();

		this.success = success;
	}

	public BankLoginInfo(boolean success, String orderRef) {
		this(success);

		this.orderRef = orderRef;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getOrderRef() {
		return orderRef;
	}

	public void setOrderRef(String orderRef) {
		this.orderRef = orderRef;
	}

}