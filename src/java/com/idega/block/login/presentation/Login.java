//idega 2000 Grimur Jonsson - Tryggvi Larusson - Thorhallur Helgason
/*
*Copyright 2000-2001 idega.is All Rights Reserved.
*/

package com.idega.block.login.presentation;

import com.idega.core.user.data.User;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;
import java.util.*;
import com.idega.block.login.business.*;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;

/**
 * Title:        Login
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>,<a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.1
 */

public class Login extends JModuleObject{

String backgroundImageUrl = "";
String loginImageUrl = "";
String newUserImageUrl = "";
String logOutImageUrl = "";
String tryAgainImageUrl = "";
String loginWidth = "";
String loginHeight = "";
String color = "";
String loggedOnTextSize = "";
String loggedOnTextColor = "";
String userText = "";
String userTextColor = "";
int userTextSize = -1;
String passwordText = "";
String passwordTextColor = "";
int passwordTextSize = -1;
String styleAttribute = "font-size: 10pt";
int inputLength = 10;
boolean noLoginImage=true;
String submitButtonAlignment = "";

private Table outerTable;
private Form myForm;

public static String controlParameter;

public static final int LAYOUT_VERTICAL = 1;
public static final int LAYOUT_HORIZONTAL = 2;
public static final int LAYOUT_STACKED = 3;

private int LAYOUT = -1;

	public Login() {
		super();
		setDefaultValues();
	}

  public void main(ModuleInfo modinfo)throws Exception{
    IWBundle iwb = getBundle(modinfo);
    IWResourceBundle iwrb = getResourceBundle(modinfo);

    String state = internalGetState(modinfo);
    if(state!=null){
      if(state.equals("loggedon")){
        isLoggedOn(modinfo);
      }
      else if(state.equals("loggedoff")){
        startState();
      }
      else if(state.equals("loginfailed")){
        loginFailed();
      }
      else{
        startState();
      }
    }
    else{
      startState();
    }

    outerTable.add(myForm);
    add(outerTable);
  }

  public static boolean isAdmin(ModuleInfo modinfo)throws Exception{
    return AccessControl.isAdmin(modinfo);
	}

	public static User getUser(ModuleInfo modinfo){
		return LoginBusiness.getUser(modinfo);
	}

	private void startState(){
		Table loginTable = new Table(1,2);
			loginTable.setAlignment("center");
			loginTable.setBorder(0);
			loginTable.setWidth(loginWidth);
			loginTable.setHeight(loginHeight);
			if (!(color.equals(""))) {
			  loginTable.setColor(color);
			}
			loginTable.setCellpadding(0);
			loginTable.setCellspacing(0);
			loginTable.setBackgroundImage(new Image(backgroundImageUrl));

    Text loginTexti = new Text(userText);
      if ( userTextSize != -1 ) {
        loginTexti.setFontSize(userTextSize);
      }
      if (!(userTextColor.equals(""))) {
        loginTexti.setFontColor(userTextColor);
      }
    Text passwordTexti = new Text(passwordText);
      if ( passwordTextSize != -1 ) {
        passwordTexti.setFontSize(passwordTextSize);
      }
      if (!(passwordTextColor.equals(""))) {
        passwordTexti.setFontColor(passwordTextColor);
      }

		Table inputTable;

    TextInput login = new TextInput("login");
      login.setAttribute("style",styleAttribute);
      login.setSize(inputLength);

    PasswordInput passw = new PasswordInput("password");
      passw.setAttribute("style",styleAttribute);
      passw.setSize(inputLength);

    switch (LAYOUT) {
      case LAYOUT_HORIZONTAL:
        inputTable = new Table(5,2);
          inputTable.setBorder(0);
          if (!(color.equals(""))) {
          inputTable.setColor(color);
          }
          inputTable.setCellpadding(0);
          inputTable.setCellspacing(0);
          inputTable.setAlignment(2,1,"right");
          inputTable.setAlignment(2,2,"right");
          inputTable.setWidth("100%");


        inputTable.add(loginTexti,2,1);
        inputTable.add(login,2,2);
        inputTable.setAlignment(2,1,"right");
        inputTable.setAlignment(2,2,"right");
        inputTable.add(passwordTexti,4,1);
        inputTable.add(passw,4,2);

        loginTable.add(inputTable,1,1);
        break;

      case LAYOUT_VERTICAL:
        inputTable = new Table(3,3);
          inputTable.setBorder(0);
          if (!(color.equals(""))) {
          inputTable.setColor(color);
          }
          inputTable.setCellpadding(0);
          inputTable.setCellspacing(0);
          inputTable.setAlignment("center");
          inputTable.mergeCells(1,2,3,2);
          inputTable.addText("",1,2);
          inputTable.setHeight(2,"10");
          inputTable.setAlignment(1,1,"right");
          inputTable.setAlignment(1,3,"right");

        inputTable.add(loginTexti,1,1);
        inputTable.add(login,3,1);
        inputTable.add(passwordTexti,1,3);
        inputTable.add(passw,3,3);

        loginTable.add(inputTable,1,1);
        break;

      case LAYOUT_STACKED:
        inputTable = new Table(1,5);
          inputTable.setBorder(0);
          inputTable.setCellpadding(0);
          inputTable.setCellspacing(0);
          inputTable.setAlignment("center");
          inputTable.addText("",1,3);
          inputTable.setHeight(3,"5");
          if (!(color.equals(""))) {
            inputTable.setColor(color);
          }
          inputTable.setAlignment(1,1,"left");
          inputTable.setAlignment(1,4,"left");

        inputTable.add(loginTexti,1,1);
        inputTable.add(login,1,2);
        inputTable.add(passwordTexti,1,4);
        inputTable.add(passw,1,5);

        loginTable.add(inputTable,1,1);
        break;
		}


		Table submitTable = new Table(1,1);
			submitTable.setBorder(0);
			if (!(color.equals(""))) {
  			submitTable.setColor(color);
			}
			submitTable.setVerticalAlignment(1,1,"middle");
			submitTable.setAlignment(1,1,submitButtonAlignment);
			submitTable.setWidth("100%");
			submitTable.setHeight("100%");

      if(noLoginImage){
        submitTable.add(new SubmitButton("tengja","Login"),1,1);
        submitTable.add(new Parameter(LoginBusiness.LoginStateParameter,"login"));

      }
      else{
        submitTable.add(new SubmitButton(new Image(loginImageUrl),"tengja"),1,1);
        submitTable.add(new Parameter(LoginBusiness.LoginStateParameter,"login"));
      }

    loginTable.add(submitTable,1,2);

    myForm.add(loginTable);
	}


	private void isLoggedOn(ModuleInfo modinfo) throws Exception{
		User user = (User) getUser(modinfo);

		Text userText = new Text();
			if ( userTextSize != -1 ) {
				userText.setFontSize(userTextSize);
			}
			if (!(userTextColor.equals(""))) {
				userText.setFontColor(userTextColor);
			}

      userText.addToText(user.getName());

		Table loginTable = new Table(1,2);
			loginTable.setBorder(0);
			loginTable.setBackgroundImage(new Image(backgroundImageUrl));
			loginTable.setAlignment("center");
			loginTable.setWidth(loginWidth);
			loginTable.setHeight(loginHeight);
      loginTable.setHeight(1,"50%");
      loginTable.setHeight(2,"50%");
			loginTable.setCellpadding(0);
			loginTable.setCellspacing(0);
      loginTable.setVerticalAlignment(1,1,"bottom");
      loginTable.setVerticalAlignment(1,2,"top");
			if (!(color.equals(""))) {
        loginTable.setColor(color);
			}

		Table inputTable = new Table(1,1);
			if (!(color.equals(""))) {
				inputTable.setColor(color);
			}
			inputTable.setBorder(0);
			inputTable.setCellpadding(0);
			inputTable.setCellspacing(0);
			inputTable.setAlignment(1,1,"center");
			inputTable.setVerticalAlignment(1,1,"middle");
			inputTable.setWidth("100%");

  		inputTable.add(userText);

		Table submitTable = new Table(1,1);
			submitTable.setBorder(0);
			if (!(color.equals(""))) {
				submitTable.setColor(color);
			}
			submitTable.setAlignment(1,1,"center");
			submitTable.setVerticalAlignment(1,1,"middle");
			submitTable.setWidth("100%");

      if (logOutImageUrl.equals("")) {
        submitTable.add(new SubmitButton("action","Útskráning"));
        submitTable.add(new Parameter(LoginBusiness.LoginStateParameter,"logoff"));
      }
      else {
        Image logOut = new Image(logOutImageUrl);
        submitTable.add(new SubmitButton(logOut,"utskraning"));
        submitTable.add(new Parameter(LoginBusiness.LoginStateParameter,"logoff"));
      }

    loginTable.add(inputTable,1,1);
    loginTable.add(submitTable,1,2);

		myForm.add(loginTable);
	}

	private void loginFailed() {
		Text mistokst = new Text("Innskráning mistókst");
			if ( userTextSize != -1 ) {
				mistokst.setFontSize(userTextSize);
			}
			if (!(userTextColor.equals(""))) {
				mistokst.setFontColor(userTextColor);
			}

		Table loginTable = new Table(1,2);
			loginTable.setBackgroundImage(new com.idega.jmodule.object.Image(backgroundImageUrl));
			loginTable.setAlignment("center");
			loginTable.setWidth(loginWidth);
			loginTable.setHeight(loginHeight);
      loginTable.setHeight(1,"50%");
      loginTable.setHeight(2,"50%");
			loginTable.setBorder(0);
			loginTable.setCellpadding(0);
			loginTable.setCellspacing(0);
      loginTable.setVerticalAlignment(1,1,"bottom");
      loginTable.setVerticalAlignment(1,2,"top");
			if (!(color.equals(""))) {
				loginTable.setColor(color);
			}

		Table inputTable = new Table(1,1);
			inputTable.setBorder(0);
			if (!(color.equals(""))) {
				inputTable.setColor(color);
			}
			inputTable.setCellpadding(0);
			inputTable.setCellspacing(0);
			inputTable.setAlignment(1,1,"center");
			inputTable.setVerticalAlignment(1,1,"middle");
			inputTable.setWidth("100%");

  		inputTable.add(mistokst,1,1);

		Table submitTable = new Table(1,1);
			submitTable.setBorder(0);
			if (!(color.equals(""))) {
				submitTable.setColor(color);
			}
			submitTable.setAlignment(1,1,"center");
			submitTable.setVerticalAlignment(1,1,"middle");
			submitTable.setWidth("100%");

      if (tryAgainImageUrl.equals("")) {
        submitTable.add(new SubmitButton("Reyna aftur"),1,1);
        submitTable.add(new Parameter(LoginBusiness.LoginStateParameter,"tryagain"));
      }
      else {
        Image tryAgain = new Image(tryAgainImageUrl);
        submitTable.add(new SubmitButton(tryAgain,"tryAgain"));
        submitTable.add(new Parameter(LoginBusiness.LoginStateParameter,"tryagain"));
      }

    loginTable.add(submitTable,1,2);
    loginTable.add(inputTable,1,1);

		myForm.add(loginTable);
	}

	private void isNotSignedOn(String what) {
		Text textinn = new Text("");
			textinn.setFontSize(1);
			textinn.setBold();
      if (what.equals("empty")) {
        textinn.addToText("Skrifið kennitölu í notandareitinn");
      }
      else if (what.equals("toBig")) {
        textinn.addToText("Kennitala skal vera skrifuð án bandstriks");
      }

		Table loginTable = new Table(1,2);
			loginTable.setBackgroundImage(new com.idega.jmodule.object.Image(backgroundImageUrl));
			loginTable.setAlignment("center");
			loginTable.setWidth(loginWidth);
			loginTable.setHeight(loginHeight);
			loginTable.setBorder(0);
			if (!(color.equals(""))) {
				loginTable.setColor(color);
			}
			loginTable.setCellpadding(0);
			loginTable.setCellspacing(0);
			loginTable.setColor(1,2,"#FFFFFF");

		Table inputTable = new Table(1,1);
			if (!(color.equals(""))) {
				inputTable.setColor(color);
			}
			inputTable.setBorder(0);
			inputTable.setCellpadding(0);
			inputTable.setCellspacing(0);
			inputTable.setAlignment(1,1,"center");
			inputTable.setVerticalAlignment(1,1,"middle");
			inputTable.setWidth("100%");

  		inputTable.add(textinn,1,1);

		Table submitTable = new Table(1,1);
			if (!(color.equals(""))) {
				submitTable.setColor(color);
			}
			submitTable.setBorder(0);
			submitTable.setAlignment(1,1,"center");
			submitTable.setVerticalAlignment(1,1,"middle");
			submitTable.setWidth("100%");

		  submitTable.add(new SubmitButton("Reyna aftur"),1,1);

    loginTable.add(inputTable,1,1);
    loginTable.add(submitTable,1,2);

		myForm.add(loginTable);
	}

  public String internalGetState(ModuleInfo modinfo){
      return LoginBusiness.internalGetState(modinfo);
  }

  public void setLayout(int layout) {
    LAYOUT = layout;
  }

	private void setDefaultValues() {
		loginImageUrl="/pics/templates/tengjast2.gif";
		newUserImageUrl="/pics/templates/nyskraning2.gif";
		loginWidth="148";
		loginHeight="89";
		userText = "Notandi";
		passwordText = "Lykilorð";
    submitButtonAlignment = "center";
    LAYOUT = LAYOUT_VERTICAL;

    outerTable = new Table();
      outerTable.setCellpadding(0);
      outerTable.setCellspacing(0);
      outerTable.setAlignment("center");

    myForm = new Form();
      myForm.setEventListener(LoginBusiness.class.getName());
			myForm.setMethod("post");
			myForm.maintainAllParameters();
	}

	public void setVertical() {
		LAYOUT = LAYOUT_VERTICAL;
	}

	public void setHorizontal() {
		LAYOUT = LAYOUT_HORIZONTAL;
	}

	public void setStacked() {
		LAYOUT = this.LAYOUT_STACKED;
	}

	public void setStyle(String styleAttribute){
    this.styleAttribute=styleAttribute;
  }

  public void setInputLength(int inputLength) {
    this.inputLength=inputLength;
  }

  public void setLoggedOnTextSize(String size) {
		loggedOnTextSize = size;
	}
	public void setLoggedOnTextColor(String color) {
		loggedOnTextColor = color;
	}

	public void setUserText(String text) {
		userText = text;
	}

  public void setUserTextSize(int size) {
		userTextSize = size;
	}

  public void setUserTextColor(String color) {
		userTextColor = color;
	}

  public void setPasswordText(String text) {
		passwordText = text;
	}

	public void setPasswordTextSize(int size) {
		passwordTextSize = size;
	}

	public void setPasswordTextColor(String color) {
		passwordTextColor = color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setHeight(String height) {
		loginHeight = height;
	}

	public void setWidth(String width) {
		loginWidth = width;
	}

	public void setBackgroundImageUrl(String url) {
		backgroundImageUrl = url;
	}

	public void setLoginImageUrl(String url) {
    noLoginImage=false;
		loginImageUrl = url;
	}

	public void setNewUserImageUrl(String url) {
    noLoginImage=false;
		newUserImageUrl = url;
	}

	public void setTryAgainImageUrl(String url) {
		tryAgainImageUrl = url;
	}

	public void setLogOutImageUrl(String url) {
		logOutImageUrl = url;
	}

  public void setSubmitButtonAlignment(String alignment) {
    submitButtonAlignment = alignment;
  }

}
