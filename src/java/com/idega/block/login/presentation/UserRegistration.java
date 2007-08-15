/*
 */
package com.idega.block.login.presentation;

import java.rmi.RemoteException;
import java.text.MessageFormat;
import javax.ejb.FinderException;
import com.idega.block.login.exception.LoginModificationException;
import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.LoginContext;
import com.idega.core.accesscontrol.business.LoginCreator;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.LoginTableHome;
import com.idega.core.contact.data.Email;
import com.idega.core.messaging.EmailMessage;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.FormItem;
import com.idega.presentation.ui.PasswordInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.Window;
import com.idega.presentation.util.TextFormat;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.user.data.UserHome;
import com.idega.user.util.Converter;
import com.idega.util.IWTimestamp;
import com.idega.util.text.Name;

/**
 * <p>
 * Component to register as a new user in the system.<br/> 
 * This is a refactoring of the older Register component.
 * </p>
 * Last modified: $Date: 2007/08/15 13:42:58 $ by $Author: sigtryggur $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1.2.2 $
 */
public class UserRegistration extends Block {
	
	public final static String PARAMETER_USER_LOGIN = "user_login";
	public final static String PARAMETER_PROCESS = "process";
	public static String PARAMETER_USER_ID = "user_id";
	public static String PARAMETER_SEND = "send";
	public static final String PARAMETER_PASSWORD_CONFIRM = "reg_pass_conf";
	public static final String PARAMETER_PASSWORD = "reg_pass";
	public static final String PARAMETER_USERNAME = "reg_username";
	public static final String PARAMETER_EMAIL = "reg_email";
	public static final String PARAMETER_USERREALNAME = "reg_userrealname";
	protected final static String IW_BUNDLE_IDENTIFIER = "com.idega.block.login";
	
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
	public static final int NO_LOGIN = 12;
	public static final int MISMATCH = 13;
	public static final int NO_USER_WITH_EMAIL = 14;

	private UserBusiness userBusiness = null;

	private TextFormat formatter;
	
	private int iInputLength = -1;
	private boolean iUseLinksAsButtons = false;
	protected String iInputStyleClass = null;
	protected String iButtonStyleClass = null;
	public static final String DEFAULT_STYLE_CLASS = "UserRegistration";
	
	private boolean lookupUserByEmail=false;
	//private boolean lookupUserByPersonalId=false;
	//private boolean supplyUsernameAndPassword=true;
	private boolean allowNoPreviousLogin=true;
	
	public UserRegistration() {
		setStyleClass(DEFAULT_STYLE_CLASS);
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
	
	public void main(IWContext iwc) throws Exception {
		this.iwb = getBundle(iwc);
		this.iwrb = getResourceBundle(iwc);
		this.userBusiness = getUserBusiness(iwc);
		control(iwc);
	}

	protected void control(IWContext iwc) {
		// debugParameters(iwc);
		this.setFormatter(TextFormat.getInstance());
		int code = INIT;
		if (iwc.isParameterSet(PARAMETER_PROCESS)) {
			code = processForm(iwc);
		}
		Layer outerLayer = new Layer(Layer.DIV);
		outerLayer.setStyleClass(this.getStyleClass());
		add(outerLayer);
		if (code == NORMAL) {
			outerLayer.add(getSubmitted(iwc));
		}
		else {
			outerLayer.add(getInitialState(iwc, code));
		}
	}
	
	protected int processForm(IWContext iwc) {
		String realName = iwc.getParameter(PARAMETER_USERREALNAME);
		String userEmail = iwc.getParameter(PARAMETER_EMAIL);
		String userName = iwc.getParameter(PARAMETER_USERNAME);
		String pass = iwc.getParameter(PARAMETER_PASSWORD);
		String conf = iwc.getParameter(PARAMETER_PASSWORD_CONFIRM);
		int code = NORMAL;
		if (realName != null && userEmail != null && userName != null) {
			// System.err.println("trying to register");
			try {
				code = registerUser(realName, userEmail, userName, pass, conf);
			}
			catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (userEmail != null && isLookupUserByEmail()) {
			User usr;
			try {
				usr = lookupUserByEmail(userEmail);
				if (usr != null) {
					sendNewLoginEmailToUser(iwc, usr, userEmail);
				} else {
					code = NO_USER_WITH_EMAIL;
				}
			}
			catch (LoginModificationException e) {
				e.printStackTrace();
			}
		}
		return code;
	}

	protected PresentationObject getForm(IWContext iwc, int code) {
		Layer T = new Layer();
		String stextInfo = this.iwrb.getLocalizedString("register.info", "Register");
		String textUserRealName = this.iwrb.getLocalizedString("register.name", "Your name");
		String textUserEmail = this.iwrb.getLocalizedString("register.email", "Email");
		String textUserName = this.iwrb.getLocalizedString("register.username", "Username");
		String textPassword = this.iwrb.getLocalizedString("register.passwd", "Password");
		String textConfirm = this.iwrb.getLocalizedString("register.confirm", "Confirm");
		TextInput inputUserRealName = new TextInput(PARAMETER_USERREALNAME);
		TextInput inputUserEmail = new TextInput(PARAMETER_EMAIL);
		TextInput inputUserName = new TextInput(PARAMETER_USERNAME);
		TextInput inputPassword = new PasswordInput(PARAMETER_PASSWORD);
		TextInput inputConfirm = new PasswordInput(PARAMETER_PASSWORD_CONFIRM);
		if (iwc.isParameterSet(PARAMETER_USERREALNAME)) {
			inputUserRealName.setContent(iwc.getParameter(PARAMETER_USERREALNAME));
		}
		if (iwc.isParameterSet(PARAMETER_EMAIL)) {
			inputUserEmail.setContent(iwc.getParameter(PARAMETER_EMAIL));
		}
		if (iwc.isParameterSet(PARAMETER_USERNAME)) {
			inputUserName.setContent(iwc.getParameter(PARAMETER_USERNAME));
		}
		Layer textInfo = new Layer();
		textInfo.add(stextInfo);
		T.add(textInfo);
		if(isLookupUserByEmail()){
			T.add(new FormItem(textUserEmail, inputUserEmail));
		}
		else{
			T.add(new FormItem(textUserRealName, inputUserRealName));
			T.add(new FormItem(textUserEmail, inputUserEmail));
			T.add(new FormItem(textUserName, inputUserName));
			T.add(new FormItem(textPassword, inputPassword));
			T.add(new FormItem(textConfirm, inputConfirm));
		}

		String message = getMessage(code);
		if (message != null) {
			Layer layer = new Layer();
			T.add(layer);
			layer.add(message);
		}
		// System.err.println(code+" : "+message);
		Form myForm = getForm();
		myForm.add(T);
		addButtons(myForm, T);
		return myForm;
	}
	
	protected Form getForm() {
		Form myForm = new Form();
		myForm.addParameter(PARAMETER_PROCESS, "true");
		return myForm;
	}

	protected PresentationObject getSubmitted(IWContext iwc) {
		Layer table = new Layer();
		Layer T = new Layer();
		T.add(this.iwrb.getLocalizedString("register.done", "Your login and password has been sent to you."));
		table.add(T);
		return table;
	}

	public int registerUser(String userRealName, String emailAddress, String userName, String pass, String conf)
			throws RemoteException {
		int internal = NORMAL;
		if (userRealName.length() < 2) {
			return NO_NAME;
		}
		if (emailAddress.length() == 0) {
			return NO_EMAIL;
		}
		if (emailAddress.indexOf("@") == -1) {
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
			/*String sender = this.iwb.getProperty("register.email_sender");
			String server = this.iwb.getProperty("register.email_server");
			String subject = this.iwb.getProperty("register.email_subject");
			if (sender == null || server == null || subject == null) {
				return NO_SERVER;
			}*/
			String subject = getEmailSubject();
			String letter = getEmailBody();
			if (letter == null) {
				return NO_LETTER;
			}
			Name name = new Name(userRealName);
			// createUserWithLogin(String firstname, String middlename, String
			// lastname, String displayname, String description, Integer gender,
			// IWTimestamp date_of_birth, Integer primary_group, String
			// userLogin, String password, Boolean accountEnabled, IWTimestamp
			// modified, int daysOfValidity, Boolean passwordExpires, Boolean
			// userAllowedToChangePassw, Boolean changeNextTime,String
			// encryptionType) throws CreateException{
			User iwUser = this.userBusiness.createUserWithLogin(name.getFirstName(), name.getMiddleName(),
					name.getLastName(), null, null, null, null, null, usr, pass, Boolean.TRUE, IWTimestamp.RightNow(),
					5000, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, null);
			LoginContext user = new LoginContext(iwUser, usr, pass);
			if (user == null) {
				return NO_USERNAME;
			}
			if (letter != null) {
				Object[] objs = { user.getUserName(), user.getPassword() };
				String body = MessageFormat.format(letter, objs);
				// body.append();
				//SendMail.send(sender, emailAddress, "", "", server, subject, body.toString());
				EmailMessage message = new EmailMessage(subject,body.toString(),emailAddress);
				message.send();
				return SENT;
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return ERROR;
		}
		return internal;
	}

	protected String getEmailBody() {
		String letter = this.iwrb.getLocalizedString("register.email_body", "Username : {0} \nPassword: {1}");
		return letter;
	}

	protected String getEmailSubject() {
		String subject = this.iwrb.getLocalizedString("register.email_subject", "New Login information");
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
			case MISMATCH:
				msg = this.iwrb.getLocalizedString("register.MISMATCH", "MISMATCH");
				break;
			case NO_USER_WITH_EMAIL:
				msg = this.iwrb.getLocalizedString("register.NO_USER_WITH_EMAIL", "NO_USER_WITH_EMAIL");
				break;
		}
		return msg;
	}

	protected PresentationObject getInitialState(IWContext iwc, int code) {
		return getForm(iwc, code);
	}

	public UserBusiness getUserBusiness(IWApplicationContext iwac) throws RemoteException {
		return (UserBusiness) IBOLookup.getServiceInstance(iwac, UserBusiness.class);
	}

	protected void addButtons(Form myForm, Layer T) {
		PresentationObject ok = null;
		PresentationObject close = null;
		if (this.iUseLinksAsButtons) {
			ok = new Link(this.iwrb.getLocalizedString("send", "Send"));
			((Link) ok).setToFormSubmit(myForm);
			close = new Link(this.iwrb.getLocalizedString("close", "Close"));
			((Link) close).setAsCloseLink();
		}
		else {
			ok = new SubmitButton(this.iwrb.getLocalizedString("send", "Send"));
			close = new CloseButton(this.iwrb.getLocalizedString("close", "Close"));
			close.setStyleClass(this.iButtonStyleClass);
		}
		if (this.iButtonStyleClass != null) {
			ok.setStyleClass(this.iButtonStyleClass);
		}
		Layer buttonsLayer = new Layer(Layer.DIV);
		T.add(buttonsLayer);
		buttonsLayer.add(ok);
		if (this.getParentPage() instanceof Window) {
			buttonsLayer.add(close);
		}
	}
	
	public void setButtonStyleClass(String buttonStyleClass) {
		this.iButtonStyleClass = buttonStyleClass;
	}

	public void setInputStyleClass(String inputStyleClass) {
		this.iInputStyleClass = inputStyleClass;
	}

	public void setUseLinksAsButtons(boolean useLinksAsButtons) {
		this.iUseLinksAsButtons = useLinksAsButtons;
	}
	
	public boolean getUseLinksAsButtons() {
		return this.iUseLinksAsButtons;
	}

	public void setInputLength(int inputLength) {
		this.setIInputLength(inputLength);
	}

	protected void setFormatter(TextFormat form) {
		this.formatter = form;
	}

	protected TextFormat getFormatter() {
		return formatter;
	}

	protected void setIInputLength(int iInputLength) {
		this.iInputLength = iInputLength;
	}

	protected int getIInputLength() {
		return iInputLength;
	}

	
	public boolean isLookupUserByEmail() {
		return lookupUserByEmail;
	}

	
	public void setLookupUserByEmail(boolean lookupUserByEmail) {
		this.lookupUserByEmail = lookupUserByEmail;
	}

	
	/*public boolean isLookupUserByPersonalId() {
		return lookupUserByPersonalId;
	}

	
	public void setLookupUserByPersonalId(boolean lookupUserByPersonalId) {
		this.lookupUserByPersonalId = lookupUserByPersonalId;
	}*/

	
	/*protected boolean isSupplyUsernameAndPassword() {
		return supplyUsernameAndPassword;
	}

	
	protected void setSupplyUsernameAndPassword(boolean supplyUsernameAndPassword) {
		this.supplyUsernameAndPassword = supplyUsernameAndPassword;
	}*/

	public User lookupUserByEmail(String emailAddress) throws LoginModificationException {
		return lookupUserByEmail(emailAddress,false);
	}
	
	public User lookupUserByEmail(String emailAddress,boolean allowNoPreviousLogin) throws LoginModificationException {
		debug("Beginning lookup");
		if (emailAddress.length() == 0) {
			throw new LoginModificationException(NO_EMAIL);
		}
		User usr = null;
		try {
			UserHome uhome = (UserHome) com.idega.data.IDOLookup.getHome(User.class);
			usr = uhome.findUserFromEmail(emailAddress);
			if(!isAllowNoPreviousLogin()){
				LoginTable login = LoginDBHandler.getUserLogin(((Integer) usr.getPrimaryKey()).intValue());
				if (login == null) {
					throw new LoginModificationException(NO_USERNAME);
				}
			}
		}
		catch (FinderException ex) {
			//nothing found
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new LoginModificationException(NO_NAME);
		}
		return usr;
	}

	public User lookupUserByLogin(String loginName) throws LoginModificationException {
		debug("Beginning lookup");
		if (loginName.length() == 0) {
			throw new LoginModificationException(NO_LOGIN);
		}
		User usr = null;
		try {
			LoginTableHome home = (LoginTableHome) IDOLookup.getHome(LoginTable.class);
			try {
				LoginTable login = home.findByLogin(loginName);
				usr = Converter.convertToNewUser(login.getUser());
			}
			catch (FinderException fe) {
				throw new LoginModificationException(NO_LOGIN);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new LoginModificationException(NO_NAME);
		}
		return usr;
	}

	
	protected void sendNewLoginEmailToUser(IWContext iwc, User usr, String emailAddress) throws LoginModificationException {
		/*String sender = this.iwb.getProperty("forgotten.email_sender", "admin@idega.is");
		String server = this.iwb.getProperty("forgotten.email_server", "mail.idega.is");
		String subject = this.iwb.getProperty("forgotten.email_subject", "Forgotten password");
		if (sender == null || server == null || subject == null) {
			throw new LoginForgotException(NO_SERVER);
		}*/
		String subject = getEmailSubject();
		String letter = getEmailBody();
		if (letter == null) {
			throw new LoginModificationException(NO_LETTER);
		}
		sendNewLoginEmailToUser(iwc, usr, emailAddress, subject, letter);
	}

	protected void sendNewLoginEmailToUser(IWContext iwc, User usr, String emailAddress, String subject, String letter) throws LoginModificationException {
		if (usr != null) {
			String userName = null;
			String password = null;
			try {
				if(isAllowNoPreviousLogin()){
					UserBusiness userBusiness = getUserBusiness(iwc);
					LoginTable login = userBusiness.generateUserLogin(usr);
					userName = login.getUserLogin();
					password = login.getUnencryptedUserPassword();
				}
				else{
					LoginBusinessBean loginBean = LoginBusinessBean.getLoginBusinessBean(iwc);
					LoginContext context = loginBean.changeUserPassword(usr, LoginCreator.createPasswd(8));
					if(context!=null){
						userName = context.getUserName();
						password = context.getPassword();
					}
				}
				
			}
			catch (Exception ex) {
				ex.printStackTrace();
				throw new LoginModificationException(ILLEGAL_USERNAME);
			}
			System.err.println(usr.getName() + " has forgotten password");
			
			sendNewLoginEmailToUser(emailAddress, subject, letter, userName, password);
		}
	}

	protected void sendNewLoginEmailToUser(String emailAddress, String subject, String sBody, String userName, String password) {
		if (sBody != null) {
			Object[] objs = { userName, password };
			String body = MessageFormat.format(sBody, objs);
			try {
				EmailMessage message = new EmailMessage(subject,body.toString(),emailAddress);
				message.send();
				//SendMail.send(sender, emailAddress, "", "", server, subject, body.toString());
			}
			catch (javax.mail.MessagingException ex) {
				ex.printStackTrace();
			}
		}
		else{
			throw new RuntimeException("Message body is null when sending email");
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
	
	public boolean isAllowNoPreviousLogin() {
		return allowNoPreviousLogin;
	}

	
	public void setAllowNoPreviousLogin(boolean allowNoPreviousLogin) {
		this.allowNoPreviousLogin = allowNoPreviousLogin;
	}
}