package com.idega.block.login.presentation;
/**

 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

import java.util.HashMap;
import java.util.Map;
import com.idega.idegaweb.presentation.StyledIWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;

public class LoginEditorWindow extends StyledIWAdminWindow {
	
    public static String PARAM_MESSAGE="msg";
    public static String PARAM_CHANGE="chg";
    
	String msg = "";
	boolean change = false;
	
	public LoginEditorWindow() {
		super();
		setScrollbar(false);
		setWidth(170);
		setHeight(270);
		//keepFocus();
	}
	
	public void setMessage(String message){
		msg = message;
	}
	
	public void setToChangeNextTime(){
		change = true;
	}
	
	public String getURL(IWContext iwc){
	    Map parameters = new HashMap();
	    parameters.put(PARAM_MESSAGE,msg);
	    if(change){
	        parameters.put(PARAM_CHANGE,"true");
	    }
		String url = getWindowURLWithParameters(getClass(),iwc.getApplicationContext(),parameters);
		return url;
	}
	
	public void main(IWContext iwc) throws Exception {
		//debugParameters(iwc);
		LoginEditor BE = new LoginEditor();
		if(iwc.isParameterSet(PARAM_MESSAGE)){
			BE.setMessage(iwc.getParameter(PARAM_MESSAGE));
		}
		if(iwc.isParameterSet(PARAM_CHANGE))
			BE.setChangeLoginNextTime(true);
		Table T = new Table(1, 1);
		T.setAlignment(1, 1, "center");
		T.setStyleClass(MAIN_STYLECLASS);
		T.add(BE, 1, 1);
		add(T,iwc);
		addTitle(getResourceBundle(iwc).getLocalizedString("login_editor","Login Editor"),TITLE_STYLECLASS);
		//setTitle("Login Editor");
		//addTitle("Login Editor");
	}
}
