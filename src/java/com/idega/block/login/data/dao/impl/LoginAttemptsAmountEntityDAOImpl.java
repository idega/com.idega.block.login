/**
 * @(#)LoginAttemptsAmountEntityDAOImpl.java    1.0.0 4:06:14 PM
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
package com.idega.block.login.data.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.block.login.data.LoginAttemptsAmountEntity;
import com.idega.block.login.data.dao.LoginAttemptsAmountEntityDAO;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.util.StringUtil;

/**
 * <p>Implementation of {@link LoginAttemptsAmountEntityDAO}</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 Jul 23, 2014
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
@Repository(LoginAttemptsAmountEntityDAO.BEAN_NAME)
@Transactional(readOnly = false)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class LoginAttemptsAmountEntityDAOImpl extends GenericDaoImpl implements
		LoginAttemptsAmountEntityDAO {

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.login.data.dao.LoginAttemptsAmountEntityDAO#create(java.lang.String, java.lang.Boolean)
	 */
	@Override
	public LoginAttemptsAmountEntity create(String ip, Boolean failed) {
		return update(null, ip, failed);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.LoginAttemptsAmountEntityDAO#update(com.idega.block.login.data.LoginAttemptsAmountEntity)
	 */
	@Override
	public LoginAttemptsAmountEntity update(LoginAttemptsAmountEntity entity) {
		if (
				entity != null
				&& !StringUtil.isEmpty(entity.getIp())
				&& entity.getFailed() != null) {

			if (entity.getId() == null) {
				entity.setCreationDate(System.currentTimeMillis());
				persist(entity);
			} else {
				merge(entity);
			}

			if (entity.getId() == null) {
				getLogger().warning("Failed to create or update entity " + 
						entity.getClass().getSimpleName());
			} else {
				getLogger().info(entity.getClass().getSimpleName() + " by id: "  + 
						entity.getId() + " has been successfully created/updated!");
			}

			return entity;
		}

		getLogger().warning("Entity " +	entity.getClass().getSimpleName() + 
				" does not match criteria, see docs...");
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.LoginAttemptsAmountEntityDAO#update(java.lang.Long, java.lang.String, java.lang.Boolean)
	 */
	@Override
	public LoginAttemptsAmountEntity update(Long id, String ip, Boolean failed) {
		LoginAttemptsAmountEntity entity = findById(id);
		if (entity == null) {
			entity = new LoginAttemptsAmountEntity(); 
		}

		if (!StringUtil.isEmpty(ip)) {
			entity.setIp(ip);
		}

		if (failed != null) {
			entity.setFailed(failed);
		}

		return update(entity);
	}

	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.LoginAttemptsAmountEntityDAO#remove(java.lang.Long)
	 */
	@Override
	public void remove(Long id) {
		remove(findById(id));
	}

	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.LoginAttemptsAmountEntityDAO#findById(java.lang.Long)
	 */
	@Override
	public LoginAttemptsAmountEntity findById(Long id) {
		if (id != null) {
			return getSingleResult(
					LoginAttemptsAmountEntity.QUERY_FIND_BY_ID, 
					LoginAttemptsAmountEntity.class, 
					new Param(LoginAttemptsAmountEntity.idProp, id));
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.LoginAttemptsAmountEntityDAO#findAll()
	 */
	@Override
	public List<LoginAttemptsAmountEntity> findAll() {
		return getResultList(
				LoginAttemptsAmountEntity.QUERY_FIND_ALL, 
				LoginAttemptsAmountEntity.class);
	}

	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.LoginAttemptsAmountEntityDAO#findAll(java.util.Date, java.util.Date, java.lang.String, java.lang.Boolean)
	 */
	@Override
	public List<LoginAttemptsAmountEntity> findAll(Date from, Date to,
			String ip, Boolean failed) {
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT laae ")
		.append("FROM LoginAttemptsAmountEntity laae ")
		.append("WHERE laae.id IS NOT NULL ");

		if (from != null) {
			hql.append("AND laae.creationDate >= : from ");
		}

		if (to != null) {
			hql.append("AND laae.creationDate < : to ");
		}

		if (!StringUtil.isEmpty(ip)) {
			hql.append(" AND laae.ip = :" + LoginAttemptsAmountEntity.ipProp + " ");
		}

		if (failed != null) {
			hql.append("AND laae.failed = :" + LoginAttemptsAmountEntity.failedProp);
		}
		
		TypedQuery<LoginAttemptsAmountEntity> query = getEntityManager()
				.createQuery(hql.toString(), LoginAttemptsAmountEntity.class);
		if (from != null) {
			query = query.setParameter("from", from);
		}

		if (to != null) {
			query = query.setParameter("to", to);
		}

		if (!StringUtil.isEmpty(ip)) {
			query = query.setParameter(
					LoginAttemptsAmountEntity.ipProp, ip);
		}

		if (failed != null) {
			query = query.setParameter(
					LoginAttemptsAmountEntity.failedProp, failed);
		}		
		
		return query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.login.data.dao.LoginAttemptsAmountEntityDAO#findAmount(java.util.Date, java.util.Date, java.lang.String, java.lang.Boolean)
	 */
	@Override
	public long findAmount(Date from, Date to, String ip, Boolean failed) {
		return getSingleResult(
				LoginAttemptsAmountEntity.QUERY_FIND_AMOUNT_BY_CRITERIA,
				Long.class, 
				new Param("from", from),
				new Param("to", to),
				new Param(LoginAttemptsAmountEntity.failedProp, failed),
				new Param(LoginAttemptsAmountEntity.ipProp, ip));
	}
}
