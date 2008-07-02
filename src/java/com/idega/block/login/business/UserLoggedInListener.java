package com.idega.block.login.business;

import com.idega.core.accesscontrol.business.AuthenticationListener;
import com.idega.core.accesscontrol.business.ServletFilterChainInterruptException;
import com.idega.presentation.IWContext;
import com.idega.user.data.User;
import com.idega.util.expression.ELUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/07/02 19:24:29 $ by $Author: civilis $
 *
 */
public class UserLoggedInListener implements AuthenticationListener {

	public String getAuthenticationListenerName() {
		return "login.UserLoggedInListener";
	}

	public void onLogoff(IWContext iwc, User lastUser)
			throws ServletFilterChainInterruptException {
	}

	public void onLogon(IWContext iwc, User loggedInUser)
			throws ServletFilterChainInterruptException {
	
		UserLoggedInEvent ev = new UserLoggedInEvent(loggedInUser);
		ev.setIWC(iwc);
		
		ELUtil.getInstance().publishEvent(ev);
	}
}