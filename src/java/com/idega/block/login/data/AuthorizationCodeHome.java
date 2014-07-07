package com.idega.block.login.data;

import java.util.Collection;

import com.idega.data.IDOEntity;
import com.idega.data.IDOHome;

public interface AuthorizationCodeHome  extends IDOHome {
	public Collection getAuthorizationCodeEntities(int start,int max);
	public AuthorizationCode getByCode(String code);
	public IDOEntity  authorize(String code,Class c);
}

