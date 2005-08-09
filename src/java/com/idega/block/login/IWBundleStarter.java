package com.idega.block.login;

import com.idega.block.login.presentation.Login2;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.include.GlobalIncludeManager;

public class IWBundleStarter implements IWBundleStartable {

	public void start(IWBundle arg0) {
		GlobalIncludeManager includeManager = GlobalIncludeManager.getInstance();
		includeManager.addBundleStyleSheet(Login2.IW_BUNDLE_IDENTIFIER, "/style/login.css");
	}

	public void stop(IWBundle arg0) {
	}

}
