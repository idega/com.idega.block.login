/*
 * Created on Mar 27, 2004
 *
 */
package com.idega.block.login.presentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.faces.context.FacesContext;

import com.idega.block.login.bean.LoggedInUser;
import com.idega.block.login.bean.LoginBean;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.facelets.ui.FaceletComponent;
import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.IWContext;
import com.idega.user.data.bean.User;
import com.idega.util.PersonalIDFormatter;
import com.idega.util.text.Name;

public class OnlineUsers extends IWBaseComponent {
	
	public static final String COMPONENT_TYPE = "com.idega.OnlineUsers";

	public static final String IW_BUNDLE_IDENTIFIER = "com.idega.block.login";

	private String faceletPath = null;
	private String styleClass = "onlineUsers";
	
	private boolean showPersonalID = false;
	private boolean showLoginName = false;
	
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	@Override
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		
		this.faceletPath = (String) values[1];
		this.styleClass = (String) values[2];
		this.showPersonalID = ((Boolean) values[3]).booleanValue();
		this.showLoginName = ((Boolean) values[4]).booleanValue();
	}

	@Override
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[5];
		values[0] = super.saveState(ctx);
		values[1] = this.faceletPath;
		values[2] = this.styleClass;
		values[3] = Boolean.valueOf(this.showPersonalID);
		values[4] = Boolean.valueOf(this.showLoginName);
		
		return values;
	}

	@Override
	public void initializeComponent(FacesContext context) {
		IWContext iwc = IWContext.getIWContext(context);

		if (getFaceletPath() == null) {
			setFaceletPath(getBundle(context, getBundleIdentifier()).getFaceletURI("onlineUsers.xhtml"));
		}

		LoginBean bean = getBeanInstance("loginBean");
		bean.setStyleClass(getStyleClass());
		bean.setShowLogin(isShowLoginName());
		bean.setShowPersonalID(isShowPersonalID());

		Collection<LoggedInUser> users = new ArrayList<LoggedInUser>();
		Collection<LoggedOnInfo> loginInfo = LoginBusinessBean.getLoggedOnInfoCollection(iwc);
		
		Iterator<LoggedOnInfo> it = loginInfo.iterator();
		while (it.hasNext()) {
			LoggedOnInfo info = it.next();
			User user = info.getUser();
			
			LoggedInUser loggedInUser = new LoggedInUser();
			loggedInUser.setName(new Name(user.getFirstName(), user.getMiddleName(), user.getLastName()).getName(iwc.getCurrentLocale()));
			loggedInUser.setPersonalID(user.getPersonalID() != null ? PersonalIDFormatter.format(user.getPersonalID(), iwc.getCurrentLocale()) : "-");
			loggedInUser.setLogin(info.getLogin());
			
			users.add(loggedInUser);
		}
		
		bean.setLoggedIn(users);
		
		FaceletComponent facelet = (FaceletComponent) iwc.getApplication().createComponent(FaceletComponent.COMPONENT_TYPE);
		facelet.setFaceletURI(getFaceletPath());
		add(facelet);
	}	
	
	private String getFaceletPath() {
		return faceletPath;
	}

	public void setFaceletPath(String faceletPath) {
		this.faceletPath = faceletPath;
	}

	private String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	private boolean isShowPersonalID() {
		return showPersonalID;
	}

	public void setShowPersonalID(boolean showPersonalID) {
		this.showPersonalID = showPersonalID;
	}

	private boolean isShowLoginName() {
		return showLoginName;
	}

	public void setShowLoginName(boolean showLoginName) {
		this.showLoginName = showLoginName;
	}
}