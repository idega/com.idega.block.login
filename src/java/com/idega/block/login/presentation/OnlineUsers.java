/*
 * Created on Mar 27, 2004
 *
 */
package com.idega.block.login.presentation;

import java.util.Collection;
import java.util.Iterator;

import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;

/**
 * OnlineUsers
 * @author aron 
 * @version 1.0
 */
public class OnlineUsers extends Block {
	
	private boolean showNobodyText = true;
	private boolean showPersonalID = false;
	private boolean showLoginName = false;
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		add(getLoggedInUsers(iwc));
	}
	
	
	
	
	public PresentationObject getLoggedInUsers(IWContext iwc){
		IWResourceBundle iwrb = getResourceBundle(iwc);
		Table table = new Table();
		int row = 1;
		Text tUsers = new Text(iwrb.getLocalizedString("online_users","Online users:"));
		tUsers.setBold();
		table.add(tUsers,1,row++);
		Collection usersLoggedIn = LoginBusinessBean.getLoggedOnInfoCollection(iwc);
		if(usersLoggedIn!=null && !usersLoggedIn.isEmpty()){
			for (Iterator iter = usersLoggedIn.iterator(); iter.hasNext();) {
				int col = 1;
				LoggedOnInfo info = (LoggedOnInfo) iter.next();
				table.addText(info.getUser().getName(),col++,row++);
				if(showPersonalID)
					table.addText(info.getUser().getPersonalID(),col++,row++);
				if(showLoginName)
					table.addText(info.getLogin(),col++,row++);
			}
		}
		else{
			Text tNone = new Text(iwrb.getLocalizedString("nobody_loggedon","nobody"));
			table.add(tNone,1,row);
		}
		table.mergeCells(1,1,table.getColumns(),1);
		
		return table;
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#getBundleIdentifier()
	 */
	public String getBundleIdentifier() {
		return Login.IW_BUNDLE_IDENTIFIER;
	}
}
