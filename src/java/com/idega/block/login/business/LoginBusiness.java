package com.idega.block.login.business;

import com.idega.builder.accesscontrol.business.AccessControl;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.data.genericentity.*;
import com.idega.jmodule.login.data.*;
import java.sql.*;
import java.io.*;

/**
 * Title:        Login
 * Description:
 * Copyright:    Copyright (c) 2000 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author idega 2000 - <a href="mailto:idega@idega.is">idega team</a>
 * @version 1.0
 */


public class LoginBusiness{

  public LoginBusiness() {
  }

  public boolean isAdmin(ModuleInfo modinfo)throws SQLException{
    return AccessControl.isAdmin(modinfo);
  }


  public static Member getMember(ModuleInfo modinfo){
    return (Member)modinfo.getSession().getAttribute("member_login");
  }


  public void logOut(ModuleInfo modinfo) throws IOException{
    modinfo.getSession().removeAttribute("member_login");
    if (modinfo.getSession().getAttribute("member_access") != null) {
      modinfo.getSession().removeAttribute("member_access");
    }
  }

  public boolean verifyPassword(ModuleInfo modinfo,String login, String password) throws SQLException{
    boolean returner = false;
    LoginTable[] login_table = (LoginTable[]) (new LoginTable()).findAllByColumn("user_login",login);

    for (int i = 0 ; i < login_table.length ; i++ ) {
      if (login_table[i].getUserPassword().equals(password)) {
        modinfo.getSession().setAttribute("member_login",new Member(login_table[i].getMemberId())   );
        returner = true;
      }
    }

    if (isAdmin(modinfo)) {
      modinfo.getSession().setAttribute("member_access","admin");
    }

    return returner;
  }




  public static void singUp(ModuleInfo modinfo, String kennitala) throws IOException {
    modinfo.getResponse().sendRedirect("/test/nyskraning.jsp?kt="+kennitala);
  }


  public static boolean registerMemberLogin(int member_id, String login, String password, String password2) throws SQLException {
      boolean returner = false;

      if (checkForNewLogin(member_id, login, password, password2) ){
          LoginTable[] log_tables = (LoginTable[]) (new LoginTable()).findAllByColumn("member_id",""+member_id,"user_login",login);
          if (log_tables.length > 0 ) {
              log_tables[0].setUserLogin(login);
              log_tables[0].setUserPassword(password);
              log_tables[0].update();
              returner = true;
          }
          else if (log_tables.length == 0) {
              LoginTable log_table = new LoginTable();
              log_table.setMemberId(new Integer(member_id));
              log_table.setUserLogin(login);
              log_table.setUserPassword(password);
              log_table.insert();
              returner = true;
          }
     }


      return returner;
  }

    public static boolean checkForNewLogin(int member_id, String login,String password, String password2) {
        boolean returner = false;

        if (password.equals(password2))
        try {
	if (!(login.equals(""))) {
		LoginTable[] eruTilFleiriLogin = (LoginTable[]) (new LoginTable()).findAllByColumn("user_login",login);
		if (eruTilFleiriLogin.length > 1) {
			returner = false;
		}
		else if (eruTilFleiriLogin.length == 1 ) {
			LoginTable[] aEgLoginid = (LoginTable[]) (new LoginTable()).findAllByColumn("member_id",member_id);
			if (aEgLoginid[0].getUserLogin().equals(login)) {
				returner = true;
			}
			else {
				returner = false;
			}
		}
		else {
//			returner = true;
		}
	}

        }
        catch (SQLException s) {

        }
        return returner;
    }

    public static Table getLoginInsert() {

        Table table = new Table();
            table.add("Notandi  : ",1,1);
            table.add("Lykilorð : ",1,2);
            table.add("Lykilorð aftur : ",1,3);

            TextInput user_name = new TextInput("user_name");
                user_name.setSize(15);
            PasswordInput password = new PasswordInput("password");
                password.setSize(15);
            PasswordInput password2 = new PasswordInput("password2");
                password2.setSize(15);

            table.add(user_name,2,1);
            table.add(password,2,2);
            table.add(password2,2,3);


        return table;

    }

    public static Table getLoginUpdate(int member_id) {
        Table table = new Table();

        try {
            com.idega.data.genericentity.Member member = new com.idega.data.genericentity.Member(member_id);
            LoginTable[] log_tab = (LoginTable[])(new LoginTable()).findAllByColumn("member_id",member_id);

            if (log_tab.length == 1) {
                    table.add("Notandi  : ",1,1);
                    table.add("Lykilorð : ",1,2);
                    table.add("Lykilorð aftur : ",1,3);

                    TextInput user_name = new TextInput("user_name",log_tab[0].getUserLogin());
                        user_name.setSize(15);
                    PasswordInput password = new PasswordInput("password",log_tab[0].getUserPassword());
                        password.setSize(15);
                    PasswordInput password2 = new PasswordInput("password2");
                        password2.setSize(15);

                    table.add(user_name,2,1);
                    table.add(password,2,2);
                    table.add(password2,2,3);


            }
            else {
                table.add("ekkert til");
            }
        }
        catch (Exception e ) {
            table.add("Villa !!!");
        }

        return table;
    }


}
