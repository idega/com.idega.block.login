//idega 2000 - Tryggvi Larusson - Grimur Jonsson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.block.login.business;


import com.idega.presentation.*;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.LoginInfo;
import com.idega.core.accesscontrol.data.PermissionGroup;
import com.idega.core.data.GenericGroup;
import com.idega.core.user.data.UserGroupRepresentative;
import com.idega.core.user.data.User;
import com.idega.core.business.UserGroupBusiness;
import java.sql.*;
import java.io.*;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.business.IWEventListener;
import com.idega.idegaweb.IWException;
import com.idega.util.Encrypter;
import java.util.Hashtable;
import java.util.List;

/**
 * Title:        LoginBusiness
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>,<a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.1
 */


public class LoginBusiness implements IWEventListener{

  public static String UserAttributeParameter="user_login";
  public static String PermissionGroupParameter="user_permission_groups";
  public static String LoginStateParameter="login_state";
  private static String LoginAttributeParameter="login_attributes";
  private static String UserGroupRepresentativeParameter = "ic_user_representative_group";
  private static String PrimaryGroupsParameter = "ic_user_primarygroups";
  private static String PrimaryGroupParameter = "ic_user_primarygroup";

  public LoginBusiness() {
  }


  public static boolean isLoggedOn(IWContext iwc){
      if(iwc.getSessionAttribute(LoginAttributeParameter)==null){
        return false;
      }
      return true;
  }

  public static void internalSetState(IWContext iwc,String state){
      iwc.setSessionAttribute(LoginStateParameter,state);
  }

  public static String internalGetState(IWContext iwc){
      return (String) iwc.getSessionAttribute(LoginStateParameter);
  }

  public void actionPerformed(IWContext iwc)throws IWException{
        //System.out.println("LoginBusiness.actionPerformed");

        try{

            if(isLoggedOn(iwc)){
                  String controlParameter = iwc.getParameter(LoginBusiness.LoginStateParameter);
                  if (controlParameter != null) {

                        if(controlParameter.equals("logoff")){
                            logOut(iwc);
                            internalSetState(iwc,"loggedoff");

                        }

                  }


            }
            else{
                  String controlParameter = iwc.getParameter(LoginBusiness.LoginStateParameter);

                  if (controlParameter != null) {
                      if(controlParameter.equals("login")){

                                boolean canLogin = false;
				if ((iwc.getParameter("login") != null) && (iwc.getParameter("password") != null)) {
                                        canLogin = verifyPasswordAndLogin(iwc, iwc.getParameter("login"),iwc.getParameter("password"));
					if (canLogin) {
                                          isLoggedOn(iwc);
                                          internalSetState(iwc,"loggedon");
					}
					else {
                                          logOut(iwc);
                                          internalSetState(iwc,"loginfailed");
					}
				}
			}
                        else if(controlParameter.equals("tryagain")){

                            internalSetState(iwc,"loggedoff");

                        }

		}
            }

      }
      catch(Exception ex){
        try {
          logOut(iwc);
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        ex.printStackTrace(System.err);
          //throw (IdegaWebException)ex.fillInStackTrace();
      }

  }

  public boolean isAdmin(IWContext iwc)throws Exception{
    return iwc.isAdmin();
  }

  public static void setLoginAttribute(String key, Object value, IWContext iwc) throws NotLoggedOnException{
    if (isLoggedOn(iwc)){
      Object obj = iwc.getSessionAttribute(LoginAttributeParameter);
      ((Hashtable)obj).put(key,value);
    }else{
      throw new NotLoggedOnException();
    }
  }

  public static Object getLoginAttribute(String key, IWContext iwc) throws NotLoggedOnException {
    if(isLoggedOn(iwc)){
      Object obj = iwc.getSessionAttribute(LoginAttributeParameter);
      if(obj == null){
        return null;
      }else{
        return ((Hashtable)obj).get(key);
      }
    }else{
      throw new NotLoggedOnException();
    }
  }

  public static void removeLoginAttribute(String key, IWContext iwc){
    if(isLoggedOn(iwc)){
      Object obj = iwc.getSessionAttribute(LoginAttributeParameter);
      if(obj != null){
        ((Hashtable)obj).remove(key);
      }
    }else if (iwc.getSessionAttribute(LoginAttributeParameter) != null) {
        iwc.removeSessionAttribute(LoginAttributeParameter);
    }
  }


  public static User getUser(IWContext iwc) /* throws NotLoggedOnException */ {
    try {
      return (User)LoginBusiness.getLoginAttribute(UserAttributeParameter,iwc);
    }
    catch (NotLoggedOnException ex) {
      return null;
    }

    /*Object obj = iwc.getSessionAttribute(UserAttributeParameter);
    if (obj != null){
      return (User)obj;
    }else{
      throw new NotLoggedOnException();
    }
    */
  }

  public static List getPermissionGroups(IWContext iwc)throws NotLoggedOnException {
    return (List)LoginBusiness.getLoginAttribute(PermissionGroupParameter,iwc);
  }

  public static UserGroupRepresentative getUserRepresentativeGroup(IWContext iwc)throws NotLoggedOnException {
    return (UserGroupRepresentative)LoginBusiness.getLoginAttribute(UserGroupRepresentativeParameter,iwc);
  }

  public static GenericGroup getPrimaryGroup(IWContext iwc)throws NotLoggedOnException {
    return (GenericGroup)LoginBusiness.getLoginAttribute(PrimaryGroupParameter,iwc);
  }


  protected static void setUser(IWContext iwc, User user){
    LoginBusiness.setLoginAttribute(UserAttributeParameter,user,iwc);
  }

  protected static void setPermissionGroups(IWContext iwc, List value){
    LoginBusiness.setLoginAttribute(PermissionGroupParameter,value,iwc);
  }

  protected static void setUserRepresentativeGroup(IWContext iwc, UserGroupRepresentative value){
    LoginBusiness.setLoginAttribute(UserGroupRepresentativeParameter,value,iwc);
  }

  protected static void setPrimaryGroup(IWContext iwc, GenericGroup value){
    LoginBusiness.setLoginAttribute(PrimaryGroupParameter,value,iwc);
  }

  private boolean logIn(IWContext iwc, int userId) throws Exception{
    User user = new User(userId);
    iwc.setSessionAttribute(LoginAttributeParameter,new Hashtable());

    LoginBusiness.setUser(iwc,user);

    List groups = AccessControl.getPermissionGroups(user);
    if(groups!=null){
      LoginBusiness.setPermissionGroups(iwc,groups);
    }
    int userGroupId = user.getGroupID();
    if(userGroupId != -1){
      LoginBusiness.setUserRepresentativeGroup(iwc,new UserGroupRepresentative(userGroupId));
    }
    if(user.getPrimaryGroupID() != -1){
      GenericGroup primaryGroup = new GenericGroup(user.getPrimaryGroupID());
      LoginBusiness.setPrimaryGroup(iwc,primaryGroup);
    }

    return true;
  }

  private boolean verifyPasswordAndLogin(IWContext iwc,String login, String password) throws Exception{
    boolean returner = false;
    LoginTable[] login_table = (LoginTable[]) (LoginTable.getStaticInstance()).findAllByColumn(LoginTable.getUserLoginColumnName(),login);

    if(login_table != null && login_table.length > 0){
      if ( Encrypter.verifyOneWayEncrypted(login_table[0].getUserPassword(), password)) {
        returner = logIn(iwc,login_table[0].getUserId());
      }
    }

    return returner;
  }

  public static boolean verifyPassword(User user,String login, String password) throws IOException,SQLException{
    boolean returner = false;
    LoginTable[] login_table = (LoginTable[]) (LoginTable.getStaticInstance()).findAllByColumn(LoginTable.getUserIDColumnName(),Integer.toString(user.getID()),LoginTable.getUserLoginColumnName(),login);

    if(login_table != null && login_table.length > 0){
      if ( Encrypter.verifyOneWayEncrypted(login_table[0].getUserPassword(), password)) {
        returner = true;
      }
    }

    return returner;
  }


  private void logOut(IWContext iwc) throws Exception{
    if (iwc.getSessionAttribute(LoginAttributeParameter) != null) {
      iwc.removeSessionAttribute(LoginAttributeParameter);
    }
  }

}
