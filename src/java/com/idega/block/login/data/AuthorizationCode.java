package com.idega.block.login.data;

import com.idega.data.IDOLegacyEntity;

public interface AuthorizationCode  extends IDOLegacyEntity {
	public Long getId();
	public void setId(Long id);
	public String getCode();
	public void setCode(String code);
	public String getAuthorization();
	public void setAuthorization(String authorization);
	public String getType();
	public void setType(String type);
}
