//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.block.login.business;

import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.*;
import java.util.*;
import java.io.*;
import com.idega.jmodule.login.data.*;
import java.sql.*;
import java.io.*;
import com.idega.data.genericentity.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/

public class AccessControl{



	public static Member getMember(ModuleInfo modinfo){
		return (Member)modinfo.getSession().getAttribute("member_login");
	}


	public static boolean isAdmin(ModuleInfo modinfo)throws SQLException{
		Member member = getMember(modinfo);
		if (member != null){
                    if(member instanceof com.idega.projects.golf.entity.Member){

                        com.idega.projects.golf.entity.Member membi = (com.idega.projects.golf.entity.Member)member;
                    	Group[] access = membi.getGroups(); //  (member).getGenericGroups();
			for(int i = 0; i < access.length; i++){
                          if ("administrator".equals(access[i].getName())){
                                  return true;
                          }

                          if ("club_admin".equals(access[i].getName())){
                            Object ID = modinfo.getSessionAttribute("golf_union_id");
                            if( ID != null){
                              int uni_id = membi.getMainUnionID();
                              if (uni_id == Integer.parseInt( ((String)ID) ) ){
                                return true;
                              }
                            }

                          }

                        }
                        return false;

                    }
                    else{
			LoginType[] access = member.getLoginType();
			for(int i = 0; i < access.length; i++){
			if ("administrator".equals(access[i].getName()))
				return true;
			}
                    }
		}
		return false;
	}




        public static boolean hasPermission(String permissionType, ModuleObject obj,ModuleInfo info){
          /*@todo*/
          return true;
        }



        /*public boolean isAdmin(ModuleInfo modinfo) {

          try{
            return com.idega.jmodule.login.business.AccessControl.isAdmin(getModuleInfo());
          }catch (SQLException E) {

            //out.print("SQLException: " + E.getMessage());
            //out.print("SQLState:     " + E.getSQLState());
            //out.print("VendorError:  " + E.getErrorCode());
          }catch (Exception E) {
		E.printStackTrace();
	  }finally {
	  }
          return false;
        }*/



        public static boolean isDeveloper(ModuleInfo modinfo) {
			if (modinfo.getSession().getAttribute("member_access") != null) {
				if (modinfo.getSession().getAttribute("member_access").equals("developer")) {
					return true;
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
        }

        public static boolean isClubAdmin(ModuleInfo modinfo) {
			if (modinfo.getSession().getAttribute("member_access") != null) {
				if (modinfo.getSession().getAttribute("member_access").equals("club_admin")) {
					return true;
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
        }

        public static boolean isUser(ModuleInfo modinfo) {
			if (modinfo.getSession().getAttribute("member_access") != null) {
				if (modinfo.getSession().getAttribute("member_access").equals("user")) {
					return true;
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
        }





}