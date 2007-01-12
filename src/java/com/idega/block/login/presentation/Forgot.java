package com.idega.block.login.presentation;

/**
 * Title: Description: Copyright: Copyright (c) 2001 Company: idega multimedia
 * 
 * @author <a href="mailto:aron@idega.is">aron@idega.is </a>
 * @version 1.0
 */

import java.text.MessageFormat;

import com.idega.block.login.exception.LoginForgotException;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.LoginContext;
import com.idega.core.accesscontrol.business.LoginCreator;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.contact.data.Email;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.Window;
import com.idega.presentation.util.TextFormat;
import com.idega.user.data.User;
import com.idega.user.data.UserHome;
import com.idega.user.util.Converter;
import com.idega.util.SendMail;

public class Forgot extends Block {

	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.block.login";

	private final static String PRM_USER_LOGIN = "user_login";
	private final static String PARAMETER_PROCESS = "process";

	private boolean _loginInput = false;
	private boolean _hideMessage = false;

	public static String prmUserId = "user_id";

	protected IWResourceBundle iwrb;
	protected IWBundle iwb;

	private String errorMsg = "";

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
	public static final int NO_LOGIN = 12;

	private String portalname = "";

	private TextFormat form;
	
	private int iSpaceBetween = 12;
	private int iInputLength = -1;
	private boolean iUseGeneratedButtons = true;
	private boolean iUseLinksAsButtons = false;
	
	private String iInputStyleClass = null;
	private String iButtonStyleClass = null;
	
	private Image iSendButtonImage = null;
	private Image iCloseButtonImage = null;

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	protected void control(IWContext iwc) {
		// debugParameters(iwc);
		this.portalname = iwc.getServerName();
		this.form = TextFormat.getInstance();
		int code = INIT;
		if (iwc.isParameterSet(PARAMETER_PROCESS)) {
			code = processForm(iwc);
		}
		if (code == NORMAL) {
			add(getSent(iwc));
		}
		else {
			add(getInitialState(iwc, code));
		}
	}

	private PresentationObject getInitialState(IWContext iwc, int code) {

		return getForm(iwc, code);
	}

	private int processForm(IWContext iwc) {
		String userEmail = iwc.getParameter("reg_user_email");
		String userLogin = iwc.getParameter(PRM_USER_LOGIN);
		int code = NORMAL;
		try {
			if (userEmail != null) {
				User usr = lookupUserByEmail(userEmail);
				sendEmail(iwc, usr, userEmail);
			}
			else if (userLogin != null) {
				System.out.println("login.presentation.Forgot.java - login mode");
				User usr = lookupUserByLogin(userLogin);
				Email email = getUserEmail(usr);
				if (email != null) {
					sendEmail(iwc, usr, email.getEmailAddress());
				}
				else {
					code = NO_EMAIL;
				}

			}
		}
		catch (LoginForgotException e) {
			code = e.getCode();
		}
		return code;
	}

	private PresentationObject getSent(IWContext iwc) {
		Table T = new Table();
		T.add(this.iwrb.getLocalizedString("forgotten.sent_message", "Your login and password has been sent"));
		return T;
	}

	private PresentationObject getForm(IWContext iwc, int code) {
		String message = getMessage(code);

		Form myForm = new Form();
		myForm.addParameter(PARAMETER_PROCESS, "true");
		Table T = new Table();
		T.setColumns(2);
		T.setWidth(Table.HUNDRED_PERCENT);
		T.setCellpadding(0);
		T.setCellspacing(0);
		myForm.add(T);
		int row = 1;

		if (!this._hideMessage) {
			String manual = this.iwrb.getLocalizedString("forgotten.manual", "Enter your username and a new password will be sent to your registered email address");
			T.mergeCells(1, row, 2, row);
			T.add(this.form.format(manual), 1, row++);
		}
		
		if (this.iSpaceBetween > 0) {
			T.setHeight(row++, this.iSpaceBetween);
		}

		if (this._loginInput) {
			TextInput inputUserLogin = new TextInput(PRM_USER_LOGIN);
			if (this.iInputStyleClass != null) {
				inputUserLogin.setStyleClass(this.iInputStyleClass);
			}
			if (this.iInputLength > 0) {
				inputUserLogin.setLength(this.iInputLength);
			}
			String textUserLogin = this.iwrb.getLocalizedString("forgotten.user_login", "Login") + Text.NON_BREAKING_SPACE + Text.NON_BREAKING_SPACE;
			T.add(this.form.format(textUserLogin), 1, row);
			T.add(inputUserLogin, 2, row++);
		}
		else {
			TextInput inputUserEmail = new TextInput("reg_user_email");
			if (this.iInputStyleClass != null) {
				inputUserEmail.setStyleClass(this.iInputStyleClass);
			}
			if (this.iInputLength > 0) {
				inputUserEmail.setLength(this.iInputLength);
			}
			String textUserEmail = this.iwrb.getLocalizedString("forgotten.user_email", "Email") + Text.NON_BREAKING_SPACE + Text.NON_BREAKING_SPACE;
			if (iwc.isParameterSet("reg_user_email")) {
				inputUserEmail.setContent(iwc.getParameter("reg_user_email"));
			}
			T.add(this.form.format(textUserEmail), 1, row);
			T.add(inputUserEmail, 2, row++);
		}

		if (this.iSpaceBetween > 0) {
			T.setHeight(row++, this.iSpaceBetween);
		}

		if (message != null) {
			T.mergeCells(1, row, 2, row);
			T.add(this.form.format(message, "#ff0000"), 1, row++);
		}

		if (this.iSpaceBetween > 0) {
			T.setHeight(row++, this.iSpaceBetween);
		}

		PresentationObject ok = null;
		PresentationObject close = null;
		if (this.iUseLinksAsButtons) {
			ok = new Link(this.iwrb.getLocalizedString("send", "Send"));
			((Link) ok).setToFormSubmit(myForm);
			close = new Link(this.iwrb.getLocalizedString("close", "Close"));
			((Link) close).setAsCloseLink();
		}
		else if (this.iUseGeneratedButtons) {
			ok = new SubmitButton(this.iwrb.getLocalizedImageButton("send", "Send"), "send");
			close = new CloseButton(this.iwrb.getLocalizedImageButton("close", "Close"));
		}
		else {
			if (this.iSendButtonImage != null) {
				ok = new SubmitButton(this.iSendButtonImage);
			}
			else {
				ok = new SubmitButton(this.iwrb.getLocalizedString("send", "Send"));
			}
			if (this.iCloseButtonImage != null) {
				close = new CloseButton(this.iCloseButtonImage);
			}
			else {
				close = new CloseButton(this.iwrb.getLocalizedString("close", "Close"));
			}
		}
		
		if (this.iButtonStyleClass != null) {
			ok.setStyleClass(this.iButtonStyleClass);
			close.setStyleClass(this.iButtonStyleClass);
		}

		T.setAlignment(2, row, Table.HORIZONTAL_ALIGN_RIGHT);
		T.add(ok, 2, row);
		if (this.getParentPage() instanceof Window) {
			T.add(close, 2, row);
		}

		return myForm;
	}

	public PresentationObject getAnswer() {
		Table table = new Table(1, 1);
		table.setCellpaddingAndCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setAlignment(1, 1, Table.HORIZONTAL_ALIGN_CENTER);
		table.setVerticalAlignment(1, 1, Table.VERTICAL_ALIGN_MIDDLE);

		Table T = new Table(1, 1);
		T.setWidth(Table.HUNDRED_PERCENT);
		T.setCellpadding(0);
		T.setCellspacing(0);
		T.add(this.iwrb.getLocalizedString("forgotten.done", "Your login and password has been sent to you."));
		table.add(T);
		return table;
	}

	public User lookupUserByEmail(String emailAddress) throws LoginForgotException {
		System.err.println("Beginning lookup");
		if (emailAddress.length() == 0) {
			throw new LoginForgotException(NO_EMAIL);
		}
		
		User usr = null;
		try {
			UserHome uhome = (UserHome) com.idega.data.IDOLookup.getHome(User.class);
			usr = uhome.findUserFromEmail(emailAddress);
			LoginTable login = LoginDBHandler.getUserLogin(((Integer) usr.getPrimaryKey()).intValue());
			if (login == null) {
				throw new LoginForgotException(NO_USERNAME);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new LoginForgotException(NO_NAME);
		}

		return usr;
	}

	public User lookupUserByLogin(String loginName) throws LoginForgotException {
		System.err.println("Beginning lookup");
		if (loginName.length() == 0) {
			throw new LoginForgotException(NO_LOGIN);
		}

		User usr = null;
		try {
			LoginTable[] login = (LoginTable[]) (com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance()).findAllByColumnEquals(com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(), loginName);
			if (login == null || login.length < 0) {
				throw new LoginForgotException(NO_LOGIN);
			}

			usr = Converter.convertToNewUser(login[0].getUser());

		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new LoginForgotException(NO_NAME);
		}

		return usr;
	}

	private void sendEmail(IWContext iwc, User usr, String emailAddress) throws LoginForgotException {
		String sender = this.iwb.getProperty("forgotten.email_sender", "admin@idega.is");
		String server = this.iwb.getProperty("forgotten.email_server", "mail.idega.is");
		String subject = this.iwb.getProperty("forgotten.email_subject", "Forgotten password");
		if (sender == null || server == null || subject == null) {
			throw new LoginForgotException(NO_SERVER);
		}

		LoginContext context = null;
		if (usr != null) {
			try {
				context = LoginBusinessBean.changeUserPassword(usr, LoginCreator.createPasswd(8));
			}
			catch (Exception ex) {
				ex.printStackTrace();
				throw new LoginForgotException(ILLEGAL_USERNAME);
			}

			System.err.println(usr.getName() + " has forgotten password");
			String letter = this.iwrb.getLocalizedString("forgotten.email_body", "Username : {0} \nPassword: {1} ");
			if (letter == null) {
				throw new LoginForgotException(NO_LETTER);
			}

			if (letter != null && context != null) {
				Object[] objs = { context.getUserName(), context.getPassword() };
				String body = MessageFormat.format(letter, objs);

				try {
					SendMail.send(sender, emailAddress, "", "", server, subject, body.toString());
				}
				catch (javax.mail.MessagingException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public Email getUserEmail(User user) {
		java.util.Collection emails = null;
		try {
			com.idega.core.contact.data.EmailHome emailhome = (com.idega.core.contact.data.EmailHome) com.idega.data.IDOLookup.getHome(com.idega.core.contact.data.Email.class);
			emails = emailhome.findEmailsForUser(((Integer) user.getPrimaryKey()).intValue());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		if (emails != null && emails.size() > 0) {
			return (com.idega.core.contact.data.Email) emails.iterator().next();
		}
		return null;
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

	public void main(IWContext iwc) {
		this.iwb = getBundle(iwc);
		this.iwrb = getResourceBundle(iwc);
		control(iwc);
	}

	public void setToUseLoginInput(boolean value) {
		this._loginInput = value;
	}

	public void setToHideMessage(boolean value) {
		this._hideMessage = value;
	}
	
	public void setButtonStyleClass(String buttonStyleClass) {
		this.iButtonStyleClass = buttonStyleClass;
	}
	
	public void setInputStyleClass(String inputStyleClass) {
		this.iInputStyleClass = inputStyleClass;
	}
	
	public void setSpaceBetween(int spaceBetween) {
		this.iSpaceBetween = spaceBetween;
	}
	
	public void setUseGeneratedButtons(boolean useGeneratedButtons) {
		this.iUseGeneratedButtons = useGeneratedButtons;
	}
	
	public void setUseLinksAsButtons(boolean useLinksAsButtons) {
		this.iUseLinksAsButtons = useLinksAsButtons;
	}
	
	public void setInputLength(int inputLength) {
		this.iInputLength = inputLength;
	}
	
	public void setCloseButtonImage(Image closeButtonImage) {
		this.iCloseButtonImage = closeButtonImage;
	}
	
	public void setSendButtonImage(Image sendButtonImage) {
		this.iSendButtonImage = sendButtonImage;
	}
}