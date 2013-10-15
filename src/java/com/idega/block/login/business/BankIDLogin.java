package com.idega.block.login.business;

public interface BankIDLogin {

	public static final String BEAN_NAME_PREFIX = "bankIdLogin";

	public boolean doLogin(String personalId);

}