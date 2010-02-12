package com.idega.block.login.bean;

public class LoggedInUser {

	private String name;
	private String personalID;
	private String login;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPersonalID() {
		return personalID;
	}
	
	public void setPersonalID(String personalID) {
		this.personalID = personalID;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
}