package com.idega.block.login;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.block.login.business.UserLoggedInListener;
import com.idega.block.login.presentation.Login2;
import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.business.AuthenticationBusiness;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.include.GlobalIncludeManager;

public class IWBundleStarter implements IWBundleStartable {

	public void start(IWBundle iwb) {
		GlobalIncludeManager includeManager = GlobalIncludeManager.getInstance();
		includeManager.addBundleStyleSheet(Login2.IW_BUNDLE_IDENTIFIER, "/style/login.css");
		
		try {
			IWApplicationContext iwac = iwb.getApplication().getIWApplicationContext();
			AuthenticationBusiness authBiz = (AuthenticationBusiness) IBOLookup.getServiceInstance(iwac, AuthenticationBusiness.class);
			authBiz.addAuthenticationListener(new UserLoggedInListener());
			
		} catch (Exception e) {
		
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception while registering", e);
		}
	}

	public void stop(IWBundle arg0) {
	}

}
