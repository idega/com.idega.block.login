/*
 * Created on Mar 27, 2004
 *
 */
package com.idega.block.login.presentation;

import java.rmi.RemoteException;
import java.sql.Date;
import java.text.DateFormat;

import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Text;

/**
 * LastLogin
 * @author aron 
 * @version 1.0
 */
public class LastLogin extends Block {
	
	private String style = "";
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		if(iwc.isLoggedOn()){
			try {
				IWResourceBundle iwrb = getResourceBundle(iwc);
				String text = iwrb.getLocalizedString("last_login_text","Last login");
				DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT,iwc.getCurrentLocale());
				Date last = LoginBusinessBean.getLastLoginByUser(new Integer(iwc.getUserId()));
				Text txt = new Text(text+" "+df.format(last));
				txt.setStyleAttribute(this.style);
				add(txt);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#getBundleIdentifier()
	 */
	public String getBundleIdentifier() {
		return Login.IW_BUNDLE_IDENTIFIER;
	}
	/**
	 * @param style The style to set.
	 */
	public void setStyle(String style) {
		this.style = style;
	}
}
