package com.idega.block.login.business;

import javax.servlet.http.Cookie;

import com.idega.presentation.*;
import com.idega.core.accesscontrol.business.*;
import com.idega.event.IWPageEventListener;
import com.idega.idegaweb.*;
import com.idega.util.CypherText;

/**
 * Title:        LoginCookieListener
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */


public class LoginCookieListener implements IWPageEventListener{

  public String userIDCookieName = "iwrbusid";
  public String IW_BUNDLE_IDENTIFIER = "com.idega.block.login";
  public static final String prmUserAllowsLogin ="icusallows";

  public boolean actionPerformed(IWContext iwc)throws IWException{
    Cookie userIDCookie = iwc.getCookie(userIDCookieName);
    //System.err.println("actionPerformed in LoginCookieListener");
    if( LoginBusinessBean.isLogOffAction(iwc) &&  userIDCookie!=null){
      userIDCookie.setMaxAge(0);
      iwc.addCookies(userIDCookie);
    }
    else if(!iwc.isLoggedOn()){
      //System.err.println("no user is logged on");
      if(userIDCookie!=null){
        //System.err.println("found the cookie");
        String cypheredLoginName = userIDCookie.getValue();
        String loginName = deCypherUserLogin(iwc,cypheredLoginName);
        try{
          new LoginBusinessBean().logInUnVerified(iwc,loginName);
        }
        catch(Exception ex){
          throw new IWException("Cookie login failed : "+ex.getMessage());
        }
      }
      else{//System.err.println("no cookie found");
      }
    }
    else if(iwc.isParameterSet(prmUserAllowsLogin) && LoginBusinessBean.isLoggedOn(iwc)){
      if(userIDCookie==null){
        //System.err.println("adding cookie");
        String login = LoginBusinessBean.getLoggedOnInfo(iwc).getLogin();
        userIDCookie = new Cookie(userIDCookieName,cypherUserLogin(iwc,login));
        userIDCookie.setMaxAge(60*60*24*30);
        iwc.addCookies(userIDCookie);
      }
    }
    return true;
  }

  public String getCypherKey(IWApplicationContext iwc) {
    IWBundle iwb = iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    CypherText cyph = new CypherText();

    String cypherKey = iwb.getProperty("cypherKey");
    if ((cypherKey == null) || (cypherKey.equalsIgnoreCase(""))) {
      cypherKey = cyph.getKey(100);
      iwb.setProperty("cypherKey",cypherKey);
    }

    return(cypherKey);
  }

  protected String cypherUserLogin(IWApplicationContext iwc,String userLogin){
    String key = getCypherKey(iwc);
    String cypheredId = new CypherText().doCyper(userLogin,key);
    //System.err.println("Cyphered "+userLogin +"to "+cypheredId);
    return cypheredId;
  }

  protected String deCypherUserLogin(IWApplicationContext iwc,String cypheredLogin){
    String key = getCypherKey(iwc);
    return new CypherText().doDeCypher(cypheredLogin,key);
  }

}
