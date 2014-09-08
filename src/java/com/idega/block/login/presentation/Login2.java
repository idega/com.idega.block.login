/*
 * $Id: Login2.java,v 1.41 2009/01/23 15:18:26 laddi Exp $ Created on 7.3.2005
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
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.idega.block.login.bean.LoginBean;
import com.idega.block.login.remote.RemoteLoginService;
import com.idega.block.web2.business.JQuery;
import com.idega.block.web2.business.Web2Business;
import com.idega.builder.bean.AdvancedProperty;
import com.idega.business.IBOLookup;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.LoginLock;
import com.idega.core.accesscontrol.business.LoginState;
import com.idega.core.accesscontrol.data.bean.LoginInfo;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.builder.data.ICPage;
import com.idega.facelets.ui.FaceletComponent;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.servlet.filter.IWAuthenticator;
import com.idega.user.business.UserBusiness;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>
 * New Login component based on JSF and CSS. Will gradually replace old Login
 * component
 * </p>
 * Last modified: $Date: 2009/01/23 15:18:26 $ by $Author: laddi $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.41 $
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
	private boolean redirectLoggedInUserToPrimaryGroupHomePage = false;
	private boolean redirectLoggedInUserToUrlToRedirectToOnLogon = false;
	private boolean sendToHttps = false;
	private String urlToRedirectToOnLogon = null;
	private String urlToRedirectToOnLogoff = null;
	private String urlToRedirectToOnLogonFailed = null;
	private Map<String, String> extraLogonParameters = new HashMap<String, String>();
	private Map<String, String> extraLogoffParameters = new HashMap<String, String>();
	private boolean allowCookieLogin = false;
	private boolean focusOnLoad = false;
	private String styleClass = "";
	private String buttonStyleClass = "";

	private String unAuthenticatedFaceletPath;
	private String authenticatedFaceletPath;
	private String authenticationFailedFaceletPath;

	public static final String LOGIN_SCRIPT = "javascript/LoginHelper.js";
	public static final String USER_BUSINESS_DWR_SCRIPT = "/dwr/interface/UserBusiness.js";

	@Autowired
	private Web2Business web2;

	@Autowired
	private JQuery jQuery;

	public Login2() {
		setStyleClass(STYLE_CLASS_MAIN_DEFAULT);
		setTransient(false);
	}

	protected UIComponent getLoggedInPart(FacesContext context, LoginBean bean) {
		IWContext iwc = IWContext.getIWContext(context);

		try {
			BuilderService service = BuilderServiceFactory.getBuilderService(iwc);
			bean.setPasswordChangerURL(service.getUriToObject(UserPasswordChanger.class, new ArrayList<AdvancedProperty>()));
		} catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}

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

		boolean hiddenParamAdded = false;
		bean.setAllowCookieLogin(allowCookieLogin);
		bean.setAction(iwc.getRequestURI(sendToHttps));
		bean.addParameter(LoginBusinessBean.LoginStateParameter, LoginBusinessBean.LOGIN_EVENT_LOGIN);
		if (this.redirectUserToPrimaryGroupHomePage) {
			bean.addParameter(IWAuthenticator.PARAMETER_REDIRECT_USER_TO_PRIMARY_GROUP_HOME_PAGE, Boolean.TRUE.toString());
		} else if (getURLToRedirectToOnLogon() != null) {
			bean.addParameter(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON, getURLToRedirectToOnLogon());
		} else if (iwc.isParameterSet(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON)) {
			bean.addParametersFromRequestToHiddenParameters(iwc.getRequest());
			hiddenParamAdded = true;
		}

		//Redirect if login fails
		if (getURLToRedirectToOnLogonFailed() != null) {
			bean.addParameter(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON_FAILED, getURLToRedirectToOnLogonFailed());
		} else if (!hiddenParamAdded && iwc.isParameterSet(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON_FAILED)) {
			bean.addParametersFromRequestToHiddenParameters(iwc.getRequest());
			hiddenParamAdded = true;
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

		if (iwc.isParameterSet(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON) || iwc.isParameterSet(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON_FAILED)) {
			bean.addParametersFromRequestToHiddenParameters(iwc.getRequest());
		} else if (getURLToRedirectToOnLogonFailed() != null) {
			bean.addParameter(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON_FAILED, getURLToRedirectToOnLogonFailed());
		}

		bean.addParameter(LoginBusinessBean.LoginStateParameter, LoginBusinessBean.LOGIN_EVENT_TRYAGAIN);
		bean.setOutput(message);

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


		if (redirectLoggedInUserToUrlToRedirectToOnLogon) {
			if (iwc.isLoggedOn()) {
				iwc.sendRedirect(getURLToRedirectToOnLogon());
				return;
			}
		}


		if (redirectLoggedInUserToPrimaryGroupHomePage) {
			if (iwc.isLoggedOn()) {
				try {
					BuilderService builderService = IBOLookup.getServiceInstance(iwc, BuilderService.class);
					if (!builderService.isBuilderApplicationRunning(iwc)) {
						UserBusiness business = IBOLookup.getServiceInstance(iwc, UserBusiness.class);
						ICPage page = business.getHomePageForUser(iwc.getCurrentUser());
						iwc.sendRedirect(CoreConstants.PAGES_URI_PREFIX + page.getDefaultPageURI());
						return;
					}
				} catch (Exception e) {
					throw new IBORuntimeException(e);
				}
			}
		}


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
		bean.setButtonStyleClass(getButtonStyleClass());
		bean.setLocaleStyle(getCurrentLocaleLanguage(iwc));

		IWBundle bundle = getBundle(context, getBundleIdentifier());

		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, getJQuery().getBundleURIToJQueryLib());
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, bundle.getVirtualPathWithFileNameString("javascript/login.js"));
		IWResourceBundle iwrb = bundle.getResourceBundle(iwc);
		String action = "LoginHelper.errorMessage = '" + iwrb.getLocalizedString("login.error_logging_in", "Error logging in: make sure user name and password are entered!") +
			"'; LoginHelper.loggingMessage = '" + iwrb.getLocalizedString("login.logging_in", "Logging in...") + "'; LoginHelper.useSubmitLinks = " + useSubmitLinks;
		if (CoreUtil.isSingleComponentRenderingProcess(iwc)) {
			Layer script = new Layer();
			add(script);
			script.add(PresentationUtil.getJavaScriptAction(action));
		} else
			PresentationUtil.addJavaScriptActionToBody(iwc, action);

		String cssFile = getBundle(context, getBundleIdentifier()).getVirtualPathWithFileNameString("style/login.css");
		if (!PresentationUtil.addStyleSheetToHeader(iwc, cssFile)) {
			Layer cssContainer = new Layer();
			cssContainer.setID("cssContainer");
			cssContainer.add(PresentationUtil.getStyleSheetSourceLine(cssFile));
			add(cssContainer);
		}

		Map<?, ?> remoteLogins = null;
		try {
			remoteLogins = WebApplicationContextUtils.getWebApplicationContext(iwc.getServletContext()).getBeansOfType(RemoteLoginService.class);
		} catch(Exception e) {}

		if (iwc.isLoggedOn()) {
			LoggedOnInfo loggedOnInfo = LoginBusinessBean.getLoggedOnInfo(iwc);
			LoginInfo loginInfo = loggedOnInfo.getUserLogin().getLoginInfo();

			if (loginInfo.getAllowedToChange() && loginInfo.getChangeNextTime() && !iwc.isSuperAdmin()) {
				addLoginScriptsAndStyles(context);
			}

			add(getLoggedInPart(iwc, bean));
		} else {
			LoginState state = LoginBusinessBean.internalGetState(iwc);
			if (state.equals(LoginState.LOGGED_OUT) || state.equals(LoginState.NO_STATE)) {
				add(getLoggedOutPart(context, bean));
			} else {
				UIComponent loginFailedPart = getLoginFailedPart(context, bean, getLoginFailedByState(context, state));

				// TODO: what about wml, see Login block
				add(loginFailedPart);
			}

			if (remoteLogins != null) {
				for (Object remoteLogin: remoteLogins.values()) {
					if (remoteLogin instanceof RemoteLoginService) {
						add(((RemoteLoginService) remoteLogin).getUIComponentForLogin(context));
					}
				}
			}
		}
	}

	private String getLoginFailedByState(FacesContext context, LoginState state) {
		IWContext iwc = IWContext.getIWContext(context);
		IWResourceBundle iwrb = getBundle(context, getBundleIdentifier()).getResourceBundle(iwc);

		if (state.equals(LoginState.EXPIRED)) {
			return iwrb.getLocalizedString("login_expired", "Login expired");
		} else if (state.equals(LoginState.FAILED_DISABLED_NEXT_TIME)) {
			return iwrb.getLocalizedString("login_wrong_disabled_next_time", "Invalid password, access closed next time login fails");
		} else if (state.equals(LoginState.DISABLED)) {
			return iwrb.getLocalizedString("login_blocked", "Temporarily blocked due to too many log-in attempts");
		} else {
			return iwrb.getLocalizedString("login_failed", "Login failed");
		}
	}

	private Web2Business getWeb2Business() {
		if (web2 == null)
			ELUtil.getInstance().autowire(this);
		return web2;
	}

	private JQuery getJQuery() {
		if (jQuery == null)
			ELUtil.getInstance().autowire(this);
		return jQuery;
	}

	private void addLoginScriptsAndStyles(FacesContext context) {
		IWContext iwc = IWContext.getIWContext(context);
		IWBundle iwb = getBundle(context, getBundleIdentifier());

		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, getJQuery().getBundleURIToJQueryLib());
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, getWeb2Business().getBundleURIsToFancyBoxScriptFiles());
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, CoreConstants.DWR_ENGINE_SCRIPT);
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, USER_BUSINESS_DWR_SCRIPT);
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, "/dwr/interface/LoginServices.js");
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, iwb.getVirtualPathWithFileNameString(LOGIN_SCRIPT));
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, iwb.getVirtualPathWithFileNameString("javascript/UserPasswordChanger.js"));
		PresentationUtil.addStyleSheetToHeader(iwc, getWeb2Business().getBundleURIToFancyBoxStyleFile());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
	 */
	@Override
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
		this.authenticatedFaceletPath = (String) value[7];
		this.unAuthenticatedFaceletPath = (String) value[8];
		this.authenticationFailedFaceletPath = (String) value[9];
		this.urlToRedirectToOnLogon = (String) value[10];
		this.urlToRedirectToOnLogoff = (String) value[11];
		this.styleClass = (String) value[12];
		this.extraLogonParameters = (Map) value[13];
		this.extraLogoffParameters = (Map) value[14];
		this.urlToRedirectToOnLogonFailed = (String) value[15];

		IWContext iwc = IWContext.getIWContext(context);
		LoginBean bean = getBeanInstance("loginBean");
		bean.setUseSubmitLinks(useSubmitLinks);
		bean.setStyleClass(getStyleClass());
		bean.setButtonStyleClass(getButtonStyleClass());
		bean.setLocaleStyle(getCurrentLocaleLanguage(iwc));

		if (iwc.isLoggedOn()) {
			bean.addParameter(LoginBusinessBean.LoginStateParameter, LoginBusinessBean.LOGIN_EVENT_LOGOFF);
			bean.setOutput(iwc.getCurrentUser().getName());
			if (getURLToRedirectToOnLogoff() != null) {
				bean.addParameter(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGOFF, getURLToRedirectToOnLogoff());
			}

			for (Entry<String, String> entry : extraLogoffParameters.entrySet()) {
				bean.addParameter(entry.getKey(), entry.getValue());
			}
		}
		else {
			LoginState loginState = LoginBusinessBean.internalGetState(iwc);
			if (loginState.equals(LoginState.LOGGED_OUT) || loginState.equals(LoginState.NO_STATE)) {
				boolean hiddenParamAdded = false;
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
					bean.addParametersFromRequestToHiddenParameters(iwc.getRequest());
					hiddenParamAdded = true;
				}

				//redirect if login fails
				if (getURLToRedirectToOnLogonFailed() != null) {
					bean.addParameter(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON_FAILED, getURLToRedirectToOnLogonFailed());
				} else if (!hiddenParamAdded && iwc.isParameterSet(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON_FAILED)) {
					bean.addParametersFromRequestToHiddenParameters(iwc.getRequest());
					hiddenParamAdded = true;
				}

				for (Entry<String, String> entry : extraLogonParameters.entrySet()) {
					bean.addParameter(entry.getKey(), entry.getValue());
				}
			}
			else {
				bean.addParameter(LoginBusinessBean.LoginStateParameter, LoginBusinessBean.LOGIN_EVENT_TRYAGAIN);
				bean.setOutput(getLoginFailedByState(context, loginState));
				if (iwc.isParameterSet(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON)) {
					bean.addParameter(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON, iwc.getParameter(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON));
				}
				if (iwc.isParameterSet(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON_FAILED)) {
					bean.addParameter(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON_FAILED, iwc.getParameter(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON_FAILED));
				}
			}
		}
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[16];
		state[0] = super.saveState(context);
		state[1] = Boolean.valueOf(this.useSubmitLinks);
		state[2] = Boolean.valueOf(this.generateContainingForm);
		state[3] = Boolean.valueOf(this.useSingleLineLayout);
		state[4] = Boolean.valueOf(this.redirectUserToPrimaryGroupHomePage);
		state[5] = Boolean.valueOf(this.allowCookieLogin);
		state[6] = Boolean.valueOf(this.sendToHttps);
		state[7] = this.authenticatedFaceletPath;
		state[8] = this.unAuthenticatedFaceletPath;
		state[9] = this.authenticationFailedFaceletPath;
		state[10] = this.urlToRedirectToOnLogon;
		state[11] = this.urlToRedirectToOnLogoff;
		state[12] = this.styleClass;
		state[13] = this.extraLogonParameters;
		state[14] = this.extraLogoffParameters;
		state[15] = this.urlToRedirectToOnLogonFailed;
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

	public boolean isRedirectLoggedInUserToPrimaryGroupHomePage() {
		return redirectLoggedInUserToPrimaryGroupHomePage;
	}

	public void setRedirectLoggedInUserToPrimaryGroupHomePage(boolean redirectLoggedInUserToPrimaryGroupHomePage) {
		this.redirectLoggedInUserToPrimaryGroupHomePage = redirectLoggedInUserToPrimaryGroupHomePage;
	}

	public boolean isRedirectLoggedInUserToUrlToRedirectToOnLogon() {
		return redirectLoggedInUserToUrlToRedirectToOnLogon;
	}

	public void setRedirectLoggedInUserToUrlToRedirectToOnLogon(
			boolean redirectLoggedInUserToUrlToRedirectToOnLogon) {
		this.redirectLoggedInUserToUrlToRedirectToOnLogon = redirectLoggedInUserToUrlToRedirectToOnLogon;
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

	public void setURLToRedirectToOnLogonFailed(String url) {
		this.urlToRedirectToOnLogonFailed = url;
	}

	public String getURLToRedirectToOnLogonFailed() {

		return urlToRedirectToOnLogonFailed;
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

	public String getButtonStyleClass() {
		return buttonStyleClass;
	}

	public void setButtonStyleClass(String buttonStyleClass) {
		this.buttonStyleClass = buttonStyleClass;
	}

	@Autowired(required=false)
	private LoginLock loginLock;

	protected LoginLock getLoginLock() {
		if (this.loginLock == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.loginLock;
	}
}