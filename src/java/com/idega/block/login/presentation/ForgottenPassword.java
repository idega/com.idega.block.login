package com.idega.block.login.presentation;

import com.idega.block.login.exception.LoginModificationException;
import com.idega.core.contact.data.Email;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.FormItem;
import com.idega.presentation.ui.TextInput;
import com.idega.user.data.User;

/**
 * <p>
 * Component to re-send a new login/password to a user registered with email.<br>
 * This is a refactoring of the older Forgot component.
 * </p>
 *  Last modified: $Date: 2007/06/28 14:38:31 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1.2.1 $
 */
public class ForgottenPassword extends UserRegistration {

	private boolean _loginInput = false;
	private boolean _hideMessage = false;
	
	public static final String DEFAULT_STYLE_CLASS = "ForgottenPassword";
	
	public ForgottenPassword() {
		setStyleClass(DEFAULT_STYLE_CLASS);
		setLookupUserByEmail(true);
		setAllowNoPreviousLogin(false);
	}
	
	protected int processForm(IWContext iwc) {
		String userEmail = iwc.getParameter("reg_user_email");
		String userLogin = iwc.getParameter(PARAMETER_USER_LOGIN);
		int code = NORMAL;
		try {
			if (userEmail != null) {
				User usr = lookupUserByEmail(userEmail);
				sendNewLoginEmailToUser(iwc, usr, userEmail);
			}
			else if (userLogin != null) {
				System.out.println("login.presentation.Forgot.java - login mode");
				User usr = lookupUserByLogin(userLogin);
				Email email = getUserEmail(usr);
				if (email != null) {
					sendNewLoginEmailToUser(iwc, usr, email.getEmailAddress());
				}
				else {
					code = NO_EMAIL;
				}
			}
		}
		catch (LoginModificationException e) {
			code = e.getCode();
		}
		return code;
	}

	protected PresentationObject getSubmitted(IWContext iwc) {
		Layer layer = new Layer(Layer.DIV);
		layer.add(this.iwrb.getLocalizedString("forgotten.sent_message", "Your login and password has been sent"));
		return layer;
	}

	protected PresentationObject getForm(IWContext iwc, int code) {
		String message = getMessage(code);
		Form myForm = getForm();
		Layer T = new Layer(Layer.DIV);
		myForm.add(T);
		if (!this.isHideMessage()) {
			String manual = this.iwrb.getLocalizedString("forgotten.manual",
					"Enter your username and a new password will be sent to your registered email address");
			T.add(this.getFormatter().format(manual));
		}
		if (this.isLoginInput()) {
			TextInput inputUserLogin = new TextInput(PARAMETER_USER_LOGIN);
			if (this.iInputStyleClass != null) {
				inputUserLogin.setStyleClass(this.iInputStyleClass);
			}
			if (this.getIInputLength() > 0) {
				inputUserLogin.setLength(this.getIInputLength());
			}
			String textUserLogin = this.iwrb.getLocalizedString("forgotten.user_login", "Login")
					+ Text.NON_BREAKING_SPACE + Text.NON_BREAKING_SPACE;
			T.add(new FormItem(textUserLogin, inputUserLogin));
		}
		else {
			TextInput inputUserEmail = new TextInput("reg_user_email");
			if (this.iInputStyleClass != null) {
				inputUserEmail.setStyleClass(this.iInputStyleClass);
			}
			if (this.getIInputLength() > 0) {
				inputUserEmail.setLength(this.getIInputLength());
			}
			String textUserEmail = this.iwrb.getLocalizedString("forgotten.user_email", "Email")
					+ Text.NON_BREAKING_SPACE + Text.NON_BREAKING_SPACE;
			if (iwc.isParameterSet("reg_user_email")) {
				inputUserEmail.setContent(iwc.getParameter("reg_user_email"));
			}
			T.add(new FormItem(textUserEmail, inputUserEmail));
		}
		if (message != null) {
			T.add(this.getFormatter().format(message, "#ff0000"));
		}
		addButtons(myForm, T);
		return myForm;
	}


	protected String getEmailBody() {
		String letter = this.iwrb.getLocalizedString("forgotten.email_body", "Username : {0} \nPassword: {1} ");
		return letter;
	}

	protected String getEmailSubject() {
		String subject = this.iwrb.getLocalizedString("forgotten.email_subject", "Forgotten password");
		return subject;
	}

	

	public String getMessage(int code) {
		String msg = null;
		switch (code) {
			case NORMAL:
				this.iwrb.getLocalizedString("register.NORMAL", "NORMAL");
				break;
			case USER_NAME_EXISTS:
				msg = this.iwrb.getLocalizedString("register.USER_NAME_EXISTS", "USER_NAME_EXISTS");
				break;
			case ILLEGAL_USERNAME:
				msg = this.iwrb.getLocalizedString("register.ILLEGAL_USERNAME", "ILLEGAL_USERNAME");
				break;
			case ILLEGAL_EMAIL:
				msg = this.iwrb.getLocalizedString("register.ILLEGAL_EMAIL", "ILLEGAL_EMAIL");
				break;
			case NO_NAME:
				msg = this.iwrb.getLocalizedString("register.NO_NAME", "NO_NAME");
				break;
			case NO_EMAIL:
				msg = this.iwrb.getLocalizedString("register.NO_EMAIL", "NO_EMAIL");
				break;
			case NO_USERNAME:
				msg = this.iwrb.getLocalizedString("register.NO_USERNAME", "NO_USER");
				break;
			case NO_SERVER:
				msg = this.iwrb.getLocalizedString("register.NO_SERVER", "NO_SERVER");
				break;
			case ERROR:
				msg = this.iwrb.getLocalizedString("register.ERROR", "ERROR");
				break;
			case SENT:
				msg = this.iwrb.getLocalizedString("register.SENT", "SENT");
				break;
		}
		return msg;
	}

	public void setToUseLoginInput(boolean value) {
		this.setLoginInput(value);
	}

	public void setToHideMessage(boolean value) {
		this.setHideMessage(value);
	}

	public void setLoginInput(boolean _loginInput) {
		this._loginInput = _loginInput;
		if(_loginInput){
			setLookupUserByEmail(false);
		}
	}

	public boolean isLoginInput() {
		return _loginInput;
	}

	public void setHideMessage(boolean _hideMessage) {
		this._hideMessage = _hideMessage;
	}

	public boolean isHideMessage() {
		return _hideMessage;
	}
	

}
