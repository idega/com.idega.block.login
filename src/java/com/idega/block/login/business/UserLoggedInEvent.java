package com.idega.block.login.business;

import org.springframework.context.ApplicationEvent;

import com.idega.presentation.IWContext;
import com.idega.user.data.bean.User;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/05/30 15:02:45 $ by $Author: civilis $
 *
 */
public class UserLoggedInEvent extends ApplicationEvent {

	private static final long serialVersionUID = 3750860225723036506L;

	private User loggedInUsr;
	private IWContext iwc;

	public IWContext getIWC() {
		return iwc;
	}

	public void setIWC(IWContext iwc) {
		this.iwc = iwc;
	}

	public UserLoggedInEvent(Object source) {
		super(source);

		if (source instanceof User) {
			this.loggedInUsr = (User)source;
		}
	}

	public User getLoggedInUsr() {
		return loggedInUsr;
	}

}