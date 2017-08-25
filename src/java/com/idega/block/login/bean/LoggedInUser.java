package com.idega.block.login.bean;

import java.io.Serializable;

public class LoggedInUser implements Serializable {

	private static final long serialVersionUID = -600623971979400484L;

	private String name;
	private String personalID;
	private String login;

	private OAuthToken token;

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

	public OAuthToken getToken() {
		return token;
	}

	public void setToken(OAuthToken token) {
		this.token = token;
	}

}