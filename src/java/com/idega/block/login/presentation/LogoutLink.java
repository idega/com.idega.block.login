package com.idega.block.login.presentation;

import javax.faces.context.FacesContext;

public class LogoutLink extends Login2 {

	@Override
	public void initializeComponent(FacesContext context) {
		setUnAuthenticatedFaceletPath(getBundle(context, getBundleIdentifier()).getFaceletURI("loggedOutEmpty.xhtml"));
		setAuthenticatedFaceletPath(getBundle(context, getBundleIdentifier()).getFaceletURI("loggedInButton.xhtml"));
		setAuthenticationFailedFaceletPath(getBundle(context, getBundleIdentifier()).getFaceletURI("loginFailedEmpty.xhtml"));
		
		super.initializeComponent(context);
	}	
}