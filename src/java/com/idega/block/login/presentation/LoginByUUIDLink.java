/*
 * $Id: LoginByUUIDLink.java,v 1.2 2005/02/08 10:58:11 eiki Exp $
 * Created on Feb 7, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.login.presentation;

import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;
import com.idega.user.data.User;


/**
 * 
 *  Last modified: $Date: 2005/02/08 10:58:11 $ by $Author: eiki $
 * 
 * Creates a link with the necessery parameters to login to another IdegaWeb system via a users UUID.
 * The receiving server must allow you to login via UUID for it to work. See LoginBusinessBean loginByUUID javadoc.
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.2 $
 */
public class LoginByUUIDLink extends Link {

	boolean useCurrentUsersUUID = false;
	/**
	 * 
	 */
	public LoginByUUIDLink() {
		super();
	}

	public void setUUID(String uuid){
		if(uuid!=null){
			addParameter(LoginBusinessBean.PARAM_LOGIN_BY_UNIQUE_ID,uuid);
			addParameter(LoginBusinessBean.LoginStateParameter,"login");
		}
	}
	
	public void setToUseCurrentUsersUUID(boolean useCurrentUsersUUID){
		this.useCurrentUsersUUID = useCurrentUsersUUID;
	}
	
	
		
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#print(com.idega.presentation.IWContext)
	 */
	public void print(IWContext iwc) throws Exception {
		if(useCurrentUsersUUID){
			User user = iwc.getCurrentUser();
			setUUID(user.getUniqueId());
		}
		super.print(iwc);
	}
}
