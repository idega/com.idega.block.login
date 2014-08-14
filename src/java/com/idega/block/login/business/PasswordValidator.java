package com.idega.block.login.business;

import com.idega.presentation.IWContext;
import com.idega.user.data.User;

public interface PasswordValidator {
	public String getPasswordError(String password, IWContext iwc,User user);
}
