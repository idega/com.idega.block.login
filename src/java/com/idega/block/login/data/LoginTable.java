//idega 2000 - Gimmi
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.block.login.data;

//import java.util.*;
import java.sql.*;
import com.idega.data.*;

public class LoginTable extends GenericEntity{

	public LoginTable(){
		super();
	}

	public LoginTable(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("member_id","Meðlimur",true,true,"java.lang.Integer","one-to-one","com.idega.projects.lv.entity.Member");
		addAttribute("user_login","Notandanafn",true,true,"java.lang.String");
		addAttribute("user_password","Lykilorð",true,true,"java.lang.String");
	}

	public String getIDColumnName(){
		return "login_table_id";
	}

	public String getEntityName(){
		return "i_login_table";
	}

	public String getUserPassword(){
		return (String) getColumnValue("user_password");
	}

	public void setUserPassword(String userPassword){
		setColumn("user_password", userPassword);
	}
	public void setUserLogin(String userLogin) {
		setColumn("user_login", userLogin);
	}
	public String getUserLogin() {
		return (String) getColumnValue("user_login");
	}

	public int getMemberId(){
		return getIntColumnValue("member_id");
	}

	public void setMemberId(Integer memberId){
		setColumn("member_id", memberId);
	}
	public void setMemberId(int memberId) {
		setMemberId((new Integer(memberId)));
	}

}
