package com.idega.block.login.business;

import com.idega.presentation.IWContext;
import com.idega.user.data.User;

public interface PasswordTokenEmailMessageSender {
	
	public void sendMessageForUser(User user,IWContext iwc,String passwordChangeLink);

}
