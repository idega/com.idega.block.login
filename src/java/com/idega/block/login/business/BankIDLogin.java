package com.idega.block.login.business;

import com.idega.block.login.bean.BankLoginInfo;

public interface BankIDLogin {

	public static final String BEAN_NAME_PREFIX = "bankIdLogin";

	public BankLoginInfo doLogin(String personalId);

	public boolean isLoggedIn(String personalId, String orderRef);

}