//idega 2000 - Tryggvi Larusson - Grimur Jonsson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.block.login.business;


import com.idega.jmodule.object.*;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.LoginInfo;
import com.idega.core.accesscontrol.data.PermissionGroup;
import com.idega.core.user.data.User;
import java.sql.*;
import java.io.*;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.business.IWEventListener;
import com.idega.idegaweb.IWException;
import com.idega.util.Encrypter;

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
  public static String UserAccessAttributeParameter="user_access";
  public static String LoginStateParameter="login_state";

  public LoginBusiness() {
  }


  public static boolean isLoggedOn(ModuleInfo modinfo){
      if(modinfo.getSessionAttribute(UserAttributeParameter)==null){
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


  public static User getUser(ModuleInfo modinfo){
    return (User)modinfo.getSessionAttribute(UserAttributeParameter);
  }

  public static PermissionGroup[] getPermissionGroups(ModuleInfo modinfo){
    return (PermissionGroup[])modinfo.getSessionAttribute(PermissionGroupParameter);
  }

  private boolean verifyPassword(ModuleInfo modinfo,String login, String password) throws IOException,SQLException{
          boolean returner = false;
          LoginTable[] login_table = (LoginTable[]) (new LoginTable()).findAllByColumn(LoginTable.getUserLoginColumnName(),login);

          for (int i = 0 ; i < login_table.length ; i++ ) {
            if ( Encrypter.verifyOneWayEncrypted(login_table[i].getUserPassword(), password)) {
              User user = new User(login_table[i].getUserId());
              modinfo.getSession().setAttribute(UserAttributeParameter, user);
              PermissionGroup[] groups = AccessControl.getPermissionGroups(user);
              if(groups!=null){
                modinfo.setSessionAttribute(PermissionGroupParameter,groups);
              }
              returner = true;
            }else{
              System.err.println(login_table[i].getUserPassword()+" != "+ Encrypter.encryptOneWay(password));
            }
          }
          if (isAdmin(modinfo)) {
                  modinfo.setSessionAttribute(UserAccessAttributeParameter,"admin");
          }
/*		if (isDeveloper(modinfo)) {
                  modinfo.getSession().setAttribute("user_access","developer");
          }
          if (isClubAdmin(modinfo)) {
                  modinfo.getSession().setAttribute("user_access","club_admin");
          }
          if (isUser(modinfo)) {
                  modinfo.getSession().setAttribute("user_access","user");
          }
*/
          return returner;
  }



    private void logOut(ModuleInfo modinfo) throws Exception{
            modinfo.removeSessionAttribute(UserAttributeParameter);
            modinfo.removeSessionAttribute(PermissionGroupParameter);

            if (modinfo.getSessionAttribute(UserAccessAttributeParameter) != null) {
                    modinfo.removeSessionAttribute(UserAccessAttributeParameter);
            }
    }

    public static boolean registerUserLogin(int user_id,String user_login,String user_pass_one,String user_pass_two) throws SQLException {
        boolean returner = false;

        if (user_pass_one.equals(user_pass_two)) {
            LoginTable[] logTable = (LoginTable[]) (new LoginTable()).findAllByColumn(LoginTable.getUserLoginColumnName(),user_login);
            if (logTable.length == 0) {
                LoginTable logT = new LoginTable();
                  logT.setUserId(user_id);
                  logT.setUserLogin(user_login);
                  logT.setUserPassword(user_pass_one);
                logT.insert();
                returner = true;
            }
            else if (logTable.length == 1) {
                if (logTable[0].getUserId()  == user_id ) {
                    logTable[0].setUserId(user_id);
                    logTable[0].setUserLogin(user_login);
                    logTable[0].setUserPassword(user_pass_one);
                  logTable[0].update();
                  returner = true;
                }
            }
            else {
                returner = false;
            }
        }

        if (returner) {

        }

        return returner;
    }


}
