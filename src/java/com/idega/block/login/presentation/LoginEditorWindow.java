package com.idega.block.login.presentation;
/**

 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;

public class LoginEditorWindow extends IWAdminWindow {
	
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
		String url = getWindowURL(getClass(),iwc.getApplicationContext());
		url += "&msg="+msg;
		if(change)
			url+="&chg=true";
		return url;
	}
	
	public void main(IWContext iwc) throws Exception {
		//debugParameters(iwc);
		LoginEditor BE = new LoginEditor();
		if(iwc.isParameterSet("msg"))
			BE.setMessage(iwc.getParameter("msg"));
		if(iwc.isParameterSet("chg"))
			BE.setChangeLoginNextTime(true);
		Table T = new Table(1, 1);
		T.setAlignment(1, 1, "center");
		T.add(BE, 1, 1);
		add(T);
		setTitle("Login Editor");
		//addTitle("Login Editor");
	}
}
