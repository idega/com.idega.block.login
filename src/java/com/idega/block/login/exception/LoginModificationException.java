/*
 * Created on 16.7.2003
 * 
 * Copyright (C) 2003-2007 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 * 
 */
package com.idega.block.login.exception;

import com.idega.block.login.presentation.UserRegistration;

/**
 * <p>
 * Exception thrown when updating or creating a Login/Password for a User.
 * This is a refactoring of the older LoginForgotException
 * </p>
 * Last modified: $Date: 2007/06/28 14:38:32 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>, 
 *   		<a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1.2.1 $
 */
public class LoginModificationException extends Exception {
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 5405210791672307994L;
	public static final int CODE_INIT = UserRegistration.INIT;
//	public static final int NORMAL = Forgot.NORMAL;
	public static final int CODE_USER_NAME_EXISTS = UserRegistration.USER_NAME_EXISTS;
	public static final int CODE_ILLEGAL_USERNAME = UserRegistration.ILLEGAL_USERNAME;
	public static final int CODE_ILLEGAL_EMAIL = UserRegistration.ILLEGAL_EMAIL;
	public static final int CODE_NO_NAME = UserRegistration.NO_NAME;
	public static final int CODE_NO_EMAIL = UserRegistration.NO_EMAIL;
	public static final int CODE_NO_USERNAME = UserRegistration.NO_USERNAME;
	public static final int CODE_NO_SERVER = UserRegistration.NO_SERVER;
	public static final int CODE_NO_LETTER = UserRegistration.NO_LETTER;
	public static final int CODE_ERROR = UserRegistration.ERROR;
	public static final int CODE_NO_LOGIN = UserRegistration.NO_LOGIN;
//	public static final int SENT = Forgot.SENT;
	
	int _code = CODE_INIT;
	
	/**
	 * Constuct by code defined as a constant
	 */
	public LoginModificationException(int code) {
		super();
		this._code = code;
	}
	
	public int getCode(){
		return this._code;
	}


}
