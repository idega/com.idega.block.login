/**
 * @(#)PasswordTokenEntityDAOImpl.java    1.0.0 9:14:45 AM
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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.block.login.data.PasswordTokenEntity;
import com.idega.block.login.data.dao.PasswordTokenEntityDAO;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

/**
 * <p>implementation for {@link PasswordTokenEntityDAO} data object</p>
 * <p>You can report about problems to:
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 Jan 15, 2014
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
@Repository(PasswordTokenEntityDAO.BEAN_NAME)
@Transactional(readOnly = false)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class PasswordTokenEntityDAOImpl extends GenericDaoImpl implements
		PasswordTokenEntityDAO {

	private SecureRandom random = new SecureRandom();

	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.PasswordTokenEntityDAO#update(com.idega.block.login.data.PasswordTokenEntity)
	 */
	@Override
	public PasswordTokenEntity update(PasswordTokenEntity entity) {
		if (
				entity != null &&
				!StringUtil.isEmpty(entity.getToken()) &&
				!StringUtil.isEmpty(entity.getIp()) &&
				!StringUtil.isEmpty(entity.getUuid()) &&
				entity.getCreationTime() != null &&
				entity.getExpirationTime() != null) {
			if (entity.getId() == null) {
				persist(entity);
			} else {
				merge(entity);
			}

			if (entity.getId() == null) {
				getLogger().warning("Failed to create or update entity " +
						entity.getClass().getSimpleName());
			} else {
				getLogger().fine(entity.getClass().getSimpleName() + " by id: "  +
						entity.getId() + " has been successfully created/updated!");
			}

			return entity;
		}

		getLogger().warning("Entity " +	entity.getClass().getSimpleName() +
				" does not match criteria, see docs...");
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.PasswordTokenEntityDAO#update(java.lang.Long, java.lang.String, java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public PasswordTokenEntity update(Long id, String token, Long lifetime,
			String uuid, String ip) {
		PasswordTokenEntity entity = findById(id);
		if (entity == null) {
			entity = new PasswordTokenEntity();
		}

		if (!StringUtil.isEmpty(token)) {
			entity.setToken(token);
		}

		if (entity.getCreationDate() == null) {
			entity.setCreationDate(new Date(System.currentTimeMillis()));
		}

		if (lifetime != null) {
			entity.setExpirationTime(entity.getCreationTime() + lifetime);
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(entity.getCreationTime());
			calendar.add(Calendar.HOUR, 12);
			entity.setExpirationTime(calendar.getTimeInMillis());
		}

		if (!StringUtil.isEmpty(uuid)) {
			entity.setUuid(uuid);
		}

		if (!StringUtil.isEmpty(ip)) {
			entity.setIp(ip);
		}

		return update(entity);
	}

	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.PasswordTokenEntityDAO#create(java.lang.String, java.lang.String)
	 */
	@Override
	public PasswordTokenEntity create(String uuid, String ip) {
		return create(uuid, ip, null);
	}

	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.PasswordTokenEntityDAO#create(java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public PasswordTokenEntity create(String uuid, String ip, Long lifetime) {
		if (StringUtil.isEmpty(uuid) || StringUtil.isEmpty(ip)) {
			return null;
		}

		List<PasswordTokenEntity> entities = findAll(uuid);
		if (!ListUtil.isEmpty(entities)) {
			for (PasswordTokenEntity entity : entities) {
				remove(entity);
			}
		}

		return update(
				null,
				new BigInteger(100, this.random).toString(),
				lifetime,
				uuid,
				ip
		);
	}

	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.PasswordTokenEntityDAO#remove(com.idega.block.login.data.PasswordTokenEntity)
	 */
	@Override
	public boolean remove(PasswordTokenEntity entity) {
		if (entity != null) {
			getLogger().fine(PasswordTokenEntity.class.getSimpleName() +
					" by id: '" + entity.getId() + "' removed!");
			super.remove(entity);
			return Boolean.TRUE;
		}

		getLogger().warning("Failed to remove " + PasswordTokenEntity.class.getSimpleName());
		return Boolean.FALSE;
	}

	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.PasswordTokenEntityDAO#remove(long)
	 */
	@Override
	public boolean remove(long primaryKey) {
		return remove(findById(primaryKey));
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.login.data.dao.PasswordTokenEntityDAO#removeByUUID(java.lang.String)
	 */
	@Override
	public boolean removeByUUID(String uuid) {
		if (StringUtil.isEmpty(uuid)) {
			return Boolean.FALSE;
		}

		List<PasswordTokenEntity> entities = findAll(uuid);
		for (PasswordTokenEntity entity : entities) {
			if (!remove(entity)) {
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.login.data.dao.PasswordTokenEntityDAO#findById(long)
	 */
	@Override
	public PasswordTokenEntity findById(Long id) {
		if (id != null) {
			return getSingleResult(
					PasswordTokenEntity.QUERY_FIND_BY_ID,
					PasswordTokenEntity.class,
					new Param(PasswordTokenEntity.idProp, id));
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.PasswordTokenEntityDAO#findAll()
	 */
	@Override
	public List<PasswordTokenEntity> findAll() {
		return getResultList(
				PasswordTokenEntity.QUERY_FIND_ALL,
				PasswordTokenEntity.class);
	}

	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.PasswordTokenEntityDAO#findAll(java.lang.String)
	 */
	@Override
	public List<PasswordTokenEntity> findAll(String uuid) {
		if (!StringUtil.isEmpty(uuid)) {
			return getResultList(
					PasswordTokenEntity.QUERY_FIND_ALL_BY_UUID,
					PasswordTokenEntity.class,
					new Param(PasswordTokenEntity.uuidProp, uuid));
		}

		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.PasswordTokenEntityDAO#findAllValid()
	 */
	@Override
	public List<PasswordTokenEntity> findAllValid() {
		return getResultList(
				PasswordTokenEntity.QUERY_FIND_ALL_VALID,
				PasswordTokenEntity.class,
				new Param(
						PasswordTokenEntity.expirationDateProp,
						new Date(System.currentTimeMillis())));
	}

	/* (non-Javadoc)
	 * @see com.idega.block.login.data.dao.PasswordTokenEntityDAO#findAllValid(java.lang.String)
	 */
	@Override
	public List<PasswordTokenEntity> findAllValid(String uuid) {
		if (!StringUtil.isEmpty(uuid)) {
			return getResultList(
					PasswordTokenEntity.QUERY_FIND_ALL_VALID_BY_UUID,
					PasswordTokenEntity.class,
					new Param(PasswordTokenEntity.uuidProp, uuid),
					new Param(
							PasswordTokenEntity.expirationDateProp,
							new Date(System.currentTimeMillis())));
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.block.login.data.dao.PasswordTokenEntityDAO#findByToken(java.lang.String)
	 */
	@Override
	public PasswordTokenEntity findByToken(String token) {
		if (StringUtil.isEmpty(token)) {
			return null;
		}

		return getSingleResult(
				PasswordTokenEntity.QUERY_FIND_BY_TOKEN,
				PasswordTokenEntity.class,
				new Param(PasswordTokenEntity.tokenProp, token),
				new Param(
						PasswordTokenEntity.expirationDateProp,
						new Date(System.currentTimeMillis())));
	}

	@Override
	public PasswordTokenEntity create(String uuid, String ip, Long lifetime, Integer strictLength) {
		if (strictLength == null) {
			return create(uuid, ip, lifetime);
		}

		if (StringUtil.isEmpty(uuid) || StringUtil.isEmpty(ip)) {
			return null;
		}

		List<PasswordTokenEntity> entities = findAll(uuid);
		if (!ListUtil.isEmpty(entities)) {
			for (PasswordTokenEntity entity : entities) {
				remove(entity);
			}
		}

		String key = null;
		if (strictLength == 6) {
			key = new BigInteger(20, this.random).toString();
			if (!StringUtil.isEmpty(key)) {
				if (key.length() > 6) {
					key = key.substring(0, 6);
				} else if (key.length() < 6) {
					while (key.length() < 6) {
						key = key + "0";
					}
				}
			}
		}

		return update(
				null,
				key,
				lifetime,
				uuid,
				ip
		);
	}

}
