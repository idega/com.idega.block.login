/**
 * 
 */
package com.idega.block.login.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("loginBean")
@Scope("request")
public class LoginBean {

	private boolean useSubmitLinks = false;
	private boolean generateContainingForm = true;
	private boolean allowCookieLogin = false;
	private String styleClass = null;
	private String action = null;
	private Map parameters = new HashMap();
	private String defaultOutput;
	private String localeStyle;

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
		Collection coll = new ArrayList();

		Iterator iterator = parameters.keySet().iterator();
		while (iterator.hasNext()) {
			String parameter = (String) iterator.next();
			String value = (String) parameters.get(parameter);
			coll.add(new Parameter(parameter, value));
		}

		return (Parameter[]) coll.toArray(new Parameter[coll.size()]);
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
}