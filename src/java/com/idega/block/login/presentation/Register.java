package com.idega.block.login.presentation;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;

import org.apache.commons.validator.EmailValidator;

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
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.ui.PasswordInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;
import com.idega.util.SendMail;
import com.idega.util.text.Name;

public class Register extends Block {
	
	public static final String COMPONENT_TYPE = "com.idega.Register";

	public static String prmUserId = "user_id";
	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.block.login";
	protected IWResourceBundle iwrb;
	protected IWBundle iwb;
	public static final int INIT = 100;
	public static final int NORMAL = 0;
	public static final int USER_NAME_EXISTS = 1;
	public static final int ILLEGAL_USERNAME = 2;
	public static final int ILLEGAL_EMAIL = 3;
	public static final int NO_NAME = 5;
	public static final int NO_EMAIL = 6;
	public static final int NO_USERNAME = 7;
	public static final int NO_SERVER = 8;
	public static final int NO_LETTER = 9;
	public static final int ERROR = 10;
	public static final int SENT = 11;
	public static final int MISMATCH = 12;
	private boolean generateContainingForm = true;
	private boolean displayCloseButton = true;
	private Integer code;
	
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	protected UIComponent getComponent(IWContext iwc)throws RemoteException {
		int code = getCode();
		Table T = new Table(1, 3);
		if (code == SENT) {
			T.add(getAnswer(iwc), 1, 2);
		} else {
			T.add(getForm(iwc, code), 1, 2);
		}
		
		return T;
	}
	
	@Override
	public void decode(FacesContext fc) {
		super.decode(fc);
		
		IWContext iwc = IWContext.getIWContext(fc);
		
		int code = INIT;
		if (iwc.isParameterSet("send.x")) {
			try {
				code = processForm(iwc);
			} catch (RemoteException e) {
				Logger.getLogger(getClassName()).log(Level.WARNING, "Exception while processing form", e);
			}
		}
		if (code == SENT) {
			
			RegisterEvent event = new RegisterEvent(this);
			event.setRegisterSuccess(true);
			fireRegisterEvent(fc, event);
		}
		
		setCode(code);
	}
	
	protected void fireRegisterEvent(FacesContext ctx, RegisterEvent event) {

		RegisterListener registerListener = (RegisterListener)getValueBindingByAttributeExp(ctx, "registerListener");
		
		if(registerListener != null) {
		
			addRegisterListener(registerListener);
			
			event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
			queueEvent(event);
		}
	}

	private int processForm(IWContext iwc)throws RemoteException {
		String realName = iwc.getParameter("reg_userrealname");
		String userEmail = iwc.getParameter("reg_email");
		String userName = iwc.getParameter("reg_username");
		String pass = iwc.getParameter("reg_pass");
		String conf = iwc.getParameter("reg_pass_conf");
		int code = NORMAL;
		if (realName != null && userEmail != null && userName != null) {
			//System.err.println("trying to register");
			code = registerUser(iwc, realName, userEmail, userName, pass, conf);
		}
		return code;
	}
	
	protected IWResourceBundle getIWRB(IWContext iwc) {
		
		if(iwrb == null)
			iwrb = getResourceBundle(iwc);
		
		return iwrb;
	}
	
	private PresentationObject getForm(IWContext iwc, int code) {
		Table T = new Table(2, 9);
		IWResourceBundle iwrb = getIWRB(iwc);
		String textInfo = iwrb.getLocalizedString("register.info", "Register");
		String textUserRealName =
			iwrb.getLocalizedString("register.name", "Your name");
		String textUserEmail =
			iwrb.getLocalizedString("register.email", "Email");
		String textUserName =
			iwrb.getLocalizedString("register.username", "Username");
		String textPassword =
			iwrb.getLocalizedString("register.passwd", "Password");
		String textConfirm =
			iwrb.getLocalizedString("register.confirm", "Confirm");
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
		String message = getMessage(iwc, code);
		if (message != null) {
			T.add(message, 1, 7);
		}
		
		UIComponent sendButton;
		
		if(isDisplayCloseButton()) {
		
			CloseButton close = new CloseButton(iwrb.getLocalizedImageButton("close", "Close"));
			T.add(close, 2, 9);
		}
		
		if(isGenerateContainingForm()) {
			
			sendButton =
				new SubmitButton(
					iwrb.getLocalizedImageButton("send", "Send"),
					"send");
			
		} else {
			
			Parameter param = new Parameter("send.x", "");
			T.add(param);
			
			GenericButton gbutton = new GenericButton("send", iwrb.getLocalizedString("send", "Send"));
			gbutton.setOnClick("this.form.elements['send.x'].value='1';this.form.submit();");
	//		gbutton.setOnClick("this.form.submit();");
			sendButton = gbutton;
		}
		
		T.add(sendButton, 2, 9);
		
		if(isGenerateContainingForm()) {
			
			Form myForm = new Form();
			myForm.add(T);
			return myForm;
			
		} else {
			
			return T;
		}
	}
	
	public PresentationObject getAnswer(IWContext iwc) {
		Table table = new Table(1,1);
		table.setCellpaddingAndCellspacing(0);
		table.setHeight(Table.HUNDRED_PERCENT);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setAlignment(1, 1, Table.HORIZONTAL_ALIGN_CENTER);
		table.setVerticalAlignment(1, 1, Table.VERTICAL_ALIGN_MIDDLE);

		Table T = new Table(1, 1);
		T.setHeight(300);
		T.add(
				getIWRB(iwc).getLocalizedString(
				"register.done",
				"Your login and password has been sent to you."));
		table.add(T);

		return table;
	}

	public int registerUser(IWContext iwc,
		String userRealName,
		String emailAddress,
		String userName,
		String pass,
		String conf) throws RemoteException{

		int internal = NORMAL;

		if (userRealName.length() < 2) {
			return NO_NAME;
		}

		if (emailAddress.length() == 0) {
			return NO_EMAIL;
		}

		
		if (!EmailValidator.getInstance().isValid(emailAddress)) {
			return ILLEGAL_EMAIL;
		}

		if (userName.length() == 0) {
			internal = NO_USERNAME;
		}
		else if (LoginDBHandler.isLoginInUse(userName)) {
			return USER_NAME_EXISTS;
		}

		if (pass != null && conf != null) {

			if (!pass.equals(conf)) {
				return MISMATCH;
			}

		}

		String usr = internal == NO_USERNAME ? null : userName;

		try {
			String sender = iwc.getApplicationSettings().getProperty("register.email_sender");
			String server = iwc.getApplicationSettings().getProperty("register.email_server");
			String subject = iwc.getApplicationSettings().getProperty("register.email_subject");
			if (sender == null || server == null || subject == null) {
				return NO_SERVER;
			}
			String letter =
				getIWRB(iwc).getLocalizedString(
					"register.email_body",
					"Username : {1} \nPassword: {2}");

			if (letter == null) {
				return NO_LETTER;
			}

			Name name = new Name(userRealName);
														//createUserWithLogin(String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime,String encryptionType) throws CreateException{
			User iwUser = getUserBusiness(iwc).createUserWithLogin(name.getFirstName(),name.getMiddleName(),name.getLastName(),null,    null,                      null,                  null,                                      null,                             usr,                      pass,                    Boolean.TRUE ,                                IWTimestamp.RightNow(),5000,               Boolean.FALSE,    				Boolean.TRUE ,                                      Boolean.FALSE,                                 null);
			LoginContext user = new LoginContext(iwUser,usr,pass);

			if (letter != null) {
				Object[] objs = {user.getUserName(),user.getPassword()};
				String body = MessageFormat.format(letter,objs);

				// body.append();

				SendMail.send(
					sender,
					emailAddress,
					CoreConstants.EMPTY,
					CoreConstants.EMPTY,
					server,
					subject,
					body.toString());
				return SENT;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ERROR;
		}
		return internal;

	}

	public String getMessage(IWContext iwc, int code) {

		String msg = null;
		IWResourceBundle iwrb = getIWRB(iwc);

		switch (code) {

			case NORMAL :
				iwrb.getLocalizedString("register.NORMAL", "NORMAL");
				break;

			case USER_NAME_EXISTS :
				msg =
					iwrb.getLocalizedString(
						"register.USER_NAME_EXISTS",
						"USER_NAME_EXISTS");
				break;

			case ILLEGAL_USERNAME :
				msg =
					iwrb.getLocalizedString(
						"register.ILLEGAL_USERNAME",
						"ILLEGAL_USERNAME");
				break;

			case ILLEGAL_EMAIL :
				msg =
					iwrb.getLocalizedString(
						"register.ILLEGAL_EMAIL",
						"ILLEGAL_EMAIL");
				break;

			case NO_NAME :
				msg = iwrb.getLocalizedString("register.NO_NAME", "NO_NAME");
				break;

			case NO_EMAIL :
				msg = iwrb.getLocalizedString("register.NO_EMAIL", "NO_EMAIL");
				break;

			case NO_USERNAME :
				msg =
					iwrb.getLocalizedString("register.NO_USERNAME", "NO_USER");
				break;

			case NO_SERVER :
				msg =
					iwrb.getLocalizedString("register.NO_SERVER", "NO_SERVER");
				break;

			case ERROR :
				msg = iwrb.getLocalizedString("register.ERROR", "ERROR");
				break;

			case SENT :
				msg = iwrb.getLocalizedString("register.SENT", "SENT");
				break;

			case MISMATCH :
				msg = iwrb.getLocalizedString("register.MISMATCH", "MISMATCH");
				break;
		}
		return msg;
	}
	
	public UserBusiness getUserBusiness(IWApplicationContext iwac) throws RemoteException{
		return (UserBusiness) IBOLookup.getServiceInstance(iwac,UserBusiness.class);
	}

	public void main(IWContext iwc) throws RemoteException { 
		
		
	}
	
	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		super.encodeChildren(context);
		
		IWContext iwc = IWContext.getIWContext(context);
		UIComponent c = getComponent(iwc);
		renderChild(context, c);
	}
	
	public interface RegisterListener extends FacesListener {
		
		public abstract void registerSuccess();
	}
	
	public class RegisterEvent extends FacesEvent {

		private static final long serialVersionUID = 4244895460153563070L;
		private Boolean registerSuccess;

		public RegisterEvent(UIComponent component) {
	        super(component);
	    }
		@Override
		public boolean isAppropriateListener(FacesListener faceslistener) {
			return faceslistener instanceof RegisterListener;
		}

		@Override
		public void processListener(FacesListener faceslistener) {
			
			if(faceslistener instanceof RegisterListener) {
				
				RegisterListener listener = (RegisterListener)faceslistener;
			
				if(getRegisterSuccess())
					listener.registerSuccess();
			}
		}
		Boolean getRegisterSuccess() {
			return registerSuccess;
		}
		void setRegisterSuccess(Boolean registerSuccess) {
			this.registerSuccess = registerSuccess;
		}
	}
	
	public void addRegisterListener(RegisterListener listener) {

		if(!listenerAdded()) {
		
			addFacesListener(listener);
			listenerAdded(true);
		}
	}

	public boolean isGenerateContainingForm() {
		return generateContainingForm;
	}

	public void setGenerateContainingForm(boolean generateContainingForm) {
		this.generateContainingForm = generateContainingForm;
	}
	
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object[] value = (Object[]) state;
		super.restoreState(context, value[0]);
		generateContainingForm = (Boolean) value[1];
		displayCloseButton = (Boolean) value[2];
		code = (Integer)value[3];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[4];
		state[0] = super.saveState(context);
		state[1] = generateContainingForm;
		state[2] = displayCloseButton;
		state[3] = code;
		
		return state;
	}

	public Integer getCode() {
		return code == null ? INIT : code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public boolean isDisplayCloseButton() {
		return displayCloseButton;
	}

	public void setDisplayCloseButton(boolean displayCloseButton) {
		this.displayCloseButton = displayCloseButton;
	}
}