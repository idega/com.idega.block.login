/**
 *
 */
package com.idega.block.login.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.block.login.LoginConstants;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;

@Service("loginBean")
@Scope("request")
public class LoginBean {

	private boolean useSubmitLinks = false;
	private boolean generateContainingForm = true;
	private boolean allowCookieLogin = false;
	private String styleClass = null;
	private String buttonStyleClass = null;
	private String action = null;
	private Map<String, String> parameters = new HashMap<String, String>();
	private String defaultOutput;
	private String localeStyle;
	private boolean showLinkAuthByTicketSystem = false;


	private Collection<LoggedInUser> loggedIn;
	private boolean showPersonalID = false;
	private boolean showLogin = false;

	private String passwordChangerURL;

	/**
	 * @return the useSubmitLinks
	 */
	public boolean isUseSubmitLinks() {
		return this.useSubmitLinks;
	}

	/**
	 * @param useSubmitLinks
	 *          the useSubmitLinks to set
	 */
	public void setUseSubmitLinks(boolean useSubmitLinks) {
		this.useSubmitLinks = useSubmitLinks;
	}

	/**
	 * @return the generateContainingForm
	 */
	public boolean isGenerateContainingForm() {
		return this.generateContainingForm;
	}

	/**
	 * @param generateContainingForm
	 *          the generateContainingForm to set
	 */
	public void setGenerateContainingForm(boolean generateContainingForm) {
		this.generateContainingForm = generateContainingForm;
	}

	/**
	 * @return the allowCookieLogin
	 */
	public boolean isAllowCookieLogin() {
		return this.allowCookieLogin;
	}

	/**
	 * @param allowCookieLogin
	 *          the allowCookieLogin to set
	 */
	public void setAllowCookieLogin(boolean allowCookieLogin) {
		this.allowCookieLogin = allowCookieLogin;
	}

	/**
	 * @return the styleClass
	 */
	public String getStyleClass() {
		return this.styleClass;
	}

	/**
	 * @param styleClass
	 *          the styleClass to set
	 */
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getButtonStyleClass() {
		return buttonStyleClass;
	}

	public void setButtonStyleClass(String buttonStyleClass) {
		this.buttonStyleClass = buttonStyleClass;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * @param action
	 *          the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	public void addParameter(String parameter, String value) {
		parameters.put(parameter, value);
	}

	public Parameter[] getHiddenParameters() {
		List<Parameter> parametersList = new ArrayList<Parameter>(parameters.size());
		for (String parameter: this.parameters.keySet()) {
			parametersList.add(new Parameter(parameter, parameters.get(parameter)));
		}
		return ArrayUtil.convertListToArray(parametersList);
	}

	public String removeParameter(String parameter){
		return parameters.remove(parameter);
	}

	public String getUriByHiddenParameters() {
		Parameter[] parameters = getHiddenParameters();
		if (ArrayUtil.isEmpty(parameters)) {
			return CoreConstants.EMPTY;
		}

		Parameter param = null;
		StringBuilder uri = new StringBuilder();
		for (Iterator<Parameter> paramsIter = Arrays.asList(parameters).iterator(); paramsIter.hasNext();) {
			param = paramsIter.next();

			uri.append(param.getParameter()).append(CoreConstants.EQ).append(param.getValue());
			if (paramsIter.hasNext()) {
				uri.append(CoreConstants.AMP);
			}
		}

		return uri.toString();
	}

	public class Parameter {

		String parameter;
		String value;

		public Parameter(String parameter, String value) {
			this.parameter = parameter;
			this.value = value;
		}

		public String getParameter() {
			return parameter;
		}

		public String getValue() {
			return value;
		}
	}

	/**
	 * @return the output
	 */
	public String getOutput() {
		return this.defaultOutput;
	}

	/**
	 * @param output
	 *          the output to set
	 */
	public void setOutput(String output) {
		this.defaultOutput = output;
	}

	/**
	 * @return the localeStyle
	 */
	public String getLocaleStyle() {
		return this.localeStyle;
	}

	/**
	 * @param localeStyle
	 *          the localeStyle to set
	 */
	public void setLocaleStyle(String localeStyle) {
		this.localeStyle = localeStyle;
	}

	/**
	 * Adds all parameters from the request to hidden input, except internal login
	 * parameters for state, password, user name and unique id
	 * @param request
	 */
	public void addParametersFromRequestToHiddenParameters(HttpServletRequest request) {
		Map parameters = request.getParameterMap();
		ArrayList<String> excludeParam = new ArrayList<String>();
		excludeParam.add(LoginBusinessBean.PARAMETER_USERNAME);
		excludeParam.add(LoginBusinessBean.PARAMETER_PASSWORD);
		excludeParam.add(LoginBusinessBean.PARAMETER_PASSWORD2);//whatever that is...
		excludeParam.add(LoginBusinessBean.LoginStateParameter);
		excludeParam.add(LoginBusinessBean.PARAM_LOGIN_BY_UNIQUE_ID);
		excludeParam.add(LoginBusinessBean.PARAMETER_SMS_CODE);


		if (parameters != null && !parameters.isEmpty()) {
			Set<String> parametersSet = parameters.keySet();
			for (Iterator iterator = parametersSet.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				if(!excludeParam.contains(key)){
					String[] values = request.getParameterValues(key);
					if (values != null && values.length > 0) {
						for (int j = 0; j < values.length; j++) {
							this.addParameter(key,values[j]);
						}
					}
				}
			}
		}

	}

	public Collection<LoggedInUser> getLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(Collection<LoggedInUser> loggedIn) {
		this.loggedIn = loggedIn;
	}

	public boolean isShowPersonalID() {
		return showPersonalID;
	}

	public void setShowPersonalID(boolean showPersonalID) {
		this.showPersonalID = showPersonalID;
	}

	public boolean isShowLogin() {
		return showLogin;
	}

	public void setShowLogin(boolean showLogin) {
		this.showLogin = showLogin;
	}

	public String getPasswordChangerURL() {
		return passwordChangerURL;
	}

	public void setPasswordChangerURL(String passwordChangerURL) {
		this.passwordChangerURL = passwordChangerURL;
	}

	public boolean isShowLinkAuthByTicketSystem() {
		return showLinkAuthByTicketSystem;
	}

	public void setShowLinkAuthByTicketSystem(boolean showLinkAuthByTicketSystem) {
		this.showLinkAuthByTicketSystem = showLinkAuthByTicketSystem;
	}

	public String getAuthenticationServletLink() {
		return LoginConstants.TICKET_WEBSERVICE_PATH;
	}

}