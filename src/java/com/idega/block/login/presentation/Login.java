//idega 2000 Grimur Jonsson - Tryggvi Larusson - Thorhallur Helgason
/*
*Copyright 2000-2001 idega.is All Rights Reserved.
*/
package com.idega.block.login.presentation;
import java.util.HashMap;
import java.util.Map;

import com.idega.block.login.business.LoginBusiness;
import com.idega.block.login.business.LoginCookieListener;
import com.idega.builder.data.IBPage;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.data.LoginInfo;
import com.idega.core.user.data.User;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Script;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HelpButton;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.ui.PasswordInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.Window;
import com.idega.user.Converter;
import com.idega.user.data.Group;
/**
 * Title:        Login - The standard login block in idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>,<a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.1
 */
public class Login extends Block
{
	private Link loggedOnLink;
	private String backgroundImageUrl = "";
	private String newUserImageUrl = "";
	private String loginWidth = "";
	private String loginHeight = "";
	private String loginAlignment = "left";
	private String userText;
	private String passwordText;
	private String color = "";
	private String userTextColor = "";
	private String passwordTextColor;
	private int userTextSize = -1;
	private int passwordTextSize = -1;
	private int inputLength = 10;
	private int loggedOffPageId = -1;
	private String styleAttribute = "font-family: Verdana; font-size: 8pt; border: 1 solid #000000";
	private String textStyles =
		"font-family: Arial,Helvetica,sans-serif; font-size: 8pt; font-weight: bold; color: #000000; text-decoration: none;";
	private String submitButtonAlignment;
	private Form myForm;
	private Image loginImage;
	private Image logoutImage;
	private Image tryAgainImage;
	private boolean helpButton = false;
	private boolean onlyLogoutButton = false;
	private boolean register = false;
	private boolean forgot = false;
	private boolean _window;
	private int _logOnPage = -1;
	private int _redirectPage = -1;
	private Map groupPageMap;
	public static String controlParameter;
	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.block.login";
	public static final int LAYOUT_VERTICAL = 1;
	public static final int LAYOUT_HORIZONTAL = 2;
	public static final int LAYOUT_STACKED = 3;
	public static final int SINGLE_LINE = 4;
	private int LAYOUT = -1;
	protected IWResourceBundle iwrb;
	protected IWBundle iwb;
	private String loginHandlerClass = LoginBusiness.class.getName();
	protected boolean sendToHTTPS=false;
	protected boolean sendUserToHomePage=false;
  private boolean allowCookieLogin = false;

	private int _spaceBetween = 4;

	private boolean _buttonAsLink = false;
	private boolean _enterSubmit = false;
	private final String _linkStyleClass = "Link";
	private Image _iconImage;

	public Login()
	{
		super();
		setDefaultValues();
	}
	public void main(IWContext iwc) throws Exception
	{
		if (this._buttonAsLink) {
			if (getParentPage() != null) {
				Script script = null;
				if (getParentPage().getAssociatedScript() != null)
					script = getParentPage().getAssociatedScript();
				else {
					script = new Script();
					getParentPage().setAssociatedScript(script);
				}
				script.addFunction("enterSubmit", "function enterSubmit(myfield,e) { var keycode; if (window.event) keycode = window.event.keyCode; else if (e) keycode = e.which; else return true; if (keycode == 13) { myfield.form.submit(); return false; } else return true; }");
				_enterSubmit = true;
			}
		}
		
		myForm.setEventListener(loginHandlerClass);
    if(allowCookieLogin){
      iwc.getApplication().addApplicationEventListener(LoginCookieListener.class);
    }
		if(this.sendToHTTPS){
			myForm.setToSendToHTTPS();
		}
		iwb = getBundle(iwc);
		iwrb = getResourceBundle(iwc);
		if (loginImage == null)			//loginImage = iwrb.getImage("login.gif");
			loginImage = iwrb.getLocalizedImageButton("login_text", "Login");
		if (logoutImage == null)			//logoutImage = iwrb.getImage("logout.gif");
			logoutImage = iwrb.getLocalizedImageButton("logout_text", "Log out");
		if (tryAgainImage == null)			// tryAgainImage = iwrb.getImage("try_again.gif");
			tryAgainImage = iwrb.getLocalizedImageButton("tryagain_text", "Try again");
		userText = iwrb.getLocalizedString("user", "User");
		passwordText = iwrb.getLocalizedString("password", "Password");
		int state = internalGetState(iwc);
		switch (state) {
			case LoginBusiness.STATE_LOGGED_ON :
				isLoggedOn(iwc);
			break;
			case LoginBusiness.STATE_LOGGED_OUT :
				startState();
			break;
			case LoginBusiness.STATE_LOGIN_FAILED :
				loginFailed(iwrb.getLocalizedString("login_failed","Login failed"));
			break;
			case LoginBusiness.STATE_NO_USER :
				loginFailed(iwrb.getLocalizedString("login_no_user","Invalid user"));
			break;
			case LoginBusiness.STATE_WRONG_PASSW :
				loginFailed(iwrb.getLocalizedString("login_wrong","Invalid password"));
			break;
			case LoginBusiness.STATE_LOGIN_EXPIRED :
				loginFailed(iwrb.getLocalizedString("login_expired","Login expired"));
			break;
			default :
				startState();
			break;
		}
		
		add(myForm);
	}
	/*
	  public static boolean isAdmin(IWContext iwc)throws Exception{
	    return iwc.isAdmin();
	  }
	*/
	public static User getUser(IWContext iwc)
	{
		return LoginBusiness.getUser(iwc);
	}
	private void startState()
	{
		if (_logOnPage > 0)
		{
			myForm.setPageToSubmitTo(_logOnPage);
		}
		if (_redirectPage > 0)
		{
			//System.err.println("adding hidden redirect parameter");
			myForm.add(new HiddenInput(LoginBusiness.LoginRedirectPageParameter, String.valueOf(_redirectPage)));
		}
		Table loginTable = new Table();
		loginTable.setAlignment(loginAlignment);
		loginTable.setBorder(0);
		if (loginWidth != null)
			loginTable.setWidth(loginWidth);
		if (loginHeight != null)
			loginTable.setHeight(loginHeight);
		if (!(color.equals("")))
		{
			loginTable.setColor(color);
		}
		loginTable.setCellpadding(0);
		loginTable.setCellspacing(0);
		loginTable.setBackgroundImage(new Image(backgroundImageUrl));
		int ypos = 1;
		int xpos = 1;
		HelpButton helpImage =
			new HelpButton(
				iwrb.getLocalizedString("help_headline", "Web Access"),
				iwrb.getLocalizedString("help", ""),
				iwrb.getImage("help_image.gif").getURL());
		Text loginTexti = new Text(userText);
		if (userTextSize != -1)
		{
			loginTexti.setFontSize(userTextSize);
		}
		if (userTextColor != null)
		{
			loginTexti.setFontColor(userTextColor);
		}
		loginTexti.setFontStyle(textStyles);
		Text passwordTexti = new Text(passwordText);
		if (passwordTextSize != -1)
		{
			passwordTexti.setFontSize(passwordTextSize);
		}
		if (passwordTextColor != null)
		{
			passwordTexti.setFontColor(passwordTextColor);
		}
		passwordTexti.setFontStyle(textStyles);
		Table inputTable;
		TextInput login = new TextInput("login");
		login.setAttribute("style", styleAttribute);
		login.setSize(inputLength);
		if (_enterSubmit)
			login.setOnKeyPress("return enterSubmit(this,event)");
		PasswordInput passw = new PasswordInput("password");
		passw.setAttribute("style", styleAttribute);
		passw.setSize(inputLength);
		if (_enterSubmit)
			passw.setOnKeyPress("return enterSubmit(this,event)");
		switch (LAYOUT)
		{
			case LAYOUT_HORIZONTAL :
				inputTable = new Table(2, 2);
				inputTable.setBorder(0);
				if (!(color.equals("")))
				{
					inputTable.setColor(color);
				}
				inputTable.setCellpadding(1);
				inputTable.setCellspacing(0);
				inputTable.setAlignment("center");
				inputTable.add(loginTexti, 1, 1);
				inputTable.add(login, 1, 2);
				inputTable.add(passwordTexti, 2, 1);
				inputTable.add(passw, 2, 2);
				loginTable.add(inputTable, xpos, ypos);
				ypos++;
				break;
			case LAYOUT_VERTICAL :
				inputTable = new Table(3, 3);
				inputTable.setBorder(0);
				if (!(color.equals("")))
				{
					inputTable.setColor(color);
				}
				inputTable.setCellpadding(1);
				inputTable.setCellspacing(0);
				inputTable.setAlignment("center");
				inputTable.mergeCells(1, 2, 3, 2);
				inputTable.setHeight(2, "2");
				inputTable.setAlignment(1, 1, "right");
				inputTable.setAlignment(1, 3, "right");
				inputTable.add(loginTexti, 1, 1);
				inputTable.add(login, 3, 1);
				inputTable.add(passwordTexti, 1, 3);
				inputTable.add(passw, 3, 3);
				loginTable.add(inputTable, xpos, ypos);
				ypos++;
				break;
			case LAYOUT_STACKED :
				inputTable = new Table(1, 5);
				inputTable.setBorder(0);
				inputTable.setCellpadding(0);
				inputTable.setCellspacing(0);
				inputTable.setAlignment("center");
				inputTable.addText("", 1, 3);
				inputTable.setHeight(3, "5");
				if (!(color.equals("")))
				{
					inputTable.setColor(color);
				}
				inputTable.setAlignment(1, 1, "left");
				inputTable.setAlignment(1, 4, "left");
				inputTable.add(loginTexti, 1, 1);
				inputTable.add(login, 1, 2);
				inputTable.add(passwordTexti, 1, 4);
				inputTable.add(passw, 1, 5);
				loginTable.add(inputTable, xpos, ypos);
				ypos++;
				break;
			case SINGLE_LINE :
				inputTable = new Table(7, 1);
				inputTable.setBorder(0);
				inputTable.setCellpadding(0);
				inputTable.setCellspacing(0);
				inputTable.setAlignment("center");
				if (!(color.equals("")))
				{
					inputTable.setColor(color);
				}
				inputTable.setAlignment(1, 1, "right");
				inputTable.setAlignment(3, 1, "right");
				inputTable.setWidth(2,1,String.valueOf(_spaceBetween));
				inputTable.setWidth(4,1,String.valueOf(_spaceBetween * 2));
				inputTable.setWidth(6,1,String.valueOf(_spaceBetween));
				inputTable.add(loginTexti, 1, 1);
				inputTable.add(login, 3, 1);
				inputTable.add(passwordTexti, 5, 1);
				inputTable.add(passw, 7, 1);
				loginTable.add(inputTable, xpos, ypos);
				xpos = 2;
				break;
		}
		Table submitTable = new Table();
		//if ( helpButton ) {
		//  submitTable = new Table(2,1);
		//}
		submitTable.setBorder(0);
		if (!(color.equals("")))
		{
			submitTable.setColor(color);
		}
		submitTable.setRowVerticalAlignment(1, "middle");
		if (!helpButton)
		{
			submitTable.setAlignment(1, 1, submitButtonAlignment);
		}
		else
		{
			submitTable.setAlignment(2, 1, "right");
		}
		submitTable.setWidth("100%");
		if (_buttonAsLink) {
			submitTable.setCellpadding(0);
			submitTable.setCellspacing(0);
			int column = 1;
			Link link = this.getStyleLink(iwrb.getLocalizedString("login_text", "Login"), _linkStyleClass);
			link.setToFormSubmit(myForm);
			if (_iconImage != null) {
				submitTable.add(_iconImage,column++,1);
				submitTable.setWidth(column++,1,String.valueOf(_spaceBetween));
			}
			submitTable.add(link,column,1);
			loginTable.setWidth(xpos++,ypos,String.valueOf(_spaceBetween * 2));
			loginTable.add(submitTable, xpos, ypos);
		}
		else {
			SubmitButton button = new SubmitButton(loginImage, "tengja");
			if (!helpButton)
			{
				submitTable.add(button, 1, 1);
			}
			else
			{
				submitTable.add(button, 2, 1);
			}
			if (register || forgot || allowCookieLogin)
			{
				Link registerLink = getRegisterLink();
				Link forgotLink = getForgotLink();
				int row = 2;
				int col = 1;
				switch (LAYOUT)
				{
					case LAYOUT_HORIZONTAL :
					case LAYOUT_VERTICAL :
						row = 2;
						if (register)
							submitTable.add(registerLink, 1, row);
						if (forgot)
							submitTable.add(forgotLink, 2, row);
	          if(allowCookieLogin){
	            CheckBox cookieCheck = new CheckBox(LoginCookieListener.prmUserAllowsLogin);
	            Text cookieText = new Text(iwrb.getLocalizedString("cookie.allow", "Keep me signed in"));
			        cookieText.setFontStyle(this.textStyles);
	            row++;
	            submitTable.mergeCells(1, row, 2, row);
	            submitTable.add(cookieCheck,1,row);
	            submitTable.add(cookieText,1,row);
	          }
						break;
					case LAYOUT_STACKED :
						row = 2;
						if (register){
							submitTable.mergeCells(1, row, 2, row);
							submitTable.add(registerLink, 1, row);
							row++;
						}
						if (forgot){
							submitTable.mergeCells(1, row, 2, row);
							submitTable.add(forgotLink, 1, row);
	            row++;
						}
	          if(allowCookieLogin){
	            CheckBox cookieCheck = new CheckBox(LoginCookieListener.prmUserAllowsLogin);
	            Text cookieText = new Text(iwrb.getLocalizedString("cookie.allow", "Keep me signed in"));
			        cookieText.setFontStyle(this.textStyles);
	            submitTable.mergeCells(1, row, 2, row);
	            submitTable.add(cookieCheck,1,row);
	            submitTable.add(cookieText,1,row);
	            row++;
	          }
						break;
					case SINGLE_LINE :
						col = 3;
						if (register)
							submitTable.add(registerLink, col++, 1);
						if (forgot)
							submitTable.add(forgotLink, col++, 1);
	          if(allowCookieLogin){
	            CheckBox cookieCheck = new CheckBox(LoginCookieListener.prmUserAllowsLogin);
	            Text cookieText = new Text(iwrb.getLocalizedString("cookie.allow", "Keep me signed in"));
			        cookieText.setFontStyle(this.textStyles);
	            submitTable.add(cookieCheck,col,1);
	            submitTable.add(cookieText,col++,1);
	          }
						break;
				}
			}
			loginTable.add(submitTable, xpos, ypos);
		}

		submitTable.add(new Parameter(LoginBusiness.LoginStateParameter, "login"));
		myForm.add(loginTable);
	}


	private Link getRegisterLink()
	{
		Link L = new Link(iwrb.getLocalizedString("register.register", "Nýskráning"));
		L.setFontStyle(this.textStyles);
		L.setWindowToOpen(RegisterWindow.class);
		return L;
	}
	private Link getForgotLink()
	{
		Link L = new Link(iwrb.getLocalizedString("register.forgot", "Gleymt lykilorð"));
		L.setFontStyle(this.textStyles);
		L.setWindowToOpen(ForgotWindow.class);
		return L;
	}
	private void isLoggedOn(IWContext iwc) throws Exception
	{
		
		if (this.loggedOffPageId != -1)
			myForm.setPageToSubmitTo(loggedOffPageId);
		User user = (User) getUser(iwc);
		
		if ( sendUserToHomePage && LoginBusiness.isLogOnAction(iwc) ) {
			com.idega.user.data.User newUser = Converter.convertToNewUser(user);
			com.idega.user.data.Group newGroup = newUser.getPrimaryGroup();
			if ( newUser.getHomePageID() != -1 )
				iwc.forwardToIBPage(this.getParentPage(), newUser.getHomePage());
			if (newGroup != null && newGroup.getHomePageID() != -1 )
				iwc.forwardToIBPage(this.getParentPage(), newGroup.getHomePage());
		}

		if (loggedOnLink != null)
		{
			if (userTextSize > -1)
				loggedOnLink.setFontSize(userTextSize);
			if (userTextColor != null && !userTextColor.equals(""))
				loggedOnLink.setFontColor(userTextColor);
			loggedOnLink.setText(user.getName());
			loggedOnLink.setFontStyle(textStyles);
		}
		Text userText = new Text();
		if (userTextSize > -1)
		{
			userText.setFontSize(userTextSize);
		}
		if (userTextColor != null && !(userTextColor.equals("")))
		{
			userText.setFontColor(userTextColor);
		}
		userText.setFontStyle(textStyles);
		userText.addToText(user.getName());
		Table loginTable = new Table();
		loginTable.setBorder(0);
		loginTable.setBackgroundImage(new Image(backgroundImageUrl));
		loginTable.setAlignment(loginAlignment);
		if (loginWidth != null)
			loginTable.setWidth(loginWidth);
		if (loginHeight != null)
			loginTable.setHeight(loginHeight);
		loginTable.setCellpadding(0);
		loginTable.setCellspacing(0);
		if (!(color.equals("")))
		{
			loginTable.setColor(color);
		}
		if (this.LAYOUT != this.SINGLE_LINE)
		{
			loginTable.setHeight(1, "50%");
			loginTable.setHeight(2, "50%");
			loginTable.setVerticalAlignment(1, 1, "bottom");
			loginTable.setVerticalAlignment(1, 2, "top");
		}
		else
		{
			loginTable.setWidth(1, 1, "100%");
			loginTable.setCellpadding(3);
			loginTable.setAlignment(1, 1, "right");
		}
		Table inputTable = new Table(1, 1);
		if (!(color.equals("")))
		{
			inputTable.setColor(color);
		}
		inputTable.setBorder(0);
		inputTable.setCellpadding(0);
		inputTable.setCellspacing(0);
		if (LAYOUT != SINGLE_LINE)
		{
			inputTable.setAlignment(1, 1, "center");
			inputTable.setVerticalAlignment(1, 1, "middle");
			inputTable.setWidth("100%");
		}
		if (loggedOnLink != null)
		{
			inputTable.add(loggedOnLink);
		}
		else
		{
			inputTable.add(userText);
		}
		Table submitTable = new Table();
		submitTable.setBorder(0);
		if (!(color.equals("")))
		{
			submitTable.setColor(color);
		}
		if (LAYOUT != SINGLE_LINE)
		{
			submitTable.setAlignment(1, 1, "center");
			submitTable.setVerticalAlignment(1, 1, "middle");
		}
		if (onlyLogoutButton)
		{
			submitTable.setWidth(loginWidth);
			submitTable.setHeight(loginHeight);
			submitTable.setAlignment(loginAlignment);
		}
		else
		{
			submitTable.setWidth("100%");
		}
		if (_buttonAsLink) {
			submitTable.setCellpadding(0);
			submitTable.setCellspacing(0);
			loginTable.setCellpadding(0);
			int column = 1;
			Link link = this.getStyleLink(iwrb.getLocalizedString("logout_text", "Logoff"), _linkStyleClass);
			link.setToFormSubmit(myForm);
			if (_iconImage != null) {
				submitTable.add(_iconImage,column++,1);
				submitTable.setWidth(column++,1,String.valueOf(_spaceBetween));
			}
			submitTable.add(link,column,1);
		}
		else
			submitTable.add(new SubmitButton(logoutImage, "utskraning"));
			
		submitTable.add(new Parameter(LoginBusiness.LoginStateParameter, "logoff"));
		if (loggedOffPageId > 0)
			submitTable.add(new Parameter(getIBPageParameterName(), String.valueOf(loggedOffPageId)));
		if (LAYOUT != SINGLE_LINE)
		{
			loginTable.add(inputTable, 1, 1);
			loginTable.add(submitTable, 1, 2);
		}
		else
		{
			loginTable.add(inputTable, 1, 1);
			loginTable.add(submitTable, 2, 1);
		}
		if (onlyLogoutButton)
		{
			myForm.add(submitTable);
		}
		else
		{
			myForm.add(loginTable);
		}
		if(LoginBusiness.isLogOnAction(iwc)){
			LoginInfo loginInfo = LoginDBHandler.getLoginInfo((LoginDBHandler.findUserLogin(user.getID())).getID());
			if(loginInfo.getAllowedToChange() && loginInfo.getChangeNextTime()){
				Script s = new Script();
				LoginEditorWindow window = new LoginEditorWindow();
				window.setMessage(iwrb.getLocalizedString("change_password","You need to change your password"));
				window.setToChangeNextTime();
				s.addMethod("wop",window.getCallingScriptString(iwc));
				myForm.add(s);
			}
		}
	}
	private void loginFailed(String message)
	{
		Text mistokst = new Text(message);
		if (userTextSize != -1)
		{
			mistokst.setFontSize(userTextSize);
		}
		if (userTextColor != null)
		{
			mistokst.setFontColor(userTextColor);
		}
		mistokst.setFontStyle(textStyles);
		Table loginTable = new Table();
		loginTable.setBorder(0);
		loginTable.setBackgroundImage(new Image(backgroundImageUrl));
		loginTable.setAlignment(loginAlignment);
		if (loginWidth != null)
			loginTable.setWidth(loginWidth);
		if (loginHeight != null)
			loginTable.setHeight(loginHeight);
		loginTable.setCellpadding(0);
		loginTable.setCellspacing(0);
		if (!(color.equals("")))
		{
			loginTable.setColor(color);
		}
		if (this.LAYOUT != this.SINGLE_LINE)
		{
			loginTable.setHeight(1, "50%");
			loginTable.setHeight(2, "50%");
			loginTable.setVerticalAlignment(1, 1, "bottom");
			loginTable.setVerticalAlignment(1, 2, "top");
		}
		else
		{
			//loginTable.setWidth(1, 1, "100%");
			loginTable.setCellpadding(3);
			loginTable.setAlignment(1, 1, "right");
		}
		Table inputTable = new Table(1, 1);
		inputTable.setBorder(0);
		if (!(color.equals("")))
		{
			inputTable.setColor(color);
		}
		inputTable.setCellpadding(0);
		inputTable.setCellspacing(0);
		if (LAYOUT != SINGLE_LINE)
		{
			inputTable.setAlignment(1, 1, "center");
			inputTable.setVerticalAlignment(1, 1, "middle");
			inputTable.setWidth("100%");
		}
		inputTable.add(mistokst, 1, 1);
		Table submitTable = new Table();
		submitTable.setBorder(0);
		if (!(color.equals("")))
		{
			submitTable.setColor(color);
		}
		if (LAYOUT != SINGLE_LINE)
		{
			submitTable.setAlignment(1, 1, "center");
			submitTable.setVerticalAlignment(1, 1, "middle");
			submitTable.setWidth("100%");
		}
		if (_buttonAsLink) {
			submitTable.setCellpadding(0);
			submitTable.setCellspacing(0);
			loginTable.setCellpadding(0);
			int column = 1;
			Link link = this.getStyleLink(iwrb.getLocalizedString("tryagain_text", "Try again"), _linkStyleClass);
			link.setToFormSubmit(myForm);
			if (_iconImage != null) {
				submitTable.add(_iconImage,column++,1);
				submitTable.setWidth(column++,1,String.valueOf(_spaceBetween));
			}
			submitTable.add(link,column,1);
		}
		else
			submitTable.add(new SubmitButton(tryAgainImage, "tryAgain"));
		submitTable.add(new Parameter(LoginBusiness.LoginStateParameter, "tryagain"));
		if (LAYOUT != SINGLE_LINE)
		{
			loginTable.add(inputTable, 1, 1);
			loginTable.add(submitTable, 1, 2);
		}
		else
		{
			int column = 1;
			loginTable.add(inputTable, column++, 1);
			if (_buttonAsLink)
				loginTable.setWidth(column++,1,String.valueOf(_spaceBetween * 2));
			loginTable.add(submitTable, column, 1);
		}
		myForm.add(loginTable);
	}
	private void isNotSignedOn(String what)
	{
		Text textinn = new Text("");
		textinn.setFontSize(1);
		textinn.setBold();
		if (what.equals("empty"))
		{
			textinn.addToText(iwrb.getLocalizedString("write_ssn", "Type social-security number in user input"));
		}
		else if (what.equals("toBig"))
		{
			textinn.addToText(
				iwrb.getLocalizedString("without_hyphen", "Social-security number must be written without a hyphen"));
		}
		textinn.setFontStyle(textStyles);
		Table loginTable = new Table(1, 2);
		loginTable.setBackgroundImage(new com.idega.presentation.Image(backgroundImageUrl));
		loginTable.setAlignment("center");
		if (loginWidth != null)
			loginTable.setWidth(loginWidth);
		if (loginHeight != null)
			loginTable.setHeight(loginHeight);
		loginTable.setBorder(0);
		if (!(color.equals("")))
		{
			loginTable.setColor(color);
		}
		loginTable.setCellpadding(0);
		loginTable.setCellspacing(0);
		loginTable.setColor(1, 2, "#FFFFFF");
		Table inputTable = new Table(1, 1);
		if (!(color.equals("")))
		{
			inputTable.setColor(color);
		}
		inputTable.setBorder(0);
		inputTable.setCellpadding(0);
		inputTable.setCellspacing(0);
		if (LAYOUT != SINGLE_LINE)
		{
			inputTable.setAlignment(1, 1, "center");
			inputTable.setVerticalAlignment(1, 1, "middle");
			inputTable.setWidth("100%");
		}
		inputTable.add(textinn, 1, 1);
		Table submitTable = new Table(1, 1);
		if (!(color.equals("")))
		{
			submitTable.setColor(color);
		}
		submitTable.setBorder(0);
		if (LAYOUT != SINGLE_LINE)
		{
			submitTable.setAlignment(1, 1, "center");
			submitTable.setVerticalAlignment(1, 1, "middle");
			submitTable.setWidth("100%");
		}
		submitTable.add(new SubmitButton(tryAgainImage), 1, 1);
		if (LAYOUT != SINGLE_LINE)
		{
			loginTable.add(inputTable, 1, 1);
			loginTable.add(submitTable, 1, 2);
		}
		else
		{
			loginTable.add(inputTable, 1, 1);
			loginTable.add(submitTable, 2, 1);
		}
		myForm.add(loginTable);
	}
	public int internalGetState(IWContext iwc)
	{
		return LoginBusiness.internalGetState(iwc);
	}
	public String getBundleIdentifier()
	{
		return IW_BUNDLE_IDENTIFIER;
	}
	public void setLayout(int layout)
	{
		LAYOUT = layout;
	}
	protected void setDefaultValues()
	{
		submitButtonAlignment = "center";
		LAYOUT = LAYOUT_VERTICAL;
		myForm = new Form();
		//myForm.setEventListener(loginHandlerClass);
		myForm.setMethod("post");
		//myForm.maintainAllParameters();
	}
	/**
	 * Sets the login handler business class which this class sends the login/logout event to.<br><br>
	 * This Class must implement com.idega.event.IWEventHandler.<br>
	 * The default is LoginBusiness
	 */
	public void setLoginHandlerClass(String className)
	{
		this.loginHandlerClass = className;
		/*if (myForm != null)
		{
			myForm.setEventListener(className);
		}*/
	}
	/**
	 * Sets the login handler business class which this class sends the login/logout event to.<br><br>
	 * This Class must implement com.idega.event.IWEventHandler.<br>
	 * The default is LoginBusiness
	 */
	public void setLoginHandlerClass(Class handlerClass)
	{
		setLoginHandlerClass(handlerClass.getName());
	}
	public void addHelpButton()
	{
		helpButton = true;
	}
	public void setHelpButton(boolean usehelp)
	{
		helpButton = usehelp;
	}
	public void setVertical()
	{
		LAYOUT = LAYOUT_VERTICAL;
	}
	public void setHorizontal()
	{
		LAYOUT = LAYOUT_HORIZONTAL;
	}
	public void setStacked()
	{
		LAYOUT = this.LAYOUT_STACKED;
	}
	public void setStyle(String styleAttribute)
	{
		setInputStyle(styleAttribute);
	}
	public void setInputStyle(String styleAttribute)
	{
		this.styleAttribute = styleAttribute;
	}
	public void setTextStyle(String styleAttribute)
	{
		this.textStyles = styleAttribute;
	}
	public void setInputLength(int inputLength)
	{
		this.inputLength = inputLength;
	}
	public void setUserText(String text)
	{
		userText = text;
	}
	public void setUserTextSize(int size)
	{
		userTextSize = size;
	}
	public void setUserTextColor(String color)
	{
		userTextColor = color;
	}
	public void setPasswordText(String text)
	{
		passwordText = text;
	}
	public void setPasswordTextSize(int size)
	{
		passwordTextSize = size;
	}
	public void setPasswordTextColor(String color)
	{
		passwordTextColor = color;
	}
	public void setColor(String color)
	{
		this.color = color;
	}
	public void setHeight(String height)
	{
		loginHeight = height;
	}
	public void setWidth(String width)
	{
		loginWidth = width;
	}
	public void setBackgroundImageUrl(String url)
	{
		backgroundImageUrl = url;
	}
	public void setSubmitButtonAlignment(String alignment)
	{
		submitButtonAlignment = alignment;
	}
	public void setLoginButton(Image loginImage)
	{
		this.loginImage = loginImage;
	}
	public void setLogoutButton(Image logoutImage)
	{
		this.logoutImage = logoutImage;
	}
	public void setTryAgainButton(Image tryAgainImage)
	{
		this.tryAgainImage = tryAgainImage;
	}
	public void setViewOnlyLogoutButton(boolean logout)
	{
		onlyLogoutButton = logout;
	}
	public void setLoginAlignment(String alignment)
	{
		loginAlignment = alignment;
	}
	public void setLoggedOnLink(Link link)
	{
		loggedOnLink = (Link) link.clone();
	}
	public void setLogOnPage(IBPage page)
	{
		_logOnPage = page.getID();
	}
	public void setLogOnPage(int page)
	{
		_logOnPage = page;
	}
	public void setLoggedOnWindow(boolean window)
	{
		if (window)
		{
			loggedOnLink = new Link();
			loggedOnLink.setWindowToOpen(LoginEditorWindow.class);
		}
	}
	public void setLoggedOnPage(IBPage page)
	{
		loggedOnLink = new Link();
		loggedOnLink.setPage(page);
	}
	public void setLoggedOffPage(int ibPageId)
	{
		loggedOffPageId = ibPageId;
	}
	public void setLoggedOffPage(IBPage page)
	{
		loggedOffPageId = page.getID();
	}
	public void setRegister(boolean register)
	{
		this.register = register;
	}
	public void setForgot(boolean forgot)
	{
		this.forgot = forgot;
	}

  public void setAllowCookieLogin(boolean cookies){
    this.allowCookieLogin = cookies;
  }

	/** todo: implement */
	public void setRedirectPage(int page)
	{
		System.err.println("setting redirect page");
		_redirectPage = page;
	}
	public void setGroupForwardPage(Group group, IBPage page)
	{
		try
		{
			getGroupPageMap().put(group.getPrimaryKey().toString(), page.getPrimaryKey().toString());
		}
		catch (Exception ex)
		{}
	}
	private Map getGroupPageMap()
	{
		if (groupPageMap == null)
		{
			groupPageMap = new HashMap();
		}
		return groupPageMap;
	}
	public Object clone()
	{
		Login obj = null;
		try
		{
			obj = (Login) super.clone();
			if (this.myForm != null)
			{
				obj.myForm = (Form) this.myForm.clone();
			}
			if (this.loginImage != null)
			{
				obj.loginImage = (Image) this.loginImage.clone();
			}
			if (this.logoutImage != null)
			{
				obj.logoutImage = (Image) this.logoutImage.clone();
			}
			if (this.tryAgainImage != null)
			{
				obj.tryAgainImage = (Image) this.tryAgainImage.clone();
			}
			if (this.loggedOnLink != null)
			{
				obj.loggedOnLink = (Link) this.loggedOnLink.clone();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace(System.err);
		}
		return obj;
	}

	/**
	 * Set the form to automatically send over to a corresponding HTTPS address
	 **/
	public void setToSendToHTTPS(){
		setToSendToHTTPS(true);
	}

	/**
	 * Set if the form should automatically send over to a corresponding HTTPS address
	 **/
	public void setToSendToHTTPS(boolean doSendToHTTPS){
		if(myForm!=null){
			myForm.setToSendToHTTPS(doSendToHTTPS);
		}
		sendToHTTPS=doSendToHTTPS;
	}


	/**
	 * Set if the form should send the user to his home page after login.
	 **/
	public void setToSendUserToHomePage(boolean doSendToHomePage){
		sendUserToHomePage=doSendToHomePage;
	}

	/**
	 * @see com.idega.presentation.Block#getStyleNames()
	 */
	public Map getStyleNames() {
		Map styleMap = new HashMap();
		styleMap.put(_linkStyleClass, "");
		styleMap.put(_linkStyleClass+":hover", "");
		return styleMap;
	}

	/**
	 * Sets the submit button as link.
	 * @param buttonAsLink The buttonAsLink to set
	 */
	public void setButtonAsLink(boolean buttonAsLink) {
		_buttonAsLink = buttonAsLink;
	}

	/**
	 * Sets the spaceBetween.
	 * @param spaceBetween The spaceBetween to set
	 */
	public void setSpaceBetween(int spaceBetween) {
		_spaceBetween = spaceBetween;
	}

	/**
	 * Sets the iconImage.
	 * @param iconImage The iconImage to set
	 */
	public void setIconImage(Image iconImage) {
		_iconImage = iconImage;
	}

}
