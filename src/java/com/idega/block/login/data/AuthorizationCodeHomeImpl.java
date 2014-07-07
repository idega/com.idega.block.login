package com.idega.block.login.data;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;
import com.idega.data.IDOHome;
import com.idega.data.IDOLookup;


public class AuthorizationCodeHomeImpl  extends IDOFactory implements AuthorizationCodeHome {
	private static final long serialVersionUID = 926864602877674100L;

	protected Class getEntityInterfaceClass() {
		return AuthorizationCode.class;
	}

	private Logger getLogger(){
		return Logger.getLogger(AuthorizationCodeHomeImpl.class.getName());
	}
	public Collection getAuthorizationCodeEntities(int start, int max) {
		try{
			IDOEntity entity = this.idoCheckOutPooledEntity();
			Collection pks = ((AuthorizationCodeBMPBean) entity).getAuthorizationCodeEntities(start, max);
			this.idoCheckInPooledEntity(entity);
			return this.getEntityCollectionForPrimaryKeys(pks);
		}catch (FinderException e) {
		}
		return Collections.emptyList();
	}

	public AuthorizationCode getByCode(String code) {
		try{
			IDOEntity entity = this.idoCheckOutPooledEntity();
			Object pk = ((AuthorizationCodeBMPBean) entity).getByCode(code);
			this.idoCheckInPooledEntity(entity);
			return (AuthorizationCode) this.findByPrimaryKeyIDO(pk);
		}catch (FinderException e) {
		}
		return null;
	}

	public IDOEntity authorize(String code, Class c) {
		try{
			AuthorizationCode entity = getByCode(code);
			IDOHome home = IDOLookup.getHome(c);
			return home.findByPrimaryKeyIDO(entity.getAuthorization());
		}catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed Authorizing by code " + code + " entity " + c, e);
		}
		return null;
	}

}
