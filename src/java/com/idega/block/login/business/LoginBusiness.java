//idega 2000 - Tryggvi Larusson - Grimur Jonsson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.block.login.business;


import com.idega.jmodule.object.*;
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
  private static String UserGroupRepresentativeParameter = "ic_user_representive_group";
  private static String PrimaryGroupsParameter = "ic_user_primarygroups";
  private static String PrimaryGroupParameter = "ic_user_primarygroup";

  public LoginBusiness() {
  }


  public static boolean isLoggedOn(ModuleInfo modinfo){
      if(modinfo.getSessionAttribute(LoginAttributeParameter)==null){
        return false;
      }
      return true;
  }

  public static void internalSetState(ModuleInfo modinfo,String state){
      modinfo.setSessionAttribute(LoginStateParameter,state);
  }

  public static String internalGetState(ModuleInfo modinfo){
      return (String) modinfo.getSessionAttribute(LoginStateParameter);
  }

  public void actionPerformed(ModuleInfo modinfo)throws IWException{
        //System.out.println("LoginBusiness.actionPerformed");

        try{

            if(isLoggedOn(modinfo)){
                  String controlParameter = modinfo.getParameter(LoginBusiness.LoginStateParameter);
                  if (controlParameter != null) {

                        if(controlParameter.equals("logoff")){
                            logOut(modinfo);
                            internalSetState(modinfo,"loggedoff");

                        }

                  }


            }
            else{
                  String controlParameter = modinfo.getParameter(LoginBusiness.LoginStateParameter);

                  if (controlParameter != null) {
                      if(controlParameter.equals("login")){

                                boolean canLogin = false;
				if ((modinfo.getParameter("login") != null) && (modinfo.getParameter("password") != null)) {
					canLogin = verifyPassword(modinfo, modinfo.getParameter("login"),modinfo.getParameter("password"));
					if (canLogin) {
						isLoggedOn(modinfo);
                                                internalSetState(modinfo,"loggedon");
					}
					else {
                                                internalSetState(modinfo,"loginfailed");
					}
				}
			}
                        else if(controlParameter.equals("tryagain")){

                            internalSetState(modinfo,"loggedoff");

                        }

		}
            }

      }
      catch(Exception ex){
          ex.printStackTrace(System.err);
          //throw (IdegaWebException)ex.fillInStackTrace();
      }

  }

  public boolean isAdmin(ModuleInfo modinfo)throws SQLException{
    return AccessControl.isAdmin(modinfo);
  }

  public static void setLoginAttribute(String key, Object value, ModuleInfo modinfo) throws NotLoggedOnException{
    if (isLoggedOn(modinfo)){
      Object obj = modinfo.getSessionAttribute(LoginAttributeParameter);
      ((Hashtable)obj).put(key,value);
    }else{
      throw new NotLoggedOnException();
    }
  }

  public static Object getLoginAttribute(String key, ModuleInfo modinfo) throws NotLoggedOnException {
    if(isLoggedOn(modinfo)){
      Object obj = modinfo.getSessionAttribute(LoginAttributeParameter);
      if(obj == null){
        return null;
      }else{
        return ((Hashtable)obj).get(key);
      }
    }else{
      throw new NotLoggedOnException();
    }
  }

  public static void removeLoginAttribute(String key, ModuleInfo modinfo){
    if(isLoggedOn(modinfo)){
      Object obj = modinfo.getSessionAttribute(LoginAttributeParameter);
      if(obj != null){
        ((Hashtable)obj).remove(key);
      }
    }else if (modinfo.getSessionAttribute(LoginAttributeParameter) != null) {
        modinfo.removeSessionAttribute(LoginAttributeParameter);
    }
  }


  public static User getUser(ModuleInfo modinfo) /* throws NotLoggedOnException */ {
    try {
      return (User)LoginBusiness.getLoginAttribute(UserAttributeParameter,modinfo);
    }
    catch (NotLoggedOnException ex) {
      return null;
    }

    /*Object obj = modinfo.getSessionAttribute(UserAttributeParameter);
    if (obj != null){
      return (User)obj;
    }else{
      throw new NotLoggedOnException();
    }
    */
  }

  public static List getPermissionGroups(ModuleInfo modinfo)throws NotLoggedOnException {
    return (List)LoginBusiness.getLoginAttribute(PermissionGroupParameter,modinfo);
  }

  public static UserGroupRepresentative getUserRepresentiveGroup(ModuleInfo modinfo)throws NotLoggedOnException {
    return (UserGroupRepresentative)LoginBusiness.getLoginAttribute(UserGroupRepresentativeParameter,modinfo);
  }

  public static GenericGroup getPrimaryGroup(ModuleInfo modinfo)throws NotLoggedOnException {
    return (GenericGroup)LoginBusiness.getLoginAttribute(PrimaryGroupParameter,modinfo);
  }


  protected static void setUser(ModuleInfo modinfo, User user){
    LoginBusiness.setLoginAttribute(UserAttributeParameter,user,modinfo);
  }

  protected static void setPermissionGroups(ModuleInfo modinfo, List value){
    LoginBusiness.setLoginAttribute(PermissionGroupParameter,value,modinfo);
  }

  protected static void setUserRepresentiveGroup(ModuleInfo modinfo, UserGroupRepresentative value){
    LoginBusiness.setLoginAttribute(UserGroupRepresentativeParameter,value,modinfo);
  }

  protected static void setPrimaryGroup(ModuleInfo modinfo, GenericGroup value){
    LoginBusiness.setLoginAttribute(PrimaryGroupParameter,value,modinfo);
  }

  private boolean logIn(ModuleInfo modinfo, int userId) throws SQLException{
    User user = new User(userId);
    modinfo.setSessionAttribute(LoginAttributeParameter,new Hashtable());

    LoginBusiness.setUser(modinfo,user);

    List groups = AccessControl.getPermissionGroups(user);
    if(groups!=null){
      LoginBusiness.setPermissionGroups(modinfo,groups);
    }

    LoginBusiness.setUserRepresentiveGroup(modinfo,new UserGroupRepresentative(user.getGroupID()));

    if(user.getPrimaryGroupID() != -1){
      GenericGroup primaryGroup = new GenericGroup(user.getPrimaryGroupID());
      LoginBusiness.setPrimaryGroup(modinfo,primaryGroup);
    }

    return true;
  }

  private boolean verifyPassword(ModuleInfo modinfo,String login, String password) throws IOException,SQLException{
    boolean returner = false;
    LoginTable[] login_table = (LoginTable[]) (LoginTable.getStaticInstance()).findAllByColumn(LoginTable.getUserLoginColumnName(),login);

    if(login_table != null && login_table.length > 0){
      if ( Encrypter.verifyOneWayEncrypted(login_table[0].getUserPassword(), password)) {
        returner = logIn(modinfo,login_table[0].getUserId());
      }
    }

    return returner;
  }



  private void logOut(ModuleInfo modinfo) throws Exception{
    if (modinfo.getSessionAttribute(LoginAttributeParameter) != null) {
      modinfo.removeSessionAttribute(LoginAttributeParameter);
    }
  }

}
