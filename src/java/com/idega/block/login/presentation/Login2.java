/*
 * $Id: Login2.java,v 1.5 2005/06/17 15:56:30 dainis Exp $
 * Created on 7.3.2005 in project com.idega.block.login
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.login.presentation;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.LoginState;
import com.idega.core.user.data.User;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectTransitional;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.ui.PasswordInput;
import com.idega.presentation.ui.TextInput;


/**
 * <p>
 * New Login component based on JSF and CSS. Will gradually replace old Login component
 * </p>
 *  Last modified: $Date: 2005/06/17 15:56:30 $ by $Author: dainis $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.5 $
 */
public class Login2 extends PresentationObjectTransitional implements ActionListener {

	public static String STYLE_CLASS_DEFAULT="login_default";
	public static String STYLE_CLASS_SINGLELINE="login_singleline";
	private static final String IW_BUNDLE_IDENTIFIER="com.idega.block.login";
	protected static final String FACET_LOGGED_IN="login_loggedin";
	protected static final String FACET_LOGGED_OUT = "login_loggedout";
	protected static final String FACET_LOGIN_FAILED = "login_login_failed";
	private boolean useSubmitLinks = false;
	
	/**
	 *
	 */
	public Login2() {
		setStyleClass(STYLE_CLASS_DEFAULT);
		setTransient(false);
	}
	
	
	protected UIComponent getLoggedInPart(IWContext iwc){
		//UIComponent layer = null;
		UIComponent layer = (UIComponent)getFacet(FACET_LOGGED_IN);
		if(layer==null){
			layer = new Layer();
			((Layer) layer).setStyleClass(getStyleClass());
			
			//Form form = new Form();
			//layer.getChildren().add(form);
			
			User user = iwc.getCurrentUser();
			Text text = new Text();
			String name = user.getName();
			text.setText(name);
			text.setStyleClass("user_name");
			layer.getChildren().add(text);
			
			String logoutText = getLocalizedString("logout_text", "Log out",iwc);
			
			String loginParameter = LoginBusinessBean.LoginStateParameter;
			String logoutParamValue = LoginBusinessBean.LOGIN_EVENT_LOGOFF;

			Parameter param = new Parameter(loginParameter,"");
			
//			SubmitButton sbutton = new SubmitButton(logoutText,LoginBusinessBean.LoginStateParameter,LoginBusinessBean.LOGIN_EVENT_LOGOFF);
			PresentationObject formSubmitter = null;
			if(!getUseSubmitLinks()){
				GenericButton gbutton = new GenericButton("logoutbutton",logoutText);

				gbutton.setOnClick("this.form.elements['"+loginParameter+"'].value='"+logoutParamValue+"';this.form.submit();");
				formSubmitter = gbutton;
			} else {
				Link l = new Link();
				l.setName("logoutbutton");
				l.setText(logoutText);
				l.setURL("#");
				
				String formRef = "this.form";
				Form parentForm = getParentForm();
				if(parentForm != null){
					formRef = "document.forms['"+parentForm.getID()+"']";
				}
				l.setOnClick(formRef+".elements['"+loginParameter+"'].value='"+logoutParamValue+"';"+formRef+".submit();return false;");
				formSubmitter = l;
			}
			

			formSubmitter.setStyleClass("logout_button");
			
			layer.getChildren().add(param);
			layer.getChildren().add(formSubmitter);
			//layer.getChildren().add(sbutton);
			//layer.add(button);			
			getFacets().put(FACET_LOGGED_IN,layer);

			/*HtmlOutputText hText = new HtmlOutputText();
			hText.setValue(name);

			layer.getChildren().add(hText);

			UICommand cbutton = new HtmlCommandButton();
			cbutton.setValue("Logout");
			cbutton.addActionListener(this);

			layer.getChildren().add(cbutton);*/
			
		}
		return layer;
	}
	
	
	protected UIComponent getLoggedOutPart(IWContext iwc){

		UIComponent layer = (UIComponent)getFacet(FACET_LOGGED_OUT);
		if(layer==null){
			layer = new Layer();
			((Layer) layer).setStyleClass(getStyleClass());
			
			TextInput login = new TextInput(LoginBusinessBean.PARAMETER_USERNAME);

			PasswordInput passw = new PasswordInput(LoginBusinessBean.PARAMETER_PASSWORD);
			
			String userText = getLocalizedString("user", "User",iwc);
			String passwordText = getLocalizedString("password", "Password",iwc);
			
			Label loginTexti = new Label(userText,login);
			Label passwordTexti = new Label(passwordText,passw);
			
			String loginParameter = LoginBusinessBean.LoginStateParameter;
			String loginParamValue = LoginBusinessBean.LOGIN_EVENT_LOGIN;
			Parameter param = new Parameter(loginParameter,"");
			
			PresentationObject formSubmitter = null;
//			SubmitButton button = new SubmitButton("login_button", getLocalizedString("login_text", "Login",iwc));
			if(!getUseSubmitLinks()){
				GenericButton gbutton = new GenericButton("login_button",getLocalizedString("login_text", "Login",iwc));
				
				gbutton.setOnClick("this.form.elements['"+loginParameter+"'].value='"+loginParamValue+"';this.form.submit();");
				formSubmitter = gbutton;
			} else {
				Link l = new Link();
				l.setName("login_button");
				l.setText(getLocalizedString("login_text", "Login",iwc));
				l.setURL("#");
				
				String formRef = "this.form";
				Form parentForm = getParentForm();
				if(parentForm != null){
					formRef = "document.forms['"+parentForm.getID()+"']";
				}
				l.setOnClick(formRef+".elements['"+loginParameter+"'].value='"+loginParamValue+"';"+formRef+".submit();return false;");
				formSubmitter = l;
			}
			loginTexti.setStyleClass("user_name_text");
			login.setStyleClass("user_name_input");
			
			passwordTexti.setStyleClass("password_text");
			passw.setStyleClass("password_input");

			formSubmitter.setStyleClass("login_button");
			
			layer.getChildren().add(loginTexti);
			layer.getChildren().add(login);
			
			layer.getChildren().add(passwordTexti);
			layer.getChildren().add(passw);
			
			layer.getChildren().add(param);
			
			layer.getChildren().add(formSubmitter);
		
			getFacets().put(FACET_LOGGED_OUT,layer);
			
		}
		return layer;
	}
	
	protected UIComponent getLoginFailedPart(IWContext iwc, String message){
	
		UIComponent layer = (UIComponent)getFacet(FACET_LOGIN_FAILED);
		if(layer==null){
			layer = new Layer();
			((Layer) layer).setStyleClass(getStyleClass());
			
			String loginParameter = LoginBusinessBean.LoginStateParameter;
			String logoutParamValue = LoginBusinessBean.LOGIN_EVENT_TRYAGAIN ; 

			Parameter param = new Parameter(loginParameter,"");
			
			Text t = new Text(message);
			t.setStyleClass("error_message");
			layer.getChildren().add(t);	
			
			PresentationObject formSubmitter = null;
			if(!getUseSubmitLinks()){
				GenericButton gbutton = new GenericButton("retrybutton", getLocalizedString("tryagain_text", "Try again",iwc));

				gbutton.setOnClick("this.form.elements['"+loginParameter+"'].value='"+logoutParamValue+"';this.form.submit();");
				formSubmitter = gbutton;
			} else {
				Link l = new Link();
				l.setName("retrybutton");
				l.setText(getLocalizedString("tryagain_text", "Try again",iwc));
				l.setURL("#");
				
				String formRef = "this.form";
				Form parentForm = getParentForm();
				if(parentForm != null){
					formRef = "document.forms['"+parentForm.getID()+"']";
				}
				l.setOnClick(formRef+".elements['"+loginParameter+"'].value='"+logoutParamValue+"';"+formRef+".submit();return false;");
				formSubmitter = l;
			}
			formSubmitter.setStyleClass("retry_button");			
			
			layer.getChildren().add(param);
			layer.getChildren().add(formSubmitter);
			
			getFacets().put(FACET_LOGIN_FAILED, layer);
		}
		return layer;
	}
	

	public String getBundleIdentifier(){
		return IW_BUNDLE_IDENTIFIER;
	}
	

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectTransitional#encodeChildren(javax.faces.context.FacesContext)
	 */
	public void encodeChildren(FacesContext context) throws IOException {		
		super.encodeChildren(context);
		IWContext iwc = IWContext.getIWContext(context);
		if(iwc.isLoggedOn()){
			UIComponent loggedInPart = getLoggedInPart(iwc);
			renderChild(context,loggedInPart);
		}
		else {
			//LoginBusinessBean.internalGetState(iwc);
			
			//TODO login state in core via 
			//com.idega.core.accesscontrol.business.LoginBusinessBean.internalGetState(iwc)			
			//and decide what to do	
			
			LoginState state = LoginBusinessBean.internalGetState(iwc);
			if(state.equals(LoginState.LoggedOut)){
				UIComponent loggedOutPart = getLoggedOutPart(iwc);
				renderChild(context,loggedOutPart);
			}
			else { 
				IWResourceBundle iwrb = getResourceBundle(iwc);
				
				UIComponent loginFailedPart = null;
								
				if(state.equals(LoginState.Failed)){
					loginFailedPart = getLoginFailedPart(iwc, iwrb.getLocalizedString("login_failed", "Login failed"));
				}
				else if(state.equals(LoginState.NoUser)){
					loginFailedPart = getLoginFailedPart(iwc, iwrb.getLocalizedString("login_no_user", "Invalid user"));
				}
				else if(state.equals(LoginState.WrongPassword)){
					loginFailedPart = getLoginFailedPart(iwc, iwrb.getLocalizedString("login_wrong", "Invalid password"));
				}
				else if(state.equals(LoginState.Expired)){
					loginFailedPart = getLoginFailedPart(iwc, iwrb.getLocalizedString("login_expired", "Login expired"));
				}
				
				renderChild(context,loginFailedPart);
			}	

		}
		
	}

	/**
	 * <p>
	 * Sets the layout to be single line (like in the old Login module)
	 * </p>
	 */
	public void setLayoutSingleline(){
		setStyleClass(STYLE_CLASS_SINGLELINE);
	}

	/* (non-Javadoc)
	 * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
	 */
	public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
			//LoginBusinessBean.internalGetState()
		
		/*UIComponent component = actionEvent.getComponent();
		boolean isLoggingoff=true;*/
	}
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#restoreState(javax.faces.context.FacesContext, java.lang.Object)
	 */
	public void restoreState(FacesContext context, Object state) {
		Object[] value = (Object[])state;
		super.restoreState(context, value[0]);
		useSubmitLinks = ((Boolean)value[1]).booleanValue();
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#saveState(javax.faces.context.FacesContext)
	 */
	public Object saveState(FacesContext context) {
		Object[] state = new Object[2];
		state[0] = super.saveState(context);
		state[1] = Boolean.valueOf(useSubmitLinks);
		return state;
	}

	public boolean getUseSubmitLinks() {
		return useSubmitLinks;
	}
	public void setUseSubmitLinks(boolean useSubmitLinks) {
		this.useSubmitLinks = useSubmitLinks;
		//TODO: rather have one facet for button and one for link and decide when rendering which to render
		//Now this clears all facets so that all states will be built again.
		getFacets().clear();
	}
}
