package com.idega.block.login.data.dao;

import java.util.List;

import com.idega.block.login.data.AuthorizationCodeEntity;
import com.idega.data.IDOEntity;

public interface LoginDAO {
	static final String BEAN_NAME = "idegaLoginDAO";
	
	public List<AuthorizationCodeEntity> getAuthorizationCodeEntities(int start,int max);
	public AuthorizationCodeEntity getByCode(String code);
	public <T extends IDOEntity> T authorize(String code,Class<T> c);
	public void persist(Object product);
	public <T> T merge(T product);
}
