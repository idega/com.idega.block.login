package com.idega.block.login.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import org.apache.commons.httpclient.HttpStatus;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.block.login.LoginConstants;
import com.idega.business.IBOLookup;
import com.idega.core.business.DefaultSpringBean;
import com.idega.dwr.bean.Response;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.CoreUtil;

@Service(LoginServices.BEAN_NAME)
@Scope(BeanDefinition.SCOPE_SINGLETON)
@RemoteProxy(creator=SpringCreator.class, creatorParams={
	@Param(name="beanName", value=LoginServices.BEAN_NAME),
	@Param(name="javascript", value="LoginServices")
}, name="LoginServices")
public class LoginServices extends DefaultSpringBean{
	public static final String BEAN_NAME = "loginServices";

	private Collection<PasswordValidator> passwordValidators;

	public Collection<PasswordValidator> getPasswordValidators() {
		if(passwordValidators != null){
			return passwordValidators;
		}
		passwordValidators = new ArrayList<PasswordValidator>();
		return passwordValidators;
	}

	public void addPasswordValidator(PasswordValidator passwordValidator){
		getPasswordValidators().add(passwordValidator);
	}

	public IWResourceBundle getIwrb(IWContext iwc) {
		IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(LoginConstants.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		return iwrb;
	}
	private String isValidPassword(String password,User user,IWContext iwc){
		Collection<PasswordValidator> validators = getPasswordValidators();
		for(PasswordValidator validator : validators){
			String validationError = validator.getPasswordError(password, iwc, user);
			if(validationError != null){
				return validationError;
			}
		}
		return null;
	}
	@RemoteMethod
	public Response savePassword(String password) {
		IWContext iwc = CoreUtil.getIWContext();
		IWResourceBundle iwrb = getIwrb(iwc);
		Response response = new Response();
		if (iwc.isLoggedOn()) {
			try {
				User user = iwc.getCurrentUser();
				String validationError = isValidPassword(password, user, iwc);
				if (validationError != null) {
					response.setMessage(validationError);
					response.setStatus(HttpStatus.getStatusText(HttpStatus.SC_BAD_REQUEST));
					return response;
				}

				UserBusiness userBusiness = IBOLookup.getServiceInstance(iwc, UserBusiness.class);
				if (userBusiness.changeUserPassword(user, password)) {
					response.setStatus(HttpStatus.getStatusText(HttpStatus.SC_OK));
				} else {
					response.setStatus(HttpStatus.getStatusText(HttpStatus.SC_INTERNAL_SERVER_ERROR));
					response.setMessage(iwrb.getLocalizedString("error", "Error"));
				}
				return response;
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Failed changing password to " + password, e);
			}
		}

		response.setStatus(HttpStatus.getStatusText(HttpStatus.SC_INTERNAL_SERVER_ERROR));
		response.setMessage(iwrb.getLocalizedString("error", "Error"));
		return response;
	}

}
