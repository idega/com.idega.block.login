package com.idega.block.login;

public class LoginConstants {

	public static final String	IW_BUNDLE_IDENTIFIER = "com.idega.block.login",
								TICKET_WEBSERVICE_PATH = "/TicketServices/Authentication",
								LOGIN_TYPE = "login_type",

								OAUTH_DEFAULT_CLIENT_ID = "oauth.default_client_id",
								OAUTH_UNEXPIRING_CLIENT_ID = "oauth.unexpiring_client_id";

	public enum LoginType {

		CREDENTIALS, ISLAND_DOT_IS;

	}
}