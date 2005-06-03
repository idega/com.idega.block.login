package com.idega.block.login.presentation;
/**
 * 
 * Title:
 * 
 * Description:
 * 
 * Copyright: Copyright (c) 2001
 * 
 * Company: idega multimedia
 * 
 * @author <a href="mailto:aron@idega.is">aron@idega.is</a>
 * 
 * @version 1.0
 *  
 */
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.repository.data.RefactorClassRegistry;
public class RegisterWindow extends IWAdminWindow {
	
	public static final String BUNDLE_KEY_REGISTER_CLASS = "register_class";
	
	public RegisterWindow() {
		super();
		setScrollbar(false);
		setWidth(500);
		setHeight(250);
		//keepFocus();
	}
	public void main(IWContext iwc) throws Exception {
		String bClass = null;
		try {
			bClass = iwc.getIWMainApplication().getBundle(Login.IW_BUNDLE_IDENTIFIER).getProperty(BUNDLE_KEY_REGISTER_CLASS);
		} catch(Exception e) {
			// just user default Register class
		}
		PresentationObject register;
		if(bClass!=null && bClass.trim().length()>0) {
			Class classDef;
			try {
				classDef = RefactorClassRegistry.forName(bClass);
				register = (PresentationObject) classDef.newInstance();
			} catch (Exception e) {
				System.out.println("Couldn't instantiate class for registration, using default: " + bClass);
				e.printStackTrace();
				register = new Register();
			}
		} else {
			register = new Register();
		}
		
		Table T = new Table(1, 1);
		T.setAlignment(1, 1, "center");
		T.add(register, 1, 1);
		add(T);
		setTitle("Registration");
		//addTitle("Login Editor");
	}
	
	
}
