package com.idega.block.login.presentation;

import java.io.IOException;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.PasswordInput;
import com.idega.util.CoreConstants;

/**
 *
 * 
 * @author <a href="anton@idega.com">Anton Makarov</a>
 * @version Revision: 1.0 
 *
 * Last modified: Oct 1, 2008 by Author: Anton 
 *
 */

public class UserPasswordChanger extends Block {

	private static final String HEADER_ID = "header";	
	private static final String ERROR_MESSAGE_ID = "message";
	private static final String PASSWORD_CHANGER_STYLE = "passwordChanger";
	
	private IWBundle bundle = null;
	private IWResourceBundle iwrb = null;
	
	@Override
	public String getBundleIdentifier() {
		return Login2.IW_BUNDLE_IDENTIFIER;
	}
	
	@SuppressWarnings("unchecked")
	private void initializeLocalVariables(IWContext iwc) {
		bundle = getBundle(iwc);
		iwrb = bundle.getResourceBundle(iwc);
	}
	
	@Override
	public void main(IWContext iwc) throws IOException {
		initializeLocalVariables(iwc);
		Layer container = new Layer();
		container.setID(PASSWORD_CHANGER_STYLE);
		container.setStyleClass("passwords");
		add(container);
		
		String styleName = "webfaceFormItem";
		
		// Header
		Layer headerContainer = new Layer();
		container.add(headerContainer);
		headerContainer.setStyleClass(styleName);
		Text header = new Text(iwrb.getLocalizedString("change_your_password", "Please change your password"));
		header.setID(HEADER_ID);
		headerContainer.add(header);
		
		// Error message
		Layer messageContainer = new Layer();
		container.add(messageContainer);
		messageContainer.setStyleClass(styleName);
		Text msgOutput = new Text("");
		msgOutput.setID(ERROR_MESSAGE_ID);
		messageContainer.add(msgOutput);
		
		// User password
		Layer passwordContainer = new Layer();
		container.add(passwordContainer);
		passwordContainer.setStyleClass(styleName);
		PasswordInput passwordInput = new PasswordInput("password", CoreConstants.EMPTY);
		passwordInput.setID("password");

		Label passwordLabel = new Label(iwrb.getLocalizedString("password", "Password"), passwordInput);
		passwordContainer.add(passwordLabel);
		passwordContainer.add(passwordInput);
		
		// User password verify
		Layer password2Container = new Layer();
		container.add(password2Container);
		password2Container.setStyleClass(styleName);
		PasswordInput password2Input = new PasswordInput("password2", CoreConstants.EMPTY);
		password2Input.setID("password2");

		Label password2Label = new Label(iwrb.getLocalizedString("verify_password", "Verify password"), password2Input);
		password2Container.add(password2Label);
		password2Container.add(password2Input);		
		
		// Save button
		Layer buttonsContainer = new Layer();
		container.add(buttonsContainer);
		buttonsContainer.setStyleClass("webfaceButtonLayer");
		
		GenericButton saveButton = new GenericButton(iwrb.getLocalizedString("save", "Save"));
		
		StringBuffer onClickScript = new StringBuffer("validateAndSavePassword('").append(ERROR_MESSAGE_ID)
		.append(CoreConstants.QOUTE_SINGLE_MARK).append(CoreConstants.COMMA).append(CoreConstants.QOUTE_SINGLE_MARK)
		.append(getResourceBundle(iwc).getLocalizedString("password_validation_error", "Passwords do not match")).append(CoreConstants.QOUTE_SINGLE_MARK)
		.append(CoreConstants.COMMA).append(CoreConstants.QOUTE_SINGLE_MARK).append(getResourceBundle(iwc).getLocalizedString("password_save_error", "System error. Password was not saved"))
		.append(CoreConstants.QOUTE_SINGLE_MARK).append(");");
			
		saveButton.setOnClick(onClickScript.toString());
		buttonsContainer.add(saveButton);
	}
}
