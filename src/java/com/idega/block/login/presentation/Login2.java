/*
 * $Id: Login2.java,v 1.40 2008/12/12 11:15:44 laddi Exp $ Created on 7.3.2005
 * in project com.idega.block.login
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.block.login.presentation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import com.idega.block.login.bean.LoginBean;
import com.idega.block.web2.business.Web2Business;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.business.LoginState;
import com.idega.core.accesscontrol.data.LoginInfo;
import com.idega.facelets.ui.FaceletComponent;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.servlet.filter.IWAuthenticator;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.PresentationUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>
 * New Login component based on JSF and CSS. Will gradually replace old Login
 * component
 * </p>
 * Last modified: $Date: 2008/12/12 11:15:44 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.40 $
 */
public class Login2 extends IWBaseComponent implements ActionListener {
	
	public static final String COMPONENT_TYPE = "com.idega.Login";

	public static final String IW_BUNDLE_IDENTIFIER = "com.idega.block.login";

	public static String STYLE_CLASS_MAIN_DEFAULT = "login";
	public static String STYLE_CLASS_MAIN_SINGLELINE = "loginSingleline";

	private boolean useSubmitLinks = false;
	private boolean generateContainingForm = true;
	private boolean useSingleLineLayout = false;
	private boolean redirectUserToPrimaryGroupHomePage = false;
	private boolean sendToHttps = false;
	private String urlToRedirectToOnLogon = null;
	private String urlToRedirectToOnLogoff = null;
	private Map<String, String> extraLogonParameters = new HashMap<String, String>();
	private Map<String, String> extraLogoffParameters = new HashMap<String, String>();
	private boolean allowCookieLogin = false;
	private boolean focusOnLoad = false;
	private String styleClass = "";

	private String unAuthenticatedFaceletPath;
	private String authenticatedFaceletPath;
	private String authenticationFailedFaceletPath;
	
	public static final String LOGIN_SCRIPT = "javascript/LoginHelper.js";
	public static final String USER_BUSINESS_DWR_SCRIPT = "/dwr/interface/UserBusiness.js";

	/**
	 * 
	 */
	public Login2() {
		setStyleClass(STYLE_CLASS_MAIN_DEFAULT);
		setTransient(false);
	}

	protected UIComponent getLoggedInPart(FacesContext context, LoginBean bean) {
		IWContext iwc = IWContext.getIWContext(context);
		
		bean.addParameter(LoginBusinessBean.LoginStateParameter, LoginBusinessBean.LOGIN_EVENT_LOGOFF);
		bean.setOutput(iwc.getCurrentUser().getName());
		if (getURLToRedirectToOnLogoff() != null) {
			bean.addParameter(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGOFF, getURLToRedirectToOnLogoff());
		}

		for (Entry<String, String> entry : extraLogoffParameters.entrySet()) {
			bean.addParameter(entry.getKey(), entry.getValue());
		}

		FaceletComponent facelet = (FaceletComponent) iwc.getApplication().createComponent(FaceletComponent.COMPONENT_TYPE);
		facelet.setFaceletURI(authenticatedFaceletPath);	
		
		return facelet;
	}

	protected UIComponent getLoggedOutPart(FacesContext context, LoginBean bean) {
		IWContext iwc = IWContext.getIWContext(context);
		
		bean.setAllowCookieLogin(allowCookieLogin);
		bean.setAction(iwc.getRequestURI(sendToHttps));
		bean.addParameter(LoginBusinessBean.LoginStateParameter, LoginBusinessBean.LOGIN_EVENT_LOGIN);
		if (this.redirectUserToPrimaryGroupHomePage) {
			bean.addParameter(IWAuthenticator.PARAMETER_REDIRECT_USER_TO_PRIMARY_GROUP_HOME_PAGE, Boolean.TRUE.toString());
		}
		else if (getURLToRedirectToOnLogon() != null) {
			bean.addParameter(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON, getURLToRedirectToOnLogon());
		}
		else if (iwc.isParameterSet(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON)) {
			bean.addParameter(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON, iwc.getParameter(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON));
		}
		for (Entry<String, String> entry : extraLogonParameters.entrySet()) {
			bean.addParameter(entry.getKey(), entry.getValue());
		}

		FaceletComponent facelet = (FaceletComponent) iwc.getApplication().createComponent(FaceletComponent.COMPONENT_TYPE);
		facelet.setFaceletURI(unAuthenticatedFaceletPath);	
		
		return facelet;
	}

	protected UIComponent getLoginFailedPart(FacesContext context, LoginBean bean, String message) {
		IWContext iwc = IWContext.getIWContext(context);
		
		bean.addParameter(LoginBusinessBean.LoginStateParameter, LoginBusinessBean.LOGIN_EVENT_TRYAGAIN);
		bean.setOutput(message);
		if (iwc.isParameterSet(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON)) {
			bean.addParameter(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON, iwc.getParameter(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON));
		}

		FaceletComponent facelet = (FaceletComponent) iwc.getApplication().createComponent(FaceletComponent.COMPONENT_TYPE);
		facelet.setFaceletURI(authenticationFailedFaceletPath);	
		
		return facelet;
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
	
	@Override
	public void initializeComponent(FacesContext context) {
		IWContext iwc = IWContext.getIWContext(context);
		
		if (unAuthenticatedFaceletPath == null) {
			unAuthenticatedFaceletPath = getBundle(context, getBundleIdentifier()).getFaceletURI("loggedOut.xhtml");
		}
		if (authenticatedFaceletPath == null) {
			authenticatedFaceletPath = getBundle(context, getBundleIdentifier()).getFaceletURI("loggedIn.xhtml");
		}
		if (authenticationFailedFaceletPath == null) {
			authenticationFailedFaceletPath = getBundle(context, getBundleIdentifier()).getFaceletURI("loginFailed.xhtml");
		}
		
		LoginBean bean = getBeanInstance("loginBean");
		bean.setUseSubmitLinks(useSubmitLinks);
		bean.setStyleClass(getStyleClass());
		bean.setLocaleStyle(getCurrentLocaleLanguage(iwc));
		
		if (useSubmitLinks) {
			Web2Business business = ELUtil.getInstance().getBean(Web2Business.class);
			PresentationUtil.addJavaScriptSourceLineToHeader(iwc, business.getBundleURIToJQueryLib());
			PresentationUtil.addJavaScriptSourceLineToHeader(iwc, getBundle(context, getBundleIdentifier()).getVirtualPathWithFileNameString("javascript/login.js"));
		}
		
		String cssFile = getBundle(context, getBundleIdentifier()).getVirtualPathWithFileNameString("style/login.css");
		if (!PresentationUtil.addStyleSheetToHeader(iwc, cssFile)) {
			Layer cssContainer = new Layer();
			cssContainer.setID("cssContainer");
			cssContainer.add(PresentationUtil.getStyleSheetSourceLine(cssFile));
			add(cssContainer);
		}

		if (iwc.isLoggedOn()) {
			User currentUser = iwc.getCurrentUser();
			LoginInfo loginInfo = LoginDBHandler.getLoginInfo((LoginDBHandler.getUserLogin(currentUser)));

			if (loginInfo.getAllowedToChange() && loginInfo.getChangeNextTime() && !iwc.isSuperAdmin()) {
				addLoginScriptsAndStyles(context);
			}
			
			add(getLoggedInPart(iwc, bean));
		}
		else {
			LoginState state = LoginBusinessBean.internalGetState(iwc);
			if (state.equals(LoginState.LoggedOut) || state.equals(LoginState.NoState)) {
				add(getLoggedOutPart(context, bean));
			}
			else {
				IWResourceBundle iwrb = getBundle(context, getBundleIdentifier()).getResourceBundle(iwc);

				UIComponent loginFailedPart = null;

				if (state.equals(LoginState.Failed)) {
					loginFailedPart = getLoginFailedPart(context, bean, iwrb.getLocalizedString("login_failed", "Login failed"));
				}
				else if (state.equals(LoginState.NoUser)) {
					loginFailedPart = getLoginFailedPart(context, bean, iwrb.getLocalizedString("login_no_user", "Invalid user"));
				}
				else if (state.equals(LoginState.WrongPassword)) {
					loginFailedPart = getLoginFailedPart(context, bean, iwrb.getLocalizedString("login_wrong", "Invalid password"));
				}
				else if (state.equals(LoginState.Expired)) {
					loginFailedPart = getLoginFailedPart(context, bean, iwrb.getLocalizedString("login_expired", "Login expired"));
				}
				else if (state.equals(LoginState.FailedDisabledNextTime)) {
					loginFailedPart = getLoginFailedPart(context, bean, iwrb.getLocalizedString("login_wrong_disabled_next_time", "Invalid password, access closed next time login fails"));
				}

				// TODO: what about wml, see Login block
				add(loginFailedPart);
			}
		}
	}
	
	private void addLoginScriptsAndStyles(FacesContext context) {
		IWContext iwc = IWContext.getIWContext(context);
		
		List<String> scripts = new ArrayList<String>();
		List<String> css = new ArrayList<String>();
		
		Web2Business web2 = ELUtil.getInstance().getBean(Web2Business.class);
		try {
			scripts.add(web2.getBundleURIToMootoolsLib()); //Mootools
			scripts.add(web2.getMoodalboxScriptFilePath(true));	//	MOOdalBox
			scripts.add(CoreConstants.DWR_ENGINE_SCRIPT);
			scripts.add(USER_BUSINESS_DWR_SCRIPT);
			scripts.add(getBundle(context, getBundleIdentifier()).getVirtualPathWithFileNameString(LOGIN_SCRIPT));
			css.add(web2.getMoodalboxStyleFilePath());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		StringBuffer changePassScript = new StringBuffer("changeUserPassword('")
		.append(getUriToObject(UserPasswordChanger.class.getName()))
		.append("');");

		String action = PresentationUtil.getJavaScriptLinesLoadedLazily(scripts, changePassScript.toString());
		PresentationUtil.addJavaScriptActionToBody(iwc, action);
		
		PresentationUtil.addStyleSheetsToHeader(iwc, css);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
	 */
	public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
	}

	@Override
	public void restoreState(FacesContext context, Object state) {
		Object[] value = (Object[]) state;
		super.restoreState(context, value[0]);
		this.useSubmitLinks = ((Boolean) value[1]).booleanValue();
		this.generateContainingForm = ((Boolean) value[2]).booleanValue();
		this.useSingleLineLayout = ((Boolean) value[3]).booleanValue();
		this.redirectUserToPrimaryGroupHomePage = ((Boolean) value[4]).booleanValue();
		this.allowCookieLogin = ((Boolean) value[5]).booleanValue();
		this.sendToHttps = ((Boolean) value[6]).booleanValue();
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[7];
		state[0] = super.saveState(context);
		state[1] = Boolean.valueOf(this.useSubmitLinks);
		state[2] = Boolean.valueOf(this.generateContainingForm);
		state[3] = Boolean.valueOf(this.useSingleLineLayout);
		state[4] = Boolean.valueOf(this.redirectUserToPrimaryGroupHomePage);
		state[5] = Boolean.valueOf(this.allowCookieLogin);
		state[6] = Boolean.valueOf(this.sendToHttps);
		return state;
	}

	public boolean getUseSubmitLinks() {
		return this.useSubmitLinks;
	}

	public void setUseSubmitLinks(boolean useSubmitLinks) {
		this.useSubmitLinks = useSubmitLinks;
	}

	public boolean getGenerateContainingForm() {
		return this.generateContainingForm;
	}

	public void setGenerateContainingForm(boolean generateContainingForm) {
		this.generateContainingForm = generateContainingForm;
	}

	public boolean getUseSingleLineLayout() {
		return this.useSingleLineLayout;
	}

	public void setRedirectUserToPrimaryGroupHomePage(boolean redirectToHomePage) {
		this.redirectUserToPrimaryGroupHomePage = redirectToHomePage;
	}

	public void setUseSingleLineLayout(boolean useSingleLineLayout) {
		if (useSingleLineLayout) {
			setStyleClass(STYLE_CLASS_MAIN_SINGLELINE);
		}
		else {
			setStyleClass(STYLE_CLASS_MAIN_DEFAULT);
		}
	}

	/**
	 * a small helper method
	 * 
	 * @param iwc
	 * @return
	 */
	private String getCurrentLocaleLanguage(IWContext iwc) {
		return iwc.getLocale().getLanguage();
	}

	@Deprecated
	public void setShowLabelInInput(boolean showLabelInInput) {
		/* Should be handled with custom facelet... */
	}

	public void setURLToRedirectToOnLogon(String url) {
		this.urlToRedirectToOnLogon = url;
	}

	public String getURLToRedirectToOnLogon() {
		
		return urlToRedirectToOnLogon;
	}

	public void setURLToRedirectToOnLogoff(String url) {
		this.urlToRedirectToOnLogoff = url;
	}

	public String getURLToRedirectToOnLogoff() {
		return this.urlToRedirectToOnLogoff;
	}

	public void setExtraLogonParameter(String parameter, String value) {
		this.extraLogonParameters.put(parameter, value);
	}

	public void setExtraLogoffParameter(String parameter, String value) {
		this.extraLogoffParameters.put(parameter, value);
	}

	@Deprecated
	public void setEnterSubmits(boolean enterSubmits) {
		/* Is now done automatically when using submit links */
	}

	public void setSendToHTTPS(boolean sendToHttps) {
		this.sendToHttps = sendToHttps;
	}
	
	/**
	 * <p>
	 * If set to true then a checkbox is displayed that displays and enabled 
	 * the cookie/remember-me function to keep a persistent login.
	 * </p>
	 * @param cookies
	 */
	public void setAllowCookieLogin(boolean cookies) {
		this.allowCookieLogin = cookies;
	}
	
	public boolean getAllowCookieLogin(){
		return this.allowCookieLogin;
	}

	public boolean isFocusOnLoad() {
		return focusOnLoad;
	}
	
	public void setFocusOnLoad(boolean focusOnLoad) {
		this.focusOnLoad = focusOnLoad;
	}
	
	private String getUriToObject(String className) {
		if (className == null) {
			return null;
		}
		StringBuffer uri = new StringBuffer("/servlet/ObjectInstanciator?").append(IWMainApplication.classToInstanciateParameter);
		uri.append("=").append(className);
		return uri.toString();
	}
	
	private String getLocalizedString(String key, String defaultValue, IWUserContext iwuc) {
		IWResourceBundle bundle = getBundle(iwuc, getBundleIdentifier()).getResourceBundle(iwuc.getCurrentLocale());
		if (bundle != null) {
			return bundle.getLocalizedString(key, defaultValue);
		}
		return null;
	}
	
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
	private String getStyleClass() {
		return styleClass;
	}

	public void setUnAuthenticatedFaceletPath(String pathToFacelet) {
		this.unAuthenticatedFaceletPath = pathToFacelet;
	}
	
	public void setAuthenticatedFaceletPath(String pathToFacelet) {
		this.authenticatedFaceletPath = pathToFacelet;
	}
	
	public void setAuthenticationFailedFaceletPath(String pathToFacelet) {
		this.authenticationFailedFaceletPath = pathToFacelet;
	}
}