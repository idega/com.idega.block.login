/*
 * Created on 16.7.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.block.login.exception;

import com.idega.block.login.presentation.Forgot;

/**
 * Title:		LoginForgotException
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class LoginForgotException extends Exception {
	
	public static final int CODE_INIT = Forgot.INIT;
//	public static final int NORMAL = Forgot.NORMAL;
	public static final int CODE_USER_NAME_EXISTS = Forgot.USER_NAME_EXISTS;
	public static final int CODE_ILLEGAL_USERNAME = Forgot.ILLEGAL_USERNAME;
	public static final int CODE_ILLEGAL_EMAIL = Forgot.ILLEGAL_EMAIL;
	public static final int CODE_NO_NAME = Forgot.NO_NAME;
	public static final int CODE_NO_EMAIL = Forgot.NO_EMAIL;
	public static final int CODE_NO_USERNAME = Forgot.NO_USERNAME;
	public static final int CODE_NO_SERVER = Forgot.NO_SERVER;
	public static final int CODE_NO_LETTER = Forgot.NO_LETTER;
	public static final int CODE_ERROR = Forgot.ERROR;
	public static final int CODE_NO_LOGIN = Forgot.NO_LOGIN;
//	public static final int SENT = Forgot.SENT;
	
	int _code = CODE_INIT;
	
	/**
	 * 
	 */
	public LoginForgotException(int code) {
		super();
		_code = code;
	}
	
	public int getCode(){
		return _code;
	}


}
