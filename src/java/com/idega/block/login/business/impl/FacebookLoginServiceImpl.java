/**
 * @(#)ExternalLoginServiceImpl.java    1.0.0 3:13:48 PM
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
package com.idega.block.login.business.impl;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.logging.Level;

import javax.ejb.CreateException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.block.login.business.FacebookLoginService;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.business.DefaultSpringBean;
import com.idega.user.business.UserBusiness;
import com.idega.util.CoreUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.types.User;

/**
 * @see FacebookLoginService
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 Jun 21, 2013
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
@Service(FacebookLoginService.BEAN_NAME)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class FacebookLoginServiceImpl extends DefaultSpringBean implements FacebookLoginService{

	private com.idega.user.business.UserBusiness userBusiness = null;
	
	/**
	 * 
	 * <p>Creates Idega {@link com.idega.user.data.User} from 
	 * facebook.com {@link User}</p>
	 * @param facebookUser to create in Idega system, not <code>null</code>;
	 * @return Idega {@link com.idega.user.data.User} or <code>null</code>
	 * on failure;
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	protected com.idega.user.data.User createUser(User facebookUser) {
		if (facebookUser == null) {
			return null;
		}
		
		com.idega.user.data.User idegaUser = getIdegaUser(facebookUser);
		if (idegaUser != null) {
			getLogger().info("User by email: " + facebookUser.getEmail() + " already exists!");
			return idegaUser;
		}
		
		try {
			return getUserBusiness().createUser(
					facebookUser.getFirstName(), 
					facebookUser.getMiddleName(), 
					facebookUser.getLastName(), 
					facebookUser.getUsername(), 
					null, 
					facebookUser.getBio(), 
					"male".equals(facebookUser.getGender()) ? 1 : 2, 
					new IWTimestamp(facebookUser.getBirthdayAsDate()), 
					null, facebookUser.getName());
		} catch (RemoteException e) {
			getLogger().log(Level.WARNING, "Failed to connect data source, " +
					"cause of: ", e);
		} catch (CreateException e) {
			getLogger().log(Level.WARNING, "Failed to create " + User.class + 
					" cause of: ", e);
		}
		
		return null;
	}
	
	@Override
	public com.idega.user.data.User loginByFacebookAccount(String email, String password) {
		User facebookUser = loginToFacebook(email, password);
		if (facebookUser == null) {
			getLogger().warning("Failed to log in to facebook.com");
			return null;
		}
		
		return createUser(facebookUser);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.idega.mobile.business.ExternalLoginService#loginToFacebook(java.lang.String, java.lang.String)
	 */
	@Override
	public User loginToFacebook(String email, String password) {
//		if (email == null || StringUtil.isEmpty(password)) {
//			return null;
//		}

		DefaultFacebookClient facebookClient = new DefaultFacebookClient("CAACEdEose0cBALyQGL8QYqwZBuNgLcXDXPTnxs44VriEao3qxiimYRnZCPb7yBXbzXhblOlQFZCzMTPDnZBkMNLkrAMzNpy3a97MwadyZBIjHbgzZCQSGpp1u7ysQXtZBqiTPV4ZCL6LxomcRmwl09YeFWg4CRjosUTZCmonjnxPT1QZDZD");
				
//		AccessToken accessToken = null;
//		try {
//			accessToken = facebookClient.obtainAppAccessToken(email, password);
//		} catch (Exception e) {
//			getLogger().log(
//					Level.WARNING, 
//					"Failed to log in to facebook.com by email: " + email + 
//					", cause of: ", e);			
//		}
		
//		if (accessToken == null) {
//			return null;
//		}
		
		User user = facebookClient.fetchObject("me", User.class,
				  Parameter.with("fields", "id, name"));
		if (user == null) {
			getLogger().log(
					Level.WARNING,
					"Failed to retrieve data about facebook.com user by email: " + 
					email);
			return null;
		}
		
		return user;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.mobile.business.ExternalLoginService#getIdegaUser(java.lang.String, java.lang.String)
	 */
	@Override
	public com.idega.user.data.User getIdegaUser(String email, String password) {
		return getIdegaUser(loginToFacebook(email, password));
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.idega.mobile.business.ExternalLoginService#getIdegaUser(com.restfb.types.User)
	 */
	@Override
	public com.idega.user.data.User getIdegaUser(User facebookUser) {
		if (facebookUser == null) {
			return null;
		}
		
		Collection<com.idega.user.data.User> users = getUsersByEmail(facebookUser.getEmail());
		if (ListUtil.isEmpty(users)) {
			return createUser(facebookUser);
		}

		return users.iterator().next();
	}
	
	protected Collection<com.idega.user.data.User> getUsersByEmail(String email) {
		if (StringUtil.isEmpty(email)) {
			return null;
		}
		
		return getUserBusiness().getUsersByEmail(email);
	}
	
	protected com.idega.user.business.UserBusiness getUserBusiness() {
		if (this.userBusiness != null) {
			return this.userBusiness;
		}
		
		try {
			this.userBusiness = IBOLookup.getServiceInstance(
					CoreUtil.getIWContext(), UserBusiness.class);
		} catch (IBOLookupException e) {
			getLogger().log(Level.WARNING, 
					"Failed to get " + UserBusiness.class + ", cause of: ", e);
		}
		
		return this.userBusiness;
	}
}
