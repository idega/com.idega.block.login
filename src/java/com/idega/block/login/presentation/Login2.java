/*
 * $Id: Login2.java,v 1.1 2005/03/09 02:12:37 tryggvil Exp $
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
import com.idega.core.user.data.User;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObjectTransitional;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;


/**
 * <p>
 * New Login component based on JSF and CSS. Will gradually replace old Login component
 * </p>
 *  Last modified: $Date: 2005/03/09 02:12:37 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class Login2 extends PresentationObjectTransitional implements ActionListener {

	public static String STYLE_CLASS_DEFAULT="login_default";
	public static String STYLE_CLASS_SINGLELINE="login_singleline";
	private static final String IW_BUNDLE_IDENTIFIER="com.idega.block.login";
	protected static final String FACET_LOGGED_IN="login_loggedin";
	
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
			
			Form form = new Form();
			layer.getChildren().add(form);
			
			User user = iwc.getCurrentUser();
			Text text = new Text();
			String name = user.getName();
			text.setText(name);
			form.add(text);
			
			String logoutText = getLocalizedString("logout_text", "Log out",iwc);
			
			SubmitButton sbutton = new SubmitButton(logoutText,LoginBusinessBean.LoginStateParameter,LoginBusinessBean.LOGIN_EVENT_LOGOFF);
			form.add(sbutton);
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

	public String getBundleIdentifier(){
		return IW_BUNDLE_IDENTIFIER;
	}
	

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectTransitional#encodeChildren(javax.faces.context.FacesContext)
	 */
	public void encodeChildren(FacesContext context) throws IOException {
		// TODO Auto-generated method stub
		super.encodeChildren(context);
		IWContext iwc = IWContext.getIWContext(context);
		if(iwc.isLoggedOn()){
			UIComponent loggedInPart = getLoggedInPart(iwc);
			renderChild(context,loggedInPart);
		}
		else{
			//TODO: implent logged out state
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
		
		UIComponent component = actionEvent.getComponent();
		boolean isLoggingoff=true;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#restoreState(javax.faces.context.FacesContext, java.lang.Object)
	 */
	public void restoreState(FacesContext context, Object state) {
		super.restoreState(context, state);
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#saveState(javax.faces.context.FacesContext)
	 */
	public Object saveState(FacesContext context) {
		return super.saveState(context);
	}

}
