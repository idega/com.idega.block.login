package com.idega.block.login.data.dao.impl;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.block.login.data.AuthorizationCodeEntity;
import com.idega.block.login.data.dao.LoginDAO;
import com.idega.core.idgenerator.business.UUIDGenerator;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.Query;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.data.IDOEntity;
import com.idega.data.IDOHome;
import com.idega.data.IDOLookup;

@Repository(LoginDAO.BEAN_NAME)
@Transactional(readOnly = true)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class LoginDAOImpl extends GenericDaoImpl implements LoginDAO {

	@Override
	public List<AuthorizationCodeEntity> getAuthorizationCodeEntities(
			int start, int max) {
		Query query = getQueryNamed(AuthorizationCodeEntity.QUERY_GET_ALL);
		if(start > 0){
			query.setFirstResult(start);
		}
		if(max > 0){
			query.setMaxResults(max);
		}
		List<AuthorizationCodeEntity> entities = query.getResultList(AuthorizationCodeEntity.class);
		if(entities == null){
			return Collections.emptyList();
		}
		return entities;
	}

	@Override
	public AuthorizationCodeEntity getByCode(String code) {
		return getSingleResult(AuthorizationCodeEntity.QUERY_GET_BY_CODE, AuthorizationCodeEntity.class, new Param(AuthorizationCodeEntity.PROP_CODE, code));
	}
	
	
	@Override
	public <T extends IDOEntity> T authorize(String code,Class<T> c){
		try{
			AuthorizationCodeEntity entity = getByCode(code);
			IDOHome home = IDOLookup.getHome(c);
			return home.findByPrimaryKeyIDO(entity.getAuthorization());
		}catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed Authorizing by code " + code + " entity " + c, e);
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	@Override
	public String generateNewCode(String authorization, String type){
		AuthorizationCodeEntity entity = getSingleResult(AuthorizationCodeEntity.QUERY_GET_BY_AUTHORIZATION_AND_TYPE, 
				AuthorizationCodeEntity.class, 
				new Param(AuthorizationCodeEntity.PROP_AUTHORIZATION, authorization),
				new Param(AuthorizationCodeEntity.PROP_TYPE, type));
		if(entity == null){
			entity = new AuthorizationCodeEntity();
			entity.setAuthorization(authorization);
			entity.setType(type);
		}
		String code = UUIDGenerator.getInstance().generateUUID();
		entity.setCode(code);
		merge(entity);
		return code;
	}
	

}
