package com.idega.block.login.business.impl;

import java.text.MessageFormat;
import java.util.logging.Level;

import javax.mail.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.block.login.LoginConstants;
import com.idega.block.login.business.PasswordTokenBusiness;
import com.idega.block.login.data.PasswordTokenEntity;
import com.idega.block.login.data.dao.PasswordTokenEntityDAO;
import com.idega.core.accesscontrol.business.TwoStepEmailLoginVerificator;
import com.idega.core.business.DefaultSpringBean;
import com.idega.core.file.util.MimeTypeUtil;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.user.data.bean.User;
import com.idega.util.CoreConstants;
import com.idega.util.SendMail;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class TwoStepEmailLoginVerificatorImpl extends DefaultSpringBean implements TwoStepEmailLoginVerificator {

	@Autowired
	private PasswordTokenEntityDAO passwordTokenEntityDAO;

	@Autowired
	private PasswordTokenBusiness passwordTokenBusiness;

	@Override
	public String generateSecondStepAuthKey(
			User user,
			IWContext iwc,
			int secondsForSecondStepAuthValidity,
			String emailAddress
	) {
		try {
			if (user == null || StringUtil.isEmpty(user.getUniqueId())) {
				getLogger().warning("User not provided or user (" + user + ") does not have unique ID");
				return null;
			}

			if (StringUtil.isEmpty(emailAddress)) {
				getLogger().warning(user + " does not have email address");
				return null;
			}

			//Generate the 2-STEP key - TOKEN
			PasswordTokenEntity passwordToken = passwordTokenEntityDAO.create(
					user.getUniqueId(),
					iwc.getRemoteIpAddress(),
					Long.valueOf(secondsForSecondStepAuthValidity * 1000), // 2 minutes by default
					6
			);

			if (
					passwordToken != null
					&& !StringUtil.isEmpty(passwordToken.getToken())
			) {
				IWResourceBundle iwrb = getResourceBundle(getBundle(LoginConstants.IW_BUNDLE_IDENTIFIER));

				String key = passwordToken.getToken();

				String from = IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty(CoreConstants.PROP_SYSTEM_MAIL_FROM_ADDRESS, "no-reply@idega.com");

				String emailSubject = iwrb.getLocalizedString("2_step_auth_email.subject", "Login verification {0}");
				String serverName = IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty(IWMainApplication.PROPERTY_DEFAULT_SERVICE_URL);
				if (StringUtil.isEmpty(serverName)) {
					emailSubject = MessageFormat.format(emailSubject, CoreConstants.EMPTY);
				} else {
					emailSubject = MessageFormat.format(emailSubject, CoreConstants.BRACKET_LEFT + serverName + CoreConstants.BRACKET_RIGHT);
				}

				String emailBody = iwrb.getLocalizedString("2_step_auth_email.body", "<strong>Login Verification {0}</strong><br /><br />Your verification code:<br /><br /><span><strong>{1}</strong></span><br /><br />The verification code will be valid for {2} minutes. Please do not share this code with anyone.");
				emailBody = MessageFormat.format(
						emailBody,
						StringUtil.isEmpty(serverName) ? CoreConstants.EMPTY : (CoreConstants.BRACKET_LEFT + serverName + CoreConstants.BRACKET_RIGHT),
						key,
						String.valueOf(secondsForSecondStepAuthValidity / 60)
				);

				//Send the mail to the user with the 2-STEP auth key/token
				Message messageAfterEmailSending = SendMail.send(
						from,
						emailAddress,
						null,
						null,
						from,
						null,
						emailSubject,
						emailBody,
						MimeTypeUtil.MIME_TYPE_HTML
				);

				if (messageAfterEmailSending != null) {
					return key;
				} else {
					getLogger().warning("Failed to send email to " + emailAddress + " with 2-STEP auth key (" + key + ") for user " + user);
					return null;
				}
			} else {
				getLogger().warning("Failed to generate 2-STEP auth key (" + passwordToken + ") for user: " + user);
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error while trying to generate 2-STEP auth key for user: " + user, e);
		}
		return null;
	}

	protected PasswordTokenBusiness getPasswordTokenBusiness() {
		if (this.passwordTokenBusiness == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.passwordTokenBusiness;
	}

	@Override
	public boolean isTokenValid(String token) {
		boolean validToken = getPasswordTokenBusiness().isTokenValid(token);
		if (validToken) {
			//Remove the token
			PasswordTokenEntity tokenEntity = passwordTokenEntityDAO.findByToken(token);
			if (tokenEntity != null && !StringUtil.isEmpty(tokenEntity.getUuid())) {
				passwordTokenEntityDAO.removeByUUID(tokenEntity.getUuid());
			}
		}

		return validToken;
	}


}
