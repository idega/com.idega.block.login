package com.idega.block.login.business;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.presentation.IWContext;
import com.idega.user.data.User;
import com.idega.util.CoreUtil;

@Service(LoginRequestBean.BEAN_NAME)
@Scope("request")
public class LoginRequestBean {
	public static final String BEAN_NAME = "loginRequestBean";
	private User authorizedUser;
	private IWContext iwc;
	private boolean authorizedByFilter;
	
	public boolean isAuthorizedByFilter() {
		return authorizedByFilter;
	}
	public void setAuthorizedByFilter(boolean authorizedByFilter) {
		this.authorizedByFilter = authorizedByFilter;
	}
	
	public User getAuthorizedUser() {
		if(authorizedUser != null){
			return authorizedUser;
		}
		if(authorizedByFilter){
			return authorizedUser;
		}
		try{
			authorizedUser = getIwc().getCurrentUser();
		}catch (Exception e) {
			// TODO: handle exception
		}
		return authorizedUser;
	}
	
	public void setAuthorizedUser(User authorizedUser) {
		this.authorizedUser = authorizedUser;
	}
	public IWContext getIwc() {
		if(iwc != null){
			return iwc;
		}
		iwc = CoreUtil.getIWContext();
		return iwc;
	}
	public void setIwc(IWContext iwc) {
		this.iwc = iwc;
	}
	
	
}
