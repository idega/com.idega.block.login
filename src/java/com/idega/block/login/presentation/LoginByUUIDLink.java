/*
 * $Id: LoginByUUIDLink.java,v 1.6.2.1 2006/11/20 14:24:36 eiki Exp $
 * Created on Feb 7, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.login.presentation;

import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;
import com.idega.user.data.User;


/**
 * 
 *  Last modified: $Date: 2006/11/20 14:24:36 $ by $Author: eiki $
 * 
 * Creates a link with the necessery parameters to login to another IdegaWeb system via a users UUID.
 * The receiving server must allow you to login via UUID for it to work. See LoginBusinessBean loginByUUID javadoc.
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.6.2.1 $
 */
public class LoginByUUIDLink extends Link {

	private boolean useCurrentUsersUUID = true;
	private String uuid = null;
	/**
	 * 
	 */
	public LoginByUUIDLink() {
		super();
	}

	public void setUUID(String uuid){
		if(uuid!=null){
			this.uuid = uuid;
			addParameter(LoginBusinessBean.PARAM_LOGIN_BY_UNIQUE_ID,this.uuid);
			addParameter(LoginBusinessBean.LoginStateParameter,LoginBusinessBean.LOGIN_EVENT_LOGIN);
		}
	}
	
	/**
	 * is true by default
	 * @param useCurrentUsersUUID
	 */
	public void setToUseCurrentUsersUUID(boolean useCurrentUsersUUID){
		this.useCurrentUsersUUID = useCurrentUsersUUID;
	}
	
	public boolean getUseCurrentUsersUUID(){
		return this.useCurrentUsersUUID;
	}
		
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#print(com.idega.presentation.IWContext)
	 */
	public void print(IWContext iwc) throws Exception {
		if(this.uuid==null){
			try {
				User user = iwc.getCurrentUser();
				setUUID(user.getUniqueId());
			}
			catch (NotLoggedOnException e) {
				//do nothing
			}
		}
		super.print(iwc);
	}
}
