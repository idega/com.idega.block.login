package com.idega.block.login.presentation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.idega.block.login.bean.LoginBean;
import com.idega.block.login.remote.RemoteLoginService;
import com.idega.block.web2.business.JQuery;
import com.idega.business.IBOLookup;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.LoginState;
import com.idega.core.accesscontrol.business.TwoStepLoginVerificator;
import com.idega.core.accesscontrol.data.bean.LoginInfo;
import com.idega.core.accesscontrol.data.bean.UserLogin;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICPage;
import com.idega.facelets.ui.FaceletComponent;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.servlet.filter.IWAuthenticator;
import com.idega.user.business.UserBusiness;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.map.MapUtil;
import com.idega.util.expression.ELUtil;

/**
 * Login component which is using SMS authentication
 * @author zygimantas
 *
 */
public class LoginWithSMSCode extends Login2 {

	@Autowired
	private JQuery jQuery;

	private boolean showLinkAuthByTicketSystem = false;

	public LoginWithSMSCode() {
		super();
	}

	@Override
	public void initializeComponent(FacesContext context) {
		IWContext iwc = IWContext.getIWContext(context);

		if (iwc.isLoggedOn()) {
			if (isRedirectLoggedInUserToUrlToRedirectToOnLogon()) {
				iwc.sendRedirect(getURLToRedirectToOnLogon());
				return;
			}

			if (isRedirectLoggedInUserToPrimaryGroupHomePage()) {
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

		if (getUnAuthenticatedFaceletPath() == null) {
			setUnAuthenticatedFaceletPath(getBundle(context, getBundleIdentifier()).getFaceletURI("loggedOut.xhtml"));
		}
		if (getAuthenticatedFaceletPath() == null) {
			setAuthenticatedFaceletPath(getBundle(context, getBundleIdentifier()).getFaceletURI("loggedIn.xhtml"));
		}
		if (getAuthenticationFailedFaceletPath() == null) {
			setAuthenticationFailedFaceletPath(getBundle(context, getBundleIdentifier()).getFaceletURI("loginFailed.xhtml"));
		}
		if (getSmsAuthenticationFaceletPath() == null) {
			setSmsAuthenticationFaceletPath(getBundle(context, getBundleIdentifier()).getFaceletURI("smsLogin.xhtml"));
		}

		LoginBean bean = getBeanInstance("loginBean");
		bean.setUseSubmitLinks(getUseSubmitLinks());
		bean.setStyleClass(getStyleClass());
		bean.setButtonStyleClass(getButtonStyleClass());
		bean.setLocaleStyle(getCurrentLocaleLanguage(iwc));
		bean.setShowLinkAuthByTicketSystem(isShowLinkAuthByTicketSystem());

		IWBundle bundle = getBundle(context, getBundleIdentifier());

		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, getJQuery().getBundleURIToJQueryLib());
		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, bundle.getVirtualPathWithFileNameString("javascript/login.js"));
		IWResourceBundle iwrb = bundle.getResourceBundle(iwc);
		String action = "LoginHelper.errorMessage = '" + iwrb.getLocalizedString("login.error_logging_in", "Error logging in: make sure user name, password and SMS code are entered!") +
			"'; LoginHelper.loggingMessage = '" + iwrb.getLocalizedString("login.logging_in", "Logging in...") + "'; LoginHelper.useSubmitLinks = " + getUseSubmitLinks();
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
			LoginInfo loginInfo = null;
			if (loggedOnInfo != null && loggedOnInfo.getUserLogin() != null) {
				loginInfo = loggedOnInfo.getUserLogin().getLoginInfo();
				if (loginInfo != null && loginInfo.getAllowedToChange() && loginInfo.getChangeNextTime() && !iwc.isSuperAdmin()) {
					UserLogin login = loginInfo.getUserLogin();
					String loginType = login.getLoginType();
					Integer bankCount = login.getBankCount();

					boolean changePassword = false;
					if (StringUtil.isEmpty(loginType) && bankCount == null) {
						changePassword = true;
					} else if (!"is-pki-stjr".equals(loginType) && bankCount == null) {
						changePassword = true;
					}
					if (changePassword) {
						addLoginScriptsAndStyles(context);
					}
				}
			}

			add(getLoggedInPart(iwc, bean));
		} else {
			LoginState state = LoginBusinessBean.internalGetState(iwc);
			if (state.equals(LoginState.LOGGED_OUT) || state.equals(LoginState.NO_STATE)) {
				add(getLoggedOutPart(context, bean));
			} else if (state.equals(LoginState.USER_AND_PASSWORD_EXISTS)) {
				add(getLoginWithSMSCodePart(context, bean));
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


	@Override
	protected UIComponent getLoggedOutPart(FacesContext context, LoginBean bean) {
		IWContext iwc = IWContext.getIWContext(context);

		//Logout from Ticket service
		if (iwc.isLoggedOn()) {
			logoutFromTicketSystem(iwc);
		}

		boolean hiddenParamAdded = false;
		bean.setAllowCookieLogin(getAllowCookieLogin());
		bean.setAction(iwc.getRequestURI(getSendToHTTPS()));
		bean.addParameter(LoginBusinessBean.LoginStateParameter, LoginBusinessBean.LOGIN_EVENT_SMS_LOGIN);
		if (getRedirectUserToPrimaryGroupHomePage()) {
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


		for (Entry<String, String> entry : getExtraLogonParameters().entrySet()) {
			bean.addParameter(entry.getKey(), entry.getValue());
		}

		FaceletComponent facelet = (FaceletComponent) iwc.getApplication().createComponent(FaceletComponent.COMPONENT_TYPE);
		facelet.setFaceletURI(getUnAuthenticatedFaceletPath());

		return facelet;
	}

	protected UIComponent getLoginWithSMSCodePart(FacesContext context, LoginBean bean) {
		IWContext iwc = IWContext.getIWContext(context);

		boolean hiddenParamAdded = false;
		bean.setAllowCookieLogin(getAllowCookieLogin());
		bean.setAction(iwc.getRequestURI(getSendToHTTPS()));
		bean.addParameter(LoginBusinessBean.LoginStateParameter, LoginBusinessBean.LOGIN_EVENT_FULL_WITH_SMS_LOGIN);
		if (getRedirectUserToPrimaryGroupHomePage()) {
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

		for (Entry<String, String> entry : getExtraLogonParameters().entrySet()) {
			bean.addParameter(entry.getKey(), entry.getValue());
		}

		FaceletComponent facelet = (FaceletComponent) iwc.getApplication().createComponent(FaceletComponent.COMPONENT_TYPE);
		facelet.setFaceletURI(getSmsAuthenticationFaceletPath());

		return facelet;
	}


	private JQuery getJQuery() {
		if (jQuery == null)
			ELUtil.getInstance().autowire(this);
		return jQuery;
	}

	private void logoutFromTicketSystem(IWContext iwc) {
		HttpURLConnection conn = null;
		String serverLink = iwc.getServerURL();
		String urlString = serverLink + "TicketServices/Authentication?logout=true";
		String parameter = "";

		//Setting parameter
		try {
			parameter = URLEncoder.encode("logout", "UTF-8") + "=" + URLEncoder.encode("true", "UTF-8");
		} catch (Exception e) {
			parameter = "logout=true";
		}

		try {
			URL url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();

			// Set the headers and main request parameters
			conn.setRequestProperty("SOAPAction", urlString);
			conn.setRequestProperty("Content-type", "text/xml; charset=utf-8");
		    conn.setRequestProperty("Content-Length", "" + parameter.length());
		    conn.setRequestProperty("Accept-Charset", "utf-8");
		    conn.addRequestProperty("logout", "true");
			conn.setRequestMethod("POST");
		 	conn.setDoOutput(true);
		 	conn.setDoInput(true);
		    //conn.setUseCaches (false);
		    //conn.setDefaultUseCaches (false);

			//Saving session id as cookie
		 	//Collection<TwoStepLoginVerificator> verificators = getVerificators();
			//if (!ListUtil.isEmpty(verificators)) {
			//	for (TwoStepLoginVerificator verificator: verificators) {
			//		verificator.addCookieToJarfallaSessionBean(iwc.getCookie("JSESSIONID"));
			//	}
			//}

		 	InputStream responseInputStream = url.openStream();

		 	BufferedReader rd = new BufferedReader(new InputStreamReader(responseInputStream));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
			}
			rd.close();

		 	getLogger().info("Response from Ticket WS: " + response.toString());
		} catch (Exception e) {
			getLogger().warning("Could not logout from Ticket system...");
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
	}

	private Collection<TwoStepLoginVerificator> getVerificators() {
		Map<String, TwoStepLoginVerificator> verficators = WebApplicationContextUtils.getWebApplicationContext(IWMainApplication.getDefaultIWMainApplication().getServletContext())
			.getBeansOfType(TwoStepLoginVerificator.class);
		return MapUtil.isEmpty(verficators) ? null : verficators.values();
	}

	public boolean isShowLinkAuthByTicketSystem() {
		return showLinkAuthByTicketSystem;
	}


	public void setShowLinkAuthByTicketSystem(boolean showLinkAuthByTicketSystem) {
		this.showLinkAuthByTicketSystem = showLinkAuthByTicketSystem;
	}

}