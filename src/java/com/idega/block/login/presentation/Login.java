//idega 2000 Grimur Jonsson - Tryggvi Larusson
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
String userTextSize = "";

String passwordText = "";
String passwordTextColor = "";
String passwordTextSize = "";
boolean vertical = false;
boolean noLoginImage=true;
String styleAttribute = "font-size: 10pt";
int inputLength = 10;
public static String controlParameter;

	public Login() {
		super();
		setDefaultValues();
	}

	private void setDefaultValues() {
		loginImageUrl="/pics/templates/tengjast2.gif";
		newUserImageUrl="/pics/templates/nyskraning2.gif";
		loginWidth="148";
		loginHeight="89";
		userText = "Notandi";
		passwordText = "Lykilorð";
//		color = "#FFFFFF";
	}

	public void setVertical() {
		vertical = true;
	}

	public void setHorizontal() {
		vertical = false;
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
	public void setUserTextSize(String size) {
		userTextSize = size;
	}
	public void setUserTextColor(String color) {
		userTextColor = color;
	}
	public void setPasswordText(String text) {
		passwordText = text;
	}

	public void setPasswordTextSize(String size) {
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
	public static boolean isAdmin(ModuleInfo modinfo)throws Exception{
            return AccessControl.isAdmin(modinfo);
	}

/*       public boolean isDeveloper(ModuleInfo modinfo)throws Exception{
		User user = getUser(modinfo);
	    if (user != null){
         Group[] access = user.getGroups();
         for(int i = 0; i < access.length; i++){
           if ("developer".equals(access[i].getName()))
             return true;
         }
         }
         return false;
       }

       public boolean isClubAdmin(ModuleInfo modinfo)throws Exception{
        User user = getUser(modinfo);
		if (user != null){
         Group[] access = user.getGroups();
         for(int i = 0; i < access.length; i++){
           if ("club_admin".equals(access[i].getName()))
             return true;
         }
         }
         return false;
       }

       public boolean isUser(ModuleInfo modinfo)throws Exception{
		User user = getUser(modinfo);
         if (user != null){
          Group[] access = user.getGroups();
          for(int i = 0; i < access.length; i++){
             if ("user".equals(access[i].getName()))
               return true;
           }
         }
         return false;
       }
*/

	public static User getUser(ModuleInfo modinfo){
		return LoginBusiness.getUser(modinfo);
	}


	private void startState(){
		Form myForm = new Form();
                //myForm.submitTo(this);
                myForm.setEventListener(LoginBusiness.class.getName());
			myForm.setMethod("post");
			myForm.maintainAllParameters();

/*		Table EinnTable = new Table(2,1);
			EinnTable.setBorder(0);
			EinnTable.setWidth(1,"148");
			EinnTable.setWidth(2,"20");
			EinnTable.setHeight("89");
			EinnTable.setVerticalAlignment(2,1,"top");
			EinnTable.setCellpadding(0);
			EinnTable.setCellspacing(0);

		Table haegri = new Table(1,2);
			haegri.setBackgroundImage(1,1,new Image("/pics/templates/loginHorn.gif"));
			haegri.setCellpadding(0);
			haegri.setCellspacing(0);
			haegri.setWidth(1,1,"19");
			haegri.setHeight(1,1,"20");
			EinnTable.add(haegri,2,1);
*/
		Table TveirTable = new Table(1,1);
//			TveirTable.setBorder(1);
			TveirTable.setBackgroundImage(1,1,new com.idega.jmodule.object.Image(backgroundImageUrl));
			TveirTable.setAlignment(1,1,"center");
			if (!(color.equals(""))) {
			TveirTable.setColor(color);
			}
			TveirTable.setWidth(loginWidth);
			TveirTable.setHeight(loginHeight);
			TveirTable.setCellpadding(0);
			TveirTable.setCellspacing(0);
//			TveirTable.setHeight(1,1,"89");
//			EinnTable.add(TveirTable,1,1);

		Table TrirTable = new Table(1,2);
			TrirTable.setBorder(0);
			TrirTable.setWidth("100%");
//			TrirTable.setWidth("146");
			TrirTable.setHeight("100%");
			if (!(color.equals(""))) {
			TrirTable.setColor(color);
			}
			TrirTable.setCellpadding(0);
			TrirTable.setCellspacing(0);
			TrirTable.setBackgroundImage(1,2,new com.idega.jmodule.object.Image(""));
//			TrirTable.setColor(1,2,"#FFFFFF");


			Text loginTexti = new Text(userText);
				if (!(userTextSize.equals(""))) {
					loginTexti.setFontSize(Integer.parseInt(userTextSize));
				}
				if (!(userTextColor.equals(""))) {
					loginTexti.setFontColor(userTextColor);
				}
			Text passwordTexti = new Text(passwordText);
				if (!(passwordTextSize.equals(""))) {
					passwordTexti.setFontSize(Integer.parseInt(passwordTextSize));
				}
				if (!(passwordTextColor.equals(""))) {
					passwordTexti.setFontColor(passwordTextColor);
				}

		Table uppi;


		if (!(vertical)) {
			uppi = new Table(5,2);
			uppi.setBorder(0);
			if (!(color.equals(""))) {
			uppi.setColor(color);
			}
			uppi.setCellpadding(0);
			uppi.setCellspacing(0);
			uppi.setAlignment(2,1,"right");
			uppi.setAlignment(2,2,"right");
			uppi.setWidth("100%");
			TrirTable.add(uppi,1,1);

			uppi.add(loginTexti,2,1);
			TextInput login = new TextInput("login");
				login.setAttribute("style",styleAttribute);
				login.setSize(inputLength);
			uppi.add(login,2,2);
			uppi.setAlignment(2,1,"right");
			uppi.setAlignment(2,2,"right");

			uppi.add(passwordTexti,4,1);
			PasswordInput passw = new PasswordInput("password");
				passw.setAttribute("style",styleAttribute);
				passw.setSize(inputLength);
			uppi.add(passw,4,2);


		}
		else {
			uppi = new Table(3,3);
			uppi.setBorder(0);
			if (!(color.equals(""))) {
			uppi.setColor(color);
			}
			uppi.setCellpadding(0);
			uppi.setCellspacing(0);
			uppi.setAlignment("center");
                        uppi.mergeCells(1,2,3,2);
                        uppi.addText("",1,2);
                        uppi.setHeight(2,"10");
//			uppi.setWidth("100%");
			TrirTable.add(uppi,1,1);


			uppi.add(loginTexti,1,1);
			TextInput login = new TextInput("login");
				login.setAttribute("style",styleAttribute);
				login.setSize(inputLength);
			uppi.add(login,3,1);
			uppi.setAlignment(1,1,"right");
//			uppi.setAlignment(2,2,"right");

			uppi.add(passwordTexti,1,3);
			PasswordInput passw = new PasswordInput("password");
				passw.setAttribute("style",styleAttribute);
				passw.setSize(inputLength);
			uppi.add(passw,3,3);
                        uppi.setAlignment(1,3,"right");
		}


		Table nidri = new Table(2,1);
			nidri.setBorder(0);
			if (!(color.equals(""))) {
			nidri.setColor(color);
			}
			nidri.setVerticalAlignment(1,1,"middle");
			nidri.setAlignment(1,1,"center");
			nidri.setWidth("100%");
			nidri.setHeight("100%");
			nidri.mergeCells(1,1,2,1);
			TrirTable.add(nidri,1,2);



                        if(noLoginImage){
                          nidri.add(new SubmitButton("tengja","Login"),1,1);
                          nidri.add(new Parameter(LoginBusiness.LoginStateParameter,"login"));

                        }
                        else{
                          nidri.add(new SubmitButton(new com.idega.jmodule.object.Image(loginImageUrl),"tengja"),1,1);
                          nidri.add(new Parameter(LoginBusiness.LoginStateParameter,"login"));
                        }


		TveirTable.add(TrirTable);

	myForm.add(TveirTable);
//	myForm.add(EinnTable);
	add(myForm);
	}


	private void isLoggedOn(ModuleInfo modinfo) throws Exception{

		Text userText = new Text();
//			user.setBold();
			if (!(userTextSize.equals(""))) {
				userText.setFontSize(Integer.parseInt(userTextSize));
			}
			if (!(userTextColor.equals(""))) {
				userText.setFontColor(userTextColor);
			}

		User user = (User) getUser(modinfo);


                userText.addToText(user.getName());

		Text hlekkur = new Link(userText,"");  //  new Link(userText,"/test/nyskraning.jsp?kt="+user.getSocialSecurityNumber());

		Form myForm = new Form();
                //myForm.submitTo(this);
                myForm.setEventListener(LoginBusiness.class.getName());
/*		Table EinnTable = new Table(2,1);
			EinnTable.setBorder(0);
			EinnTable.setWidth(1,"148");
			EinnTable.setWidth(2,"20");
			EinnTable.setHeight("89");
			EinnTable.setVerticalAlignment(2,1,"top");
			EinnTable.setCellpadding(0);
			EinnTable.setCellspacing(0);

		Table haegri = new Table(1,2);
			haegri.setBackgroundImage(1,1,new Image("/pics/templates/loginHorn.gif"));
			haegri.setCellpadding(0);
			haegri.setCellspacing(0);
			haegri.setWidth(1,1,"19");
			haegri.setHeight(1,1,"20");
			EinnTable.add(haegri,2,1);
*/
		Table TveirTable = new Table(1,1);
//			TveirTable.setBorder(1);
			if (!(color.equals(""))) {
			TveirTable.setColor(color);
			}
			TveirTable.setBackgroundImage(1,1,new com.idega.jmodule.object.Image(backgroundImageUrl));
			TveirTable.setAlignment(1,1,"center");
			TveirTable.setWidth(loginWidth);
			TveirTable.setHeight(loginHeight);
			TveirTable.setCellpadding(0);
			TveirTable.setCellspacing(0);
//			TveirTable.setHeight(1,1,"89");
//			EinnTable.add(TveirTable,1,1);

		Table TrirTable = new Table(1,2);
			TrirTable.setBorder(0);
			TrirTable.setWidth("100%");
//			TrirTable.setWidth("146");
//			TrirTable.setHeight("87");
			TrirTable.setCellpadding(0);
			TrirTable.setCellspacing(0);
			TrirTable.setBackgroundImage(1,2,new com.idega.jmodule.object.Image(""));
			if (!(color.equals(""))) {
			TrirTable.setColor(color);
			}
//			TrirTable.setColor(1,2,"#FFFFFF");
			TveirTable.add(TrirTable,1,1);

		Table uppi = new Table(1,1);
			if (!(color.equals(""))) {
				uppi.setColor(color);
			}
			uppi.setBorder(0);
			uppi.setCellpadding(0);
			uppi.setCellspacing(0);
			uppi.setAlignment(1,1,"center");
			uppi.setVerticalAlignment(1,1,"middle");
//			uppi.setHeight("43");
			uppi.setWidth("100%");
			TrirTable.add(uppi,1,1);

		Table nidri = new Table(1,1);
			nidri.setBorder(0);
			if (!(color.equals(""))) {
				nidri.setColor(color);
			}
			nidri.setAlignment(1,1,"center");
			nidri.setVerticalAlignment(1,1,"middle");
			nidri.setWidth("100%");
			TrirTable.add(nidri,1,2);


		uppi.add(user);
//		uppi.add(hlekkur);

		if (logOutImageUrl.equals("")) {
			nidri.add(new SubmitButton("action","Útskráning"));
                        nidri.add(new Parameter(LoginBusiness.LoginStateParameter,"logoff"));
		}
		else {
			com.idega.jmodule.object.Image logOut = new com.idega.jmodule.object.Image(logOutImageUrl);
			nidri.add(new SubmitButton(logOut,"utskraning"));
                        nidri.add(new Parameter(LoginBusiness.LoginStateParameter,"logoff"));
		}
//		myForm.add(EinnTable);
		myForm.add(TveirTable);
		add(myForm);
	}

	private void loginFailed() {

		Form myForm = new Form();
                //myForm.submitTo(this);
                myForm.setEventListener(LoginBusiness.class.getName());

/*		Table EinnTable = new Table(2,1);
			EinnTable.setBorder(0);
			EinnTable.setWidth(1,"148");
			EinnTable.setWidth(2,"20");
			EinnTable.setHeight("89");
			EinnTable.setVerticalAlignment(2,1,"top");
			EinnTable.setCellpadding(0);
			EinnTable.setCellspacing(0);

		Table haegri = new Table(1,2);
			haegri.setBackgroundImage(1,1,new Image("/pics/templates/loginHorn.gif"));
			haegri.setCellpadding(0);
			haegri.setCellspacing(0);
			haegri.setWidth(1,1,"19");
			haegri.setHeight(1,1,"20");
			EinnTable.add(haegri,2,1);
*/
		Table TveirTable = new Table(1,1);
//			TveirTable.setBorder(1);
			if (!(color.equals(""))) {
				TveirTable.setColor(color);
			}
			TveirTable.setBackgroundImage(1,1,new com.idega.jmodule.object.Image(backgroundImageUrl));
			TveirTable.setAlignment(1,1,"center");
			TveirTable.setWidth(loginWidth);
			TveirTable.setHeight(loginHeight);
			TveirTable.setCellpadding(0);
			TveirTable.setCellspacing(0);
//			TveirTable.setHeight(1,1,"89");
//			EinnTable.add(TveirTable,1,1);

		Table TrirTable = new Table(1,2);
			TrirTable.setBorder(0);
			TrirTable.setWidth("100%");
//			TrirTable.setWidth("146");
//			TrirTable.setHeight("87");
			TrirTable.setCellpadding(0);
			TrirTable.setCellspacing(0);
			TrirTable.setBackgroundImage(1,2,new com.idega.jmodule.object.Image(""));
			if (!(color.equals(""))) {
				TrirTable.setColor(color);
			}
//			TrirTable.setColor(1,2,"#FFFFFF");
			TveirTable.add(TrirTable,1,1);

		Table uppi = new Table(1,1);
			uppi.setBorder(0);
			if (!(color.equals(""))) {
				uppi.setColor(color);
			}
			uppi.setCellpadding(0);
			uppi.setCellspacing(0);
			uppi.setAlignment(1,1,"center");
			uppi.setVerticalAlignment(1,1,"middle");
//			uppi.setHeight("43");
			uppi.setWidth("100%");
			TrirTable.add(uppi,1,1);

		Table nidri = new Table(1,1);
			nidri.setBorder(0);
			if (!(color.equals(""))) {
				nidri.setColor(color);
			}
			nidri.setAlignment(1,1,"center");
			nidri.setVerticalAlignment(1,1,"middle");
			nidri.setWidth("100%");
			TrirTable.add(nidri,1,2);


		Text mistokst = new Text("Innskráning mistókst");
			if (!(userTextSize.equals(""))) {
				mistokst.setFontSize(Integer.parseInt(userTextSize));
			}
			if (!(userTextColor.equals(""))) {
				mistokst.setFontColor(userTextColor);
			}


		uppi.add(mistokst,1,1);

		if (tryAgainImageUrl.equals("")) {
			nidri.add(new SubmitButton("Reyna aftur"),1,1);
                        nidri.add(new Parameter(LoginBusiness.LoginStateParameter,"tryagain"));
		}
		else {
			com.idega.jmodule.object.Image tryAgain = new com.idega.jmodule.object.Image(tryAgainImageUrl);
			nidri.add(new SubmitButton(tryAgain,"tryAgain"));
                        nidri.add(new Parameter(LoginBusiness.LoginStateParameter,"tryagain"));
		}

//		myForm.add(EinnTable);
		myForm.add(TveirTable);
		add(myForm);

	}
	private void isNotSignedOn(String what) {

		Form myForm = new Form();
                //myForm.submitTo(this);
                myForm.setEventListener(LoginBusiness.class.getName());

/*		Table EinnTable = new Table(2,1);
			EinnTable.setBorder(0);
			EinnTable.setWidth(1,"148");
			EinnTable.setWidth(2,"20");
			EinnTable.setHeight("89");
			EinnTable.setVerticalAlignment(2,1,"top");
			EinnTable.setCellpadding(0);
			EinnTable.setCellspacing(0);

		Table haegri = new Table(1,2);
			haegri.setBackgroundImage(1,1,new Image("/pics/templates/loginHorn.gif"));
			haegri.setCellpadding(0);
			haegri.setCellspacing(0);
			haegri.setWidth(1,1,"19");
			haegri.setHeight(1,1,"20");
			EinnTable.add(haegri,2,1);
*/
		Table TveirTable = new Table(1,1);
//			TveirTable.setBorder(1);
			if (!(color.equals(""))) {
				TveirTable.setColor(color);
			}
			TveirTable.setBackgroundImage(1,1,new com.idega.jmodule.object.Image(backgroundImageUrl));
			TveirTable.setAlignment(1,1,"center");
			TveirTable.setWidth(loginWidth);
			TveirTable.setHeight(loginHeight);
			TveirTable.setCellpadding(0);
			TveirTable.setCellspacing(0);
//			TveirTable.setHeight(1,1,"89");
//			EinnTable.add(TveirTable,1,1);

		Table TrirTable = new Table(1,2);
			TrirTable.setBorder(0);
			TrirTable.setWidth("100%");
//			TrirTable.setWidth("146");
//			TrirTable.setHeight("87");
			if (!(color.equals(""))) {
				TrirTable.setColor(color);
			}
			TrirTable.setCellpadding(0);
			TrirTable.setCellspacing(0);
			TrirTable.setBackgroundImage(1,2,new com.idega.jmodule.object.Image(""));
			TrirTable.setColor(1,2,"#FFFFFF");
			TveirTable.add(TrirTable,1,1);

		Table uppi = new Table(1,1);
			if (!(color.equals(""))) {
				uppi.setColor(color);
			}
			uppi.setBorder(0);
			uppi.setCellpadding(0);
			uppi.setCellspacing(0);
			uppi.setAlignment(1,1,"center");
			uppi.setVerticalAlignment(1,1,"middle");
//			uppi.setHeight("43");
			uppi.setWidth("100%");
			TrirTable.add(uppi,1,1);

		Table nidri = new Table(1,1);
			if (!(color.equals(""))) {
				nidri.setColor(color);
			}
			nidri.setBorder(0);
			nidri.setAlignment(1,1,"center");
			nidri.setVerticalAlignment(1,1,"middle");
			nidri.setWidth("100%");
			TrirTable.add(nidri,1,2);



		Text textinn = new Text("");
			textinn.setFontSize(1);
			textinn.setBold();
		if (what.equals("empty")) {
			textinn.addToText("Skrifið kennitölu í notandareitinn");
		}
		else if (what.equals("toBig")) {
			textinn.addToText("Kennitala skal vera skrifuð án bandstriks");
		}
		uppi.add(textinn,1,1);
		nidri.add(new SubmitButton("Reyna aftur"),1,1);

//		myForm.add(EinnTable);
		myForm.add(TveirTable);
		add(myForm);

	}


        public void main(ModuleInfo modinfo)throws Exception{
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
        }



        public String internalGetState(ModuleInfo modinfo){
            return LoginBusiness.internalGetState(modinfo);
        }



}
