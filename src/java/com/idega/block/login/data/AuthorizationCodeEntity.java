package com.idega.block.login.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = AuthorizationCodeEntity.TABLE_NAME,
		uniqueConstraints={
			@UniqueConstraint(columnNames={"code"})
		}
)
@NamedQueries({
	@NamedQuery(
			name = AuthorizationCodeEntity.QUERY_GET_ALL,
			query = "FROM AuthorizationCodeEntity "),
	@NamedQuery(
			name = AuthorizationCodeEntity.QUERY_GET_BY_CODE,
			query = "FROM AuthorizationCodeEntity  a WHERE a.code = :" + AuthorizationCodeEntity.PROP_CODE),
	@NamedQuery(
			name = AuthorizationCodeEntity.QUERY_GET_BY_AUTHORIZATION_AND_TYPE,
			query = "FROM AuthorizationCodeEntity  a WHERE a.authorization = :" + AuthorizationCodeEntity.PROP_AUTHORIZATION
			+ " AND a.type = :" + AuthorizationCodeEntity.PROP_TYPE),
	@NamedQuery(
			name = AuthorizationCodeEntity.QUERY_GET_BY_AUTHORIZATION_AND_TYPE_AND_CODE,
			query = "FROM AuthorizationCodeEntity  a WHERE a.authorization = :" + AuthorizationCodeEntity.PROP_AUTHORIZATION
			+ " AND a.type = :" + AuthorizationCodeEntity.PROP_TYPE + " AND a.code = :" + AuthorizationCodeEntity.PROP_CODE)
})
public class AuthorizationCodeEntity implements Serializable {

	private static final long serialVersionUID = 7951610486528794334L;

	public static final String TABLE_NAME = "ic_authorization_code";
	public static final String QUERY_GET_ALL = TABLE_NAME + ".getAll";
	public static final String QUERY_GET_BY_CODE = TABLE_NAME + ".getByCode";
	public static final String QUERY_GET_BY_AUTHORIZATION_AND_TYPE = TABLE_NAME + ".getByAuthorizationAndType",
								QUERY_GET_BY_AUTHORIZATION_AND_TYPE_AND_CODE = TABLE_NAME + ".getByAuthorizationAndTypeAndCode";

	public static final String PROP_ID = TABLE_NAME + "_id";
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;


	public static final String PROP_CODE = TABLE_NAME + "_code";
	@Column(name = "code")
	private String code;

	public static final String PROP_AUTHORIZATION = TABLE_NAME + "_authorization";
	@Column(name = "authorization")
	private String authorization;

	public static final String PROP_TYPE = TABLE_NAME + "_type";
	@Column(name = "type")
	private String type;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEncryptedCode() {
		if (code == null) {
			return null;
		}
		String str = code;
		char[] pass = new char[str.length() / 2];
		try {
			for (int i = 0; i < pass.length; i++) {
				pass[i] = (char) Integer.decode("0x" + str.charAt(i * 2) + str.charAt((i * 2) + 1)).intValue();
			}
			return String.valueOf(pass);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return str;
	}
	public void setEncryptedCode(String code) {
		try {
			String str = "";
			char[] pass = code.toCharArray();
			for (int i = 0; i < pass.length; i++) {
				String hex = Integer.toHexString(pass[i]);
				while (hex.length() < 2) {
					String s = "0";
					s += hex;
					hex = s;
				}
				str += hex;
			}
			if (str.equals("") && !code.equals("")) {
				str = null;
			}
			this.code = str;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			this.code = code;
		}
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getAuthorization() {
		return authorization;
	}
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}


}
