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

	protected static final String IW_BUNDLE_IDENTIFIER="com.idega.block.navigation";
	private static final String WELCOME_KEY_MORNING = "welcome_message.morning";
	private static final String WELCOME_KEY_AFTERNOON = "welcome_message.afternoon";
	private static final String WELCOME_KEY_EVENING = "welcome_message.evening";

	public WelcomeMessage() {
		super("");
	}
	
	public void main(IWContext iwc) {
		User newUser = iwc.getCurrentUser();
		
		if(newUser!=null){
			try {
				IWTimestamp stamp = new IWTimestamp();
				String welcomeString = newUser.getName();
				if (stamp.getHour() < 12)
					welcomeString = getResourceBundle(iwc).getLocalizedString(WELCOME_KEY_MORNING,"Good morning") + " " + welcomeString;
				else if (stamp.getHour() < 18)
					welcomeString = getResourceBundle(iwc).getLocalizedString(WELCOME_KEY_AFTERNOON,"Good afternoon") + " " + welcomeString;
				else
					welcomeString = getResourceBundle(iwc).getLocalizedString(WELCOME_KEY_EVENING,"Good evening") + " " + welcomeString;
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
}
