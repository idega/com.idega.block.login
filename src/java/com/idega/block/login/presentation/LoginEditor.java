package com.idega.block.login.presentation;



/**

 * Title:

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      idega multimedia

 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>

 * @version 1.0

 */



import java.sql.SQLException;

import com.idega.core.user.data.User;

import com.idega.core.accesscontrol.business.LoginDBHandler;

import com.idega.core.accesscontrol.data.LoginTable;

import com.idega.presentation.text.*;

import com.idega.presentation.*;

import com.idega.idegaweb.IWResourceBundle;

import com.idega.idegaweb.IWBundle;

import com.idega.presentation.ui.*;

import com.idega.block.login.business.LoginBusiness;





public class LoginEditor extends PresentationObjectContainer{



  private User eUser = null;

  private String sUnionId = null;



  private String errorMsg = "";

  public static String prmUserId = "user_id";

  protected String MiddleColor,LightColor,DarkColor,WhiteColor,TextFontColor,HeaderFontColor,IndexFontColor;

  protected int fontSize = 2;

  protected boolean fontBold = true;

  protected String styleAttribute = "font-size: 8pt";





  private final static String IW_BUNDLE_IDENTIFIER="com.idega.block.login";

  protected IWResourceBundle iwrb;

  protected IWBundle iwb;



  public String getBundleIdentifier(){

    return IW_BUNDLE_IDENTIFIER;

  }



  public LoginEditor(){

    LightColor = "#D7DADF";

    MiddleColor = "#9fA9B3";

    DarkColor = "#27334B";

    WhiteColor = "#FFFFFF";

    TextFontColor = "#000000";

    HeaderFontColor = DarkColor;

    IndexFontColor = "#000000";

  }



  public LoginEditor(int iUserId){

    this();

    try {

      eUser = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(iUserId);

    }

    catch (SQLException ex) {

      eUser = null;

    }

  }



  protected void control(IWContext iwc){

    String sUserId = iwc.getParameter(prmUserId);

    if(eUser == null)

      eUser = LoginBusiness.getUser(iwc);

    if(eUser!=null){

      String userlogin = null;

      boolean check = false;

      if(iwc.getParameter("ok")!=null || iwc.getParameter("ok.x")!=null ){

        check = doAddTo(iwc,eUser.getID());

      }

      userlogin = getUsrLogin(eUser.getID());



      //add((iwrb.getLocalizedString("login","Login")));

      add(doView(eUser,userlogin));

    }

    else{

      errorMsg =iwrb.getLocalizedString("non_user","Non-user");

    }

    add(getMsgText(this.errorMsg));

  }



  private Text getMsgText(String msg){

    Text t = formatText(msg);

    t.setFontColor("#FF0000");

    return t;

  }



  protected PresentationObject makeLinkTable(int menuNr){

    return new Text("");

  }



  private String getUsrLogin(int mbid){

    String userLogin = getUserLogin(mbid);

    if(userLogin != null)

      return userLogin;

    else

      return iwrb.getLocalizedString("has_no_login","Has no login");

  }



  private boolean doAddTo(IWContext iwc,int iUserId){

    String sLogin = iwc.getParameter("ml.usrlgn");

    String sPasswd = iwc.getParameter("ml.psw1");

    String sConfirm = iwc.getParameter("ml.psw2");

    boolean register = false;

    if(sLogin != null && sPasswd != null && sConfirm != null){

      if(sLogin.length() > 0  && sPasswd.length() > 0 && sConfirm.length() > 0){

        try{

          register = registerMemberLogin(iUserId,sLogin,sPasswd,sConfirm);

        }

        catch(SQLException sql){

          sql.printStackTrace();

          register = false;

          errorMsg = iwrb.getLocalizedString("database_trouble","database_trouble");

        }

      }

      else

        this.errorMsg = iwrb.getLocalizedString("empty_fields","Empty fields");;

    }

    return register;

  }



  private PresentationObject doView(User user,String sUserLogin){

    Table T = new Table();

    T.add(formatText(user.getName()),1,2);



    TextInput tUsrLgn = new TextInput("ml.usrlgn",sUserLogin);

    this.setStyle(tUsrLgn);

    PasswordInput psw1 = new PasswordInput("ml.psw1");

    this.setStyle(psw1);

    PasswordInput psw2 = new PasswordInput("ml.psw2");

    this.setStyle(psw2);



    T.add(formatText(iwrb.getLocalizedString("login","Login")+":"),1,3);

    T.add(tUsrLgn,1,4);

    T.add(formatText(iwrb.getLocalizedString("passwd","Passwd")+":"),1,5);

    T.add(psw1,1,6);

    T.add(formatText(iwrb.getLocalizedString("confirm","Confirm")+":"),1,7);

    T.add(psw2,1,8);



    SubmitButton ok = new SubmitButton(iwrb.getLocalizedImageButton("save","Save"),"ok");

    CloseButton close = new CloseButton(iwrb.getLocalizedImageButton("close","Close"));

    T.add(ok,1,9);

    T.add(Text.NON_BREAKING_SPACE,1,9);

    T.add(close,1,9);

    T.add(new HiddenInput(prmUserId,String.valueOf(user.getID())));

    Form myForm = new Form();

    myForm.add(T);



    return myForm;

  }



  public boolean registerMemberLogin(int iUserId,String sUserLogin,String sPasswd,String sConfirm) throws SQLException {

    boolean returner = false;



    if (sPasswd.equals(sConfirm)) {

      LoginTable logTable = LoginDBHandler.getUserLogin(iUserId);

      if (logTable == null) {

        try {

          if (sPasswd.equals(sConfirm) ) {

            LoginDBHandler.createLogin(iUserId,sUserLogin,sPasswd);

            returner = true;

            errorMsg = iwrb.getLocalizedString("login_created","Login created");

          }

        }

        catch (Exception ex) {

          ex.printStackTrace();

          returner = false;

          //errorMsg = iwrb.getLocalizedString("creation_failed","Failed");

          errorMsg = ex.getMessage();

        }

      }

      else if (logTable != null  ) {



          try {

            if (sPasswd.equals(sConfirm) ) {

              LoginDBHandler.updateLogin(iUserId,sUserLogin,sPasswd);

              returner = true;

              errorMsg =  iwrb.getLocalizedString("updated","Login updated");

            }

          }

          catch (Exception ex) {

            ex.printStackTrace();

            returner = false;

            //errorMsg = iwrb.getLocalizedString("update_failed","Update Failed");

            errorMsg = ex.getMessage();

          }





      }

    }

    else

      errorMsg = iwrb.getLocalizedString("wrong_confirm","Confirm failed");;



    return returner;

  }



  public String getUserLogin(int iUserId){

    LoginTable L = LoginDBHandler.getUserLogin(iUserId);

    if(L != null )

      return L.getUserLogin();

    else

      return null;

  }



  public Text formatText(String s){

    Text T= new Text();

    if(s!=null){

      T= new Text(s);

      if(this.fontBold)

      T.setBold();

      T.setFontColor(this.TextFontColor);

      T.setFontSize(this.fontSize);

    }

    return T;

  }

  public Text formatText(int i){

    return formatText(String.valueOf(i));

  }

  protected void setStyle(InterfaceObject O){

    O.setAttribute("style",this.styleAttribute);

  }



  public void main(IWContext iwc){

    iwrb = getResourceBundle(iwc);

    if(LoginBusiness.isLoggedOn(iwc))

      control(iwc);

    else

      add(iwrb.getLocalizedString("not logged on","Not logged on"));

  }

}

