package com.idega.block.login.presentation;

import com.idega.business.IBOLookup;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Text;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

/**
 * @author Laddi
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class WelcomeMessage extends Text {

	protected static final String IW_BUNDLE_IDENTIFIER="com.idega.block.login";
	private static final String WELCOME_KEY_MORNING = "welcome_message.morning";
	private static final String WELCOME_KEY_AFTERNOON = "welcome_message.afternoon";
	private static final String WELCOME_KEY_EVENING = "welcome_message.evening";
	
	private boolean iShowUserName = true;
	private boolean displayDate = false;

	public WelcomeMessage() {
		super("");
	}
	
	public void main(IWContext iwc) {
		
		if(iwc.isLoggedOn()){
			try {
				IWTimestamp stamp = new IWTimestamp();
				String welcomeString = "";
				if (stamp.getHour() < 12)
					welcomeString = getResourceBundle(iwc).getLocalizedString(WELCOME_KEY_MORNING,"Good morning");
				else if (stamp.getHour() < 18)
					welcomeString = getResourceBundle(iwc).getLocalizedString(WELCOME_KEY_AFTERNOON,"Good afternoon");
				else
					welcomeString = getResourceBundle(iwc).getLocalizedString(WELCOME_KEY_EVENING,"Good evening");
				if (iShowUserName) {
					User newUser = iwc.getCurrentUser();
					welcomeString = welcomeString + " " + newUser.getName();
				}
				if(displayDate) {
					IWTimestamp s = IWTimestamp.RightNow();
					String date = s.getDateString("EEEEEEEEEEEE dd.MM.yyyy",iwc.getCurrentLocale()); 
					welcomeString = welcomeString + Text.BREAK + date;
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
	
	public void showUserName(boolean showUserName) {
		iShowUserName = showUserName;
	}
	public void showDate(boolean showDate) {
		displayDate = showDate;
	}
}
