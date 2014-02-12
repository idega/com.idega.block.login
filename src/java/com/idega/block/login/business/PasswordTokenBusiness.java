/**
 * @(#)PasswordTokenBusiness.java    1.0.0 11:04:10 AM
 *
 * Idega Software hf. Source Code Licence Agreement x
 *
 * This agreement, made this 10th of February 2006 by and between 
 * Idega Software hf., a business formed and operating under laws 
 * of Iceland, having its principal place of business in Reykjavik, 
 * Iceland, hereinafter after referred to as "Manufacturer" and Agura 
 * IT hereinafter referred to as "Licensee".
 * 1.  License Grant: Upon completion of this agreement, the source 
 *     code that may be made available according to the documentation for 
 *     a particular software product (Software) from Manufacturer 
 *     (Source Code) shall be provided to Licensee, provided that 
 *     (1) funds have been received for payment of the License for Software and 
 *     (2) the appropriate License has been purchased as stated in the 
 *     documentation for Software. As used in this License Agreement, 
 *     Licensee shall also mean the individual using or installing 
 *     the source code together with any individual or entity, including 
 *     but not limited to your employer, on whose behalf you are acting 
 *     in using or installing the Source Code. By completing this agreement, 
 *     Licensee agrees to be bound by the terms and conditions of this Source 
 *     Code License Agreement. This Source Code License Agreement shall 
 *     be an extension of the Software License Agreement for the associated 
 *     product. No additional amendment or modification shall be made 
 *     to this Agreement except in writing signed by Licensee and 
 *     Manufacturer. This Agreement is effective indefinitely and once
 *     completed, cannot be terminated. Manufacturer hereby grants to 
 *     Licensee a non-transferable, worldwide license during the term of 
 *     this Agreement to use the Source Code for the associated product 
 *     purchased. In the event the Software License Agreement to the 
 *     associated product is terminated; (1) Licensee's rights to use 
 *     the Source Code are revoked and (2) Licensee shall destroy all 
 *     copies of the Source Code including any Source Code used in 
 *     Licensee's applications.
 * 2.  License Limitations
 *     2.1 Licensee may not resell, rent, lease or distribute the 
 *         Source Code alone, it shall only be distributed as a 
 *         compiled component of an application.
 *     2.2 Licensee shall protect and keep secure all Source Code 
 *         provided by this this Source Code License Agreement. 
 *         All Source Code provided by this Agreement that is used 
 *         with an application that is distributed or accessible outside
 *         Licensee's organization (including use from the Internet), 
 *         must be protected to the extent that it cannot be easily 
 *         extracted or decompiled.
 *     2.3 The Licensee shall not resell, rent, lease or distribute 
 *         the products created from the Source Code in any way that 
 *         would compete with Idega Software.
 *     2.4 Manufacturer's copyright notices may not be removed from 
 *         the Source Code.
 *     2.5 All modifications on the source code by Licencee must 
 *         be submitted to or provided to Manufacturer.
 * 3.  Copyright: Manufacturer's source code is copyrighted and contains 
 *     proprietary information. Licensee shall not distribute or 
 *     reveal the Source Code to anyone other than the software 
 *     developers of Licensee's organization. Licensee may be held 
 *     legally responsible for any infringement of intellectual property 
 *     rights that is caused or encouraged by Licensee's failure to abide 
 *     by the terms of this Agreement. Licensee may make copies of the 
 *     Source Code provided the copyright and trademark notices are 
 *     reproduced in their entirety on the copy. Manufacturer reserves 
 *     all rights not specifically granted to Licensee.
 *
 * 4.  Warranty & Risks: Although efforts have been made to assure that the 
 *     Source Code is correct, reliable, date compliant, and technically 
 *     accurate, the Source Code is licensed to Licensee as is and without 
 *     warranties as to performance of merchantability, fitness for a 
 *     particular purpose or use, or any other warranties whether 
 *     expressed or implied. Licensee's organization and all users 
 *     of the source code assume all risks when using it. The manufacturers, 
 *     distributors and resellers of the Source Code shall not be liable 
 *     for any consequential, incidental, punitive or special damages 
 *     arising out of the use of or inability to use the source code or 
 *     the provision of or failure to provide support services, even if we 
 *     have been advised of the possibility of such damages. In any case, 
 *     the entire liability under any provision of this agreement shall be 
 *     limited to the greater of the amount actually paid by Licensee for the 
 *     Software or 5.00 USD. No returns will be provided for the associated 
 *     License that was purchased to become eligible to receive the Source 
 *     Code after Licensee receives the source code. 
 */
package com.idega.block.login.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.block.login.IWBundleStarter;
import com.idega.block.login.data.PasswordTokenEntity;
import com.idega.block.login.data.dao.PasswordTokenEntityDAO;
import com.idega.block.login.presentation.PasswordTokenCreator;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.business.DefaultSpringBean;
import com.idega.core.contact.data.Email;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.user.data.UserHome;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
import com.idega.util.SendMail;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>Notifier for {@link User}s about request of password change.</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 Jan 15, 2014
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class PasswordTokenBusiness extends DefaultSpringBean {

	/**
	 * 
	 * @return link of current request
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public String getCleanURI() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context == null) {
			return null;
		}

		ExternalContext externalContext = context.getExternalContext();
		if (externalContext == null) {
			return null;
		}

		return externalContext.getRequestHeaderMap().get("referer");
	}

	/**
	 * 
	 * <p>Searches for valid {@link PasswordTokenEntity}, which has existing
	 * {@link User} and not expired.</p>
	 * @param token is {@link PasswordTokenEntity#getToken()}, 
	 * not <code>null</code>;
	 * @return <code>true</code> if matches written criteria, 
	 * <code>false</code> otherwise;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public boolean isTokenValid(String token) {
		if(getUserByToken(token) != null) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * 
	 * @param token is {@link PasswordTokenEntity#getToken()} to search
	 * by, not <code>null</code>;
	 * @return {@link User} by {@link PasswordTokenEntity#getToken()} or
	 * <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public User getUserByToken(String token) {
		if (StringUtil.isEmpty(token)) {
			return null;
		}

		PasswordTokenEntity tokenEntity = getPasswordTokenEntityDAO().findByToken(token);
		if (tokenEntity == null) {
			return null;
		}

		try {
			return getUserHome().findUserByUniqueId(tokenEntity.getUuid());
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, 
					"Failed to get user by uuid: " + tokenEntity.getUuid());
		}

		return null;
	}
	
	/**
	 * 
	 * <p>Sends mail to unregistered {@link User} about password reset request.</p>
	 * @param email is email address of recipient, not <code>null</code>;
	 * @param ip is IP address of request sender, not <code>null</code>;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public boolean notifyUnregisteredUser(String email, String ip) {
		if (StringUtil.isEmpty(email) || StringUtil.isEmpty(ip) || !email.contains("@")) {
			return Boolean.FALSE;
		}

		StringBuilder sb = new StringBuilder(getLocalizedMessage(
			    "mail.new.text.1", 
			    "Hello,\n\n" +
			    "You (or someone else) entered this email address "));
		sb.append(email).append(CoreConstants.SPACE);
		sb.append(getLocalizedMessage("mail.new.text.2", " when trying " +
			    "to reset the password of an account. However, this email " +
			    "address is not on our database of registered users and " +
			    "therefore the attempted password reset has failed. " + 
			    "If you are a registered user and were expecting this email, " +
			    "please try again using the email address you gave when " +
			    "opening your account. " + 
			    "If you are not a registered user, please ignore this email.\n\n" +
			    "Kind regards,\nClient Support.\n\n"));
		sb.append(getLocalizedMessage("mail.request.from",
				"This action was requested from IP address: "));
		sb.append(ip);
		sb.append(getLocalizedMessage("mail.request.more_info", 
				" find out more about this address here: http://www.whatismyip.com/"));

		try {
			SendMail.send(getSender(), email, null, null, getMailHost(), 
					getSubject(), sb.toString());
			return Boolean.TRUE;
		} catch (javax.mail.MessagingException me) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
			    "Exception while sending message about password reset to email: '" + email + "' cause of: ",
			    me);
			return Boolean.FALSE;
		}
	}

	/**
	 * 
	 * <p>Sends e-mail message to registered {@link User} about process start
	 * of password reset.</p>
	 * @param entity is created link for password reset component, not <code>null</code>;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public boolean notifyRegisteredUser(PasswordTokenEntity entity) {
		if (entity == null) {
			return Boolean.FALSE;
		}

		User user = null;
		try {
			user = getUserHome().findUserByUniqueId(entity.getUuid());
		} catch (FinderException e) {
			getLogger().log(Level.WARNING, 
					"Failed to get " + User.class.getSimpleName() + 
					" by uuid: '" + entity.getUuid() + "'");
		}

		if (user == null) {			
			return Boolean.FALSE;
		}

		Email email = null;
		try {
			email = user.getUsersEmail();
		} catch (Exception e) {
			getLogger().log(Level.WARNING, 
					"Failed to get email for user: '" + user.getName() + "'");
		}

		if (email == null) {
			return Boolean.FALSE;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(getLocalizedMessage("mail.existing.text.1", "Hello ")).append(user.getName());
		sb.append(getLocalizedMessage("mail.existing.text.2", ",\n\n" +
				"You (or someone else) entered this email address "));
		sb.append(email.getEmailAddress());
		sb.append(getLocalizedMessage("mail.existing.text.3", " when trying " +
				"to reset the password of an account. If you did not asked " +
				"password reset please ignore this email. To continue the " +
				"password reset, please proceed to "));
		sb.append(getLink(entity));
		sb.append(getLocalizedMessage("mail.existing.text.4", "\n\n" +
				"Kind regards,\nClient Support."));
		sb.append(getLocalizedMessage("mail.request.from",
				"This action was requested from IP address: "));
		sb.append(entity.getIp());
		sb.append(getLocalizedMessage("mail.request.more_info", 
				" find out more about this address here: http://www.whatismyip.com/"));

		try {
			SendMail.send(getSender(), email.getEmailAddress(), null, null, 
					getMailHost(), getSubject(), sb.toString());
			return Boolean.TRUE;
		} catch (javax.mail.MessagingException me) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
			    "Exception while sending message about password reset " +
			    "to email: '" + email + "' cause of: ", me);
		}

		return Boolean.FALSE;
	}

	/**
	 * 
	 * <p>Creates a record about password reset request and notifies {@link User}s .</p>
	 * @param identifier is mail or personal id or nickname or {@link User}
	 * to send info for, not <code>null</code>;
	 * @param ip is IP address from where password reset request was sent.
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public boolean initiatePasswordReset(String identifier, String ip) {
		if (!StringUtil.isEmpty(identifier) && !StringUtil.isEmpty(ip)) {
			Collection<User> users = getUsers(identifier);
			if (ListUtil.isEmpty(users)) {
				notifyUnregisteredUser(identifier, ip);
				return Boolean.TRUE;
			} else {
				for (User user : users) {
					if (!notifyRegisteredUser(getPasswordTokenEntityDAO()
							.create(user.getUniqueId(), ip))) {
						return Boolean.FALSE;
					}
				}

				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	/**
	 * 
	 * <p>Changes password for {@link User}, 
	 * removes old {@link PasswordTokenEntity}s.</p>
	 * @param token is {@link PasswordTokenEntity#getToken()}, 
	 * not <code>null</code>;
	 * @param newPassword not <code>null</code>;
	 * @return <code>true</code> on success, <code>false</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public boolean completePasswordReset(String token, String newPassword) {
		if (StringUtil.isEmpty(newPassword)) {
			return Boolean.FALSE;
		}

		User user = getUserByToken(token);
		if (user == null) {
			return Boolean.FALSE;
		}

		if (!getPasswordTokenEntityDAO().removeByUUID(user.getUniqueId())) {
			return Boolean.FALSE;
		}

		return getUserBusiness().changeUserPassword(user, newPassword);
	}

	/**
	 * 
	 * <p>Collects all {@link Email}s of {@link User}s found by:</p>
	 * @param uuid is {@link User#getUniqueId()}, not <code>null</code>;
	 * @return {@link List} of {@link Email#getEmailAddress()} or 
	 * {@link Collections#emptyList()} on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public List<String> getEmailAddresses(String uuid) {
		if (StringUtil.isEmpty(uuid)) {
			return Collections.emptyList();
		}

		Collection<User> users = getUsers(uuid);
		if (ListUtil.isEmpty(users)) {
			return Collections.emptyList();
		}

		ArrayList<String> emails = new ArrayList<String>();
		for (User user : users) {
			Email email = null;
			try {
				email = user.getUsersEmail();
			} catch (Exception e) {
				getLogger().warning("Failed to get email for user: '" + user.getName() + "'");
			}

			if (email == null) {
				continue;
			}

			emails.add(email.getEmailAddress());
		}

		return emails;
	}

	/**
	 * 
	 * <p>Constructs a link to component for changing password.</p>
	 * @param entity designed to create a link, not <code>null</code>;
	 * @return designed link or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected String getLink(PasswordTokenEntity entity) {
		if (entity == null) {
			return null;
		}
		
		StringBuilder uri = new StringBuilder(getCleanURI());
		uri.append(CoreConstants.QMARK)
		.append(PasswordTokenCreator.PARAMETER_TOKEN)
		.append(CoreConstants.EQ)
		.append(entity.getToken());
		return uri.toString();
	}

	protected String getLocalizedMessage(String key, String value) {
		if (StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) {
			return null;
		}

		IWBundle bundle = getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER);
		if (bundle == null) {
			return null;
		}

		IWResourceBundle resourceBundle = getResourceBundle(bundle);
		if (resourceBundle == null) {
			return null;
		}

		return resourceBundle.getLocalizedString(key, value);
	}
	
	private String host = null;
	
	protected String getMailHost() {
		if (StringUtil.isEmpty(this.host)) {
			this.host = getApplication().getSettings().getProperty(
				    CoreConstants.PROP_SYSTEM_SMTP_MAILSERVER, 
				    CoreConstants.EMAIL_DEFAULT_HOST);
		}

		return this.host;
	}

	private String sender = null;

	protected String getSender() {
		if (StringUtil.isEmpty(this.sender)) {
			this.sender = getApplication().getSettings().getProperty(
				    CoreConstants.PROP_SYSTEM_MAIL_FROM_ADDRESS, 
				    CoreConstants.EMAIL_DEFAULT_FROM);
		}

		return this.sender;
	}

	protected String getSubject() {
		return getLocalizedMessage(
			    "mail.subject", 
			    "Password reset");
	}


	/**
	 * 
	 * @param identificator is {@link Email} or {@link User#getPersonalID()} 
	 * or username, not <code>null</code>;
	 * @return {@link User}s by given criteria or {@link Collections#emptyList()}
	 * on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected Collection<User> getUsers(String identificator) {
		if (!StringUtil.isEmpty(identificator)) {

			/*
			 * Try by email
			 */
			Collection<User> users = null;
			try {
				users = getUserHome().findUsersByEmail(identificator);
			} catch (FinderException e) {}

			if (!ListUtil.isEmpty(users)) {
				return users;
			}

			/*
			 * Try by personal id
			 */
			User user = null;
			try {
				user = getUserHome().findByPersonalID(identificator);
			} catch (FinderException e) {}

			if (user != null) {
				return Arrays.asList(user);
			}

			/*
			 * Try by login info
			 */
			LoginTable loginTable = LoginDBHandler.getUserLoginByUserName(identificator);
			if (loginTable != null) {
				user = loginTable.getUser();
				if (user != null) {
					return Arrays.asList(user);
				}
			}
		}

		return Collections.emptyList();
	}

	@Autowired
	private PasswordTokenEntityDAO passwordTokenEntityDAO;

	protected PasswordTokenEntityDAO getPasswordTokenEntityDAO() {
		if (this.passwordTokenEntityDAO == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.passwordTokenEntityDAO;
	}

	private UserHome userHome = null;

	protected UserHome getUserHome() {
		if (this.userHome == null) {
			try {
				this.userHome = (UserHome) IDOLookup.getHome(User.class);
			} catch (IDOLookupException e) {
				java.util.logging.Logger.getLogger(getClass().getName()).log(
						Level.WARNING, "Failed to get " + 
								UserHome.class.getSimpleName() + " cause of: ", e);
			}
		}

		return this.userHome;
	}

	private UserBusiness userBusiness = null;

	protected UserBusiness getUserBusiness() {
		if (this.userBusiness == null) {
			try {
				this.userBusiness = IBOLookup.getServiceInstance(
						IWContext.getCurrentInstance(), 
						UserBusiness.class);
			} catch (IBOLookupException e) {
				java.util.logging.Logger.getLogger(getClass().getName()).log(
						Level.WARNING, "", e);
			}
		}

		return this.userBusiness;
	}
}
