package com.idega.block.login.presentation;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

import java.rmi.RemoteException;
import java.text.MessageFormat;

import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.business.LoginContext;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.PasswordInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;
import com.idega.util.SendMail;
import com.idega.util.text.Name;

public class Register extends Block {

	private String errorMsg = "";
	public static String prmUserId = "user_id";
	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.block.login";
	protected IWResourceBundle iwrb;
	protected IWBundle iwb;
	public final int INIT = 100;
	public final int NORMAL = 0;
	public final int USER_NAME_EXISTS = 1;
	public final int ILLEGAL_USERNAME = 2;
	public final int ILLEGAL_EMAIL = 3;
	public final int NO_NAME = 5;
	public final int NO_EMAIL = 6;
	public final int NO_USERNAME = 7;
	public final int NO_SERVER = 8;
	public final int NO_LETTER = 9;
	public final int ERROR = 10;
	public final int SENT = 11;
	public final int MISMATCH = 12;
	
	private UserBusiness userBusiness = null;

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	protected void control(IWContext iwc)throws RemoteException {
		int code = this.INIT;
		if (iwc.isParameterSet("send.x")) {
			code = processForm(iwc);
		}
		Table T = new Table(1, 3);
		if (code == this.SENT) {
			T.add(getAnswer(), 1, 2);
		}
		else {
			T.add(getForm(iwc, code), 1, 2);
		}
		add(T);
	}

	private int processForm(IWContext iwc)throws RemoteException {
		String realName = iwc.getParameter("reg_userrealname");
		String userEmail = iwc.getParameter("reg_email");
		String userName = iwc.getParameter("reg_username");
		String pass = iwc.getParameter("reg_pass");
		String conf = iwc.getParameter("reg_pass_conf");
		int code = this.NORMAL;
		if (realName != null && userEmail != null && userName != null) {
			//System.err.println("trying to register");
			code = registerUser(realName, userEmail, userName, pass, conf);
		}
		return code;
	}

	private PresentationObject getSent(IWContext iwc) {
		Table T = new Table();
		T.add(
			this.iwrb.getLocalizedString(
				"register.sent_message",
				"Your login and password has been sent"));

		return T;

	}

	private PresentationObject getForm(IWContext iwc, int code) {
		Table T = new Table(2, 9);
		String textInfo = this.iwrb.getLocalizedString("register.info", "Register");
		String textUserRealName =
			this.iwrb.getLocalizedString("register.name", "Your name");
		String textUserEmail =
			this.iwrb.getLocalizedString("register.email", "Email");
		String textUserName =
			this.iwrb.getLocalizedString("register.username", "Username");
		String textPassword =
			this.iwrb.getLocalizedString("register.passwd", "Password");
		String textConfirm =
			this.iwrb.getLocalizedString("register.confirm", "Confirm");
		TextInput inputUserRealName = new TextInput("reg_userrealname");
		TextInput inputUserEmail = new TextInput("reg_email");
		TextInput inputUserName = new TextInput("reg_username");
		TextInput inputPassword = new PasswordInput("reg_pass");
		TextInput inputConfirm = new PasswordInput("reg_pass_conf");
		if (iwc.isParameterSet("reg_userrealname")) {
			inputUserRealName.setContent(iwc.getParameter("reg_userrealname"));
		}
		if (iwc.isParameterSet("reg_email")) {
			inputUserEmail.setContent(iwc.getParameter("reg_email"));
		}
		if (iwc.isParameterSet("reg_username")) {
			inputUserName.setContent(iwc.getParameter("reg_username"));
		}
		T.mergeCells(1, 1, 2, 1);
		T.add(textInfo, 1, 1);
		T.add(textUserRealName, 1, 2);
		T.add(inputUserRealName, 2, 2);
		T.add(textUserEmail, 1, 3);
		T.add(inputUserEmail, 2, 3);
		T.add(textUserName, 1, 4);
		T.add(inputUserName, 2, 4);
		T.add(textPassword, 1, 5);
		T.add(inputPassword, 2, 5);
		T.add(textConfirm, 1, 6);
		T.add(inputConfirm, 2, 6);
		T.mergeCells(1, 7, 2, 7);
		String message = getMessage(code);
		if (message != null) {
			T.add(message, 1, 7);
		}
		//System.err.println(code+" : "+message);
		SubmitButton ok =
			new SubmitButton(
				this.iwrb.getLocalizedImageButton("send", "Send"),
				"send");

		CloseButton close =
			new CloseButton(this.iwrb.getLocalizedImageButton("close", "Close"));

		T.add(ok, 2, 9);
		T.add(close, 2, 9);
		Form myForm = new Form();
		myForm.add(T);
		return myForm;
	}

	public PresentationObject getAnswer() {
		Table table = new Table(1,1);
		table.setCellpaddingAndCellspacing(0);
		table.setHeight(Table.HUNDRED_PERCENT);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setAlignment(1, 1, Table.HORIZONTAL_ALIGN_CENTER);
		table.setVerticalAlignment(1, 1, Table.VERTICAL_ALIGN_MIDDLE);

		Table T = new Table(1, 1);
		T.setHeight(300);
		T.add(
			this.iwrb.getLocalizedString(
				"register.done",
				"Your login and password has been sent to you."));
		table.add(T);

		return table;
	}

	public int registerUser(
		String userRealName,
		String emailAddress,
		String userName,
		String pass,
		String conf) throws RemoteException{

		int internal = this.NORMAL;

		if (userRealName.length() < 2) {
			return this.NO_NAME;
		}

		if (emailAddress.length() == 0) {
			return this.NO_EMAIL;
		}

		if (emailAddress.indexOf("@") == -1) {
			return this.ILLEGAL_EMAIL;
		}

		if (userName.length() == 0) {
			internal = this.NO_USERNAME;
		}
		else if (LoginDBHandler.isLoginInUse(userName)) {
			return this.USER_NAME_EXISTS;
		}

		if (pass != null && conf != null) {

			if (!pass.equals(conf)) {
				return this.MISMATCH;
			}

		}

		String usr = internal == this.NO_USERNAME ? null : userName;

		try {

			String sender = this.iwb.getProperty("register.email_sender");
			String server = this.iwb.getProperty("register.email_server");
			String subject = this.iwb.getProperty("register.email_subject");
			if (sender == null || server == null || subject == null) {
				return this.NO_SERVER;
			}
			String letter =
				this.iwrb.getLocalizedString(
					"register.email_body",
					"Username : {1} \nPassword: {2}");

			if (letter == null) {
				return this.NO_LETTER;
			}

			Name name = new Name(userRealName);
														//createUserWithLogin(String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime,String encryptionType) throws CreateException{
			User iwUser = this.userBusiness.createUserWithLogin(name.getFirstName(),name.getMiddleName(),name.getLastName(),null,    null,                      null,                  null,                                      null,                             usr,                      pass,                    Boolean.TRUE ,                                IWTimestamp.RightNow(),5000,               Boolean.FALSE,    				Boolean.TRUE ,                                      Boolean.FALSE,                                 null);
			LoginContext user = new LoginContext(iwUser,usr,pass);

			if (user == null) {
				return this.NO_USERNAME;
			}

			if (letter != null) {
				Object[] objs = {user.getUserName(),user.getPassword()};
				String body = MessageFormat.format(letter,objs);

				// body.append();

				SendMail.send(
					sender,
					emailAddress,
					"",
					"",
					server,
					subject,
					body.toString());
				return this.SENT;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return this.ERROR;
		}
		return internal;

	}

	public String getMessage(int code) {

		String msg = null;

		switch (code) {

			case NORMAL :
				this.iwrb.getLocalizedString("register.NORMAL", "NORMAL");
				break;

			case USER_NAME_EXISTS :
				msg =
					this.iwrb.getLocalizedString(
						"register.USER_NAME_EXISTS",
						"USER_NAME_EXISTS");
				break;

			case ILLEGAL_USERNAME :
				msg =
					this.iwrb.getLocalizedString(
						"register.ILLEGAL_USERNAME",
						"ILLEGAL_USERNAME");
				break;

			case ILLEGAL_EMAIL :
				msg =
					this.iwrb.getLocalizedString(
						"register.ILLEGAL_EMAIL",
						"ILLEGAL_EMAIL");
				break;

			case NO_NAME :
				msg = this.iwrb.getLocalizedString("register.NO_NAME", "NO_NAME");
				break;

			case NO_EMAIL :
				msg = this.iwrb.getLocalizedString("register.NO_EMAIL", "NO_EMAIL");
				break;

			case NO_USERNAME :
				msg =
					this.iwrb.getLocalizedString("register.NO_USERNAME", "NO_USER");
				break;

			case NO_SERVER :
				msg =
					this.iwrb.getLocalizedString("register.NO_SERVER", "NO_SERVER");
				break;

			case ERROR :
				msg = this.iwrb.getLocalizedString("register.ERROR", "ERROR");
				break;

			case SENT :
				msg = this.iwrb.getLocalizedString("register.SENT", "SENT");
				break;

			case MISMATCH :
				msg = this.iwrb.getLocalizedString("register.MISMATCH", "MISMATCH");
				break;
		}
		return msg;
	}
	
	public UserBusiness getUserBusiness(IWApplicationContext iwac) throws RemoteException{
		return (UserBusiness) IBOLookup.getServiceInstance(iwac,UserBusiness.class);
	}

	public void main(IWContext iwc) throws RemoteException {
		this.iwb = getBundle(iwc);
		this.iwrb = getResourceBundle(iwc);
		this.userBusiness = getUserBusiness(iwc);
		control(iwc);
	}

}
