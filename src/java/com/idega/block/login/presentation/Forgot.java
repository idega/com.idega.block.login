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
import com.idega.core.user.data.*;
import com.idega.core.data.Email;
import com.idega.core.user.business.UserBusiness;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.presentation.text.*;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.PresentationObject;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.ui.*;
import com.idega.block.login.business.LoginBusiness;
import com.idega.block.login.business.LoginContext;
import com.idega.block.login.business.LoginCreator;
import com.idega.util.SendMail;
import java.text.MessageFormat;
import com.idega.util.text.TextFormat;


public class Forgot extends Block{
  private String errorMsg = "";

  public static String prmUserId = "user_id";
  private final static String IW_BUNDLE_IDENTIFIER="com.idega.block.login";
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
  private String portalname = "";
  private TextFormat form;

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  protected void control(IWContext iwc){
    //debugParameters(iwc);
    portalname = iwc.getServerName();
    form = TextFormat.getInstance();
    int code = INIT;
    if(iwc.isParameterSet("send.x"))
      code = processForm(iwc);
    if(code == NORMAL)
      add(getSent(iwc));
    else
      add(getForm(iwc,code));
  }

  private int processForm(IWContext iwc){
    String realName = iwc.getParameter("reg_userrealname");
    String userEmail = iwc.getParameter("reg_user_email");
    String userName = iwc.getParameter("reg_username") ;
    int code = NORMAL;
    if(userEmail!=null){
      code = lookupUser(userEmail);
    }
    return code;
  }

  private PresentationObject getSent(IWContext iwc){
    Table T = new Table();
    T.add(iwrb.getLocalizedString("forgotten.sent_message","Your login and password has been sent"));
    return T;
  }

  private PresentationObject getForm(IWContext iwc,int code){
    Table T = new Table(2,6);
    String manual = iwrb.getLocalizedString("forgotten.manual","Enter your username and a new password will be sent to your registered email address");
    String textUserEmail = iwrb.getLocalizedString("forgotten.user_email","Email");
    TextInput inputUserEmail = new TextInput("reg_user_email");

    if(iwc.isParameterSet("reg_user_email")){
      inputUserEmail.setContent(iwc.getParameter("reg_user_email"));
    }
    T.mergeCells(1,1,2,1);
    T.add(form.format(manual),1,1);

    T.add(form.format(textUserEmail),1,3);
    T.add(inputUserEmail,2,3);
    String message = getMessage(code);
    if(message!=null)
      T.add(form.format(message,"#ff0000"),1,5);
    //System.err.println(code+" : "+message);
    SubmitButton ok = new SubmitButton(iwrb.getLocalizedImageButton("send","Send"),"send");
    CloseButton close = new CloseButton(iwrb.getLocalizedImageButton("close","Close"));
    T.add(ok,2,6);
    T.add(close,2,6);
    Form myForm = new Form();
    myForm.add(T);
    return myForm;
  }

  public PresentationObject getAnswer(){
    Table T = new Table(1,1);
    T.setVerticalAlignment("center");
    T.setAlignment("center");
    T.add(iwrb.getLocalizedString("forgotten.done","Your login and password has been sent to you."));
    return T;
  }

  public int lookupUser(String emailAddress){
    System.err.println("Beginning lookup");
    int internal = NORMAL;
    if( emailAddress.length() == 0 )
      return NO_EMAIL;
    /*
    LoginTable login =  LoginDBHandler.getUserLoginByUserName(userName);
    if(login == null)
      return NO_USERNAME;
    */
    String sender = iwb.getProperty("forgotten.email_sender","admin@idega.is");
    String server = iwb.getProperty("forgotten.email_server","mail.idega.is");
    String subject = iwb.getProperty("forgotten.email_subject","Forgotten password");
    if(sender==null || server == null || subject == null)
      return NO_SERVER;
    //QuestionHome qhome = (QuestionHome)IDOLookup.getHome(Question.class);
    User u = null;
    try{
      UserHome uhome = (UserHome) com.idega.data.IDOLookup.getHome(User.class);
      u = uhome.findUserFromEmail(emailAddress);
      LoginTable login = LoginDBHandler.getUserLogin(((Integer)u.getPrimaryKey()).intValue());
      if(login== null)
        return NO_USERNAME;
    }
    catch(Exception ex){
      ex.printStackTrace();
      return NO_NAME;
    }

    LoginContext context = null;
    if(u!=null){
      try{
         context = LoginBusiness.changeUserPassword(u,LoginCreator.createPasswd(8));
      }
      catch(Exception ex){
        ex.printStackTrace();
        return ILLEGAL_USERNAME;
      }

      System.err.println(u.getName()+" has forgotten password");
      String letter = iwrb.getLocalizedString("forgotten.email_body","Username : {0} \nPassword: {1} ");
      if(letter == null)
        return NO_LETTER;

      if(letter !=null && context !=null){
        Object[] objs = {context.getUserName(),context.getPassword()};
        String body = MessageFormat.format(letter,objs);


        try{
          SendMail.send(sender,emailAddress,"","",server,subject,body.toString());
        }
        catch(javax.mail.MessagingException ex){
          ex.printStackTrace();
        }
        return NORMAL;
      }
    }
    return internal;
  }

  public Email getUserEmail(User user){
    java.util.Collection emails = null;
    try{
    com.idega.core.data.EmailHome emailhome = (com.idega.core.data.EmailHome)com.idega.data.IDOLookup.getHome(com.idega.core.data.Email.class);
    emails = emailhome.findEmailsForUser(((Integer)user.getPrimaryKey()).intValue());
    }
    catch(Exception ex){
      ex.printStackTrace();
    }
    if(emails !=null && emails.size() > 0)
      return (com.idega.core.data.Email) emails.iterator().next();
    return null;
  }

  public String getMessage(int code){
    String msg = null;
    switch (code) {
      case NORMAL:  iwrb.getLocalizedString("register.NORMAL","NORMAL");              break;
      case USER_NAME_EXISTS:  msg =   iwrb.getLocalizedString("register.USER_NAME_EXISTS","USER_NAME_EXISTS");    break;
      case ILLEGAL_USERNAME:  msg =  iwrb.getLocalizedString("register.ILLEGAL_USERNAME","ILLEGAL_USERNAME");     break;
      case ILLEGAL_EMAIL:  msg = iwrb.getLocalizedString("register.ILLEGAL_EMAIL","ILLEGAL_EMAIL");         break;
      case NO_NAME:   msg = iwrb.getLocalizedString("register.NO_NAME","NO_NAME");              break;
      case NO_EMAIL:   msg = iwrb.getLocalizedString("register.NO_EMAIL","NO_EMAIL");             break;
      case NO_USERNAME:   msg = iwrb.getLocalizedString("register.NO_USERNAME","NO_USER");          break;
      case NO_SERVER:  msg = iwrb.getLocalizedString("register.NO_SERVER","NO_SERVER");             break;
      case ERROR:  msg = iwrb.getLocalizedString("register.ERROR","ERROR");           break;
      case SENT:    msg = iwrb.getLocalizedString("register.SENT","SENT");           break;
    }
    return msg;
  }

  public void main(IWContext iwc){
    iwb = getBundle(iwc);
    iwrb = getResourceBundle(iwc);
    control(iwc);
  }
}