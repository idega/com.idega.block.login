package com.idega.block.login.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;

public class AuthorizationCodeBMPBean extends GenericEntity implements AuthorizationCode{
	private static final long serialVersionUID = -6342126443093677849L;
	public static final String TABLE_NAME = "ic_authorization_code";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_CODE = "code";
	public static final String COLUMN_AUTHORIZATION = "authorization";
	public static final String COLUMN_TYPE = "type";
	
	public String getEntityName() {
		return TABLE_NAME;
	}

	public String getIDColumnName() {
		return COLUMN_ID;
	}

	public void initializeAttributes() {
		this.addAttribute(getIDColumnName());
		this.addAttribute(COLUMN_CODE, COLUMN_CODE, String.class);
		this.addAttribute(COLUMN_AUTHORIZATION, COLUMN_AUTHORIZATION, String.class);
		this.addAttribute(COLUMN_TYPE, COLUMN_TYPE, String.class);
		setUnique(COLUMN_CODE, true);
	}
	
	public Long getId() {
		try{
			Long.valueOf(getPrimaryKey().toString());
		}catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public void setId(Long id) {
		setColumn(COLUMN_ID, id);
	}
	public String getCode() {
		return (String) getColumnValue(COLUMN_CODE);
	}
	public void setCode(String code) {
		setColumn(COLUMN_CODE, code);
	}
	public String getAuthorization() {
		return (String) getColumnValue(COLUMN_AUTHORIZATION);
	}
	public void setAuthorization(String authorization) {
		setColumn(COLUMN_AUTHORIZATION, authorization);
	}
	public String getType() {
		return (String) getColumnValue(COLUMN_TYPE);
	}
	public void setType(String type) {
		setColumn(COLUMN_TYPE, type);
	}
	

	public Collection getAuthorizationCodeEntities(int start, int max)  throws FinderException{
		Table table = new Table(this);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn(table));
		return idoFindPKsByQuery(query, max, start);
	}

	public Object getByCode(String code)   throws FinderException{
		Table table = new Table(this);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn(table));
		query.addCriteria(new MatchCriteria(table, COLUMN_CODE, MatchCriteria.EQUALS, code));
		return idoFindOnePKByQuery(query);
	}

}
