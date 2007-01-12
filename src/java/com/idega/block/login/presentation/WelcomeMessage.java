/*
 * $Id: WelcomeMessage.java,v 1.9 2005/02/17 17:52:26 tryggvil Exp $
 * Created on 31.10.2002
 *
 * Copyright (C) 2002-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.login.presentation;

import com.idega.business.IBOLookup;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Text;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

/**
 * <p>
 * This class displays a greeting message for the currently logged in user.
 * </p>
 *  Last modified: $Date: 2005/02/17 17:52:26 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:laddi@idega.com">Thorhallur Helgason</a>,<a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.9 $
 */
public class WelcomeMessage extends Text {

	protected static final String IW_BUNDLE_IDENTIFIER="com.idega.block.login";
	private static final String WELCOME_KEY_MORNING = "welcome_message.morning";
	private static final String WELCOME_KEY_AFTERNOON = "welcome_message.afternoon";
	private static final String WELCOME_KEY_EVENING = "welcome_message.evening";
	
	private boolean iShowUserName = true;
	private boolean displayDate = false;
	private boolean displayWelcomeMessage = true;

	public WelcomeMessage() {
		super("");
	}
	
	public void main(IWContext iwc) {
		
		if(iwc.isLoggedOn()){
		  
			try {
				IWTimestamp stamp = new IWTimestamp();
				String welcomeString = "";
				if(this.displayWelcomeMessage) {
				  if (stamp.getHour() < 12) {
					welcomeString = getResourceBundle(iwc).getLocalizedString(WELCOME_KEY_MORNING,"Good morning");
				}
				else if (stamp.getHour() < 18) {
						welcomeString = getResourceBundle(iwc).getLocalizedString(WELCOME_KEY_AFTERNOON,"Good afternoon");
					}
					else {
						welcomeString = getResourceBundle(iwc).getLocalizedString(WELCOME_KEY_EVENING,"Good evening");
					} 
			  }
				if (this.iShowUserName) {
					User newUser = iwc.getCurrentUser();
					welcomeString = welcomeString + " " + newUser.getName();
				}
				if(this.displayDate) {
					IWTimestamp s = IWTimestamp.RightNow();
					String date = s.getLocaleDate(iwc.getCurrentLocale(), IWTimestamp.FULL);
					if(this.displayWelcomeMessage) {
					  welcomeString = welcomeString + Text.BREAK + date;
					}
					else {
					  welcomeString = date;
					}
					
				}
				super.setText(welcomeString);	
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected UserBusiness getUserBusiness(IWContext iwc)throws java.rmi.RemoteException{
		return (UserBusiness)IBOLookup.getServiceInstance(iwc,UserBusiness.class);
	}	
	
	public String getBundleIdentifier(){
		return IW_BUNDLE_IDENTIFIER;
	}
	
	public void setShowUserName(boolean showUserName) {
		this.iShowUserName = showUserName;
	}
	public void setShowDate(boolean showDate) {
		this.displayDate = showDate;
	}
	public void setShowWelcomeMessage(boolean showWM) {
	  this.displayWelcomeMessage = showWM;
	}
}
