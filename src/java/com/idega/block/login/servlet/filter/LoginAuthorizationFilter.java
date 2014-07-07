package com.idega.block.login.servlet.filter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.idega.block.login.data.AuthorizationCode;
import com.idega.block.login.data.AuthorizationCodeHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.user.data.User;
import com.idega.util.RequestUtil;
import com.idega.util.StringUtil;

public class LoginAuthorizationFilter  implements Filter {
	public static final String ATTRIBUTE_LOGGED_ON_USER = "com.idega.block.login.LoggedOnUser";
	public static final String USER_AUTHORIZATION_HEADER = RequestUtil.HEADER_AUTHORIZATION;

	public void destroy() {
	}

	private AuthorizationCodeHome getAuthorizationCodeHome() {
		try {
			return (AuthorizationCodeHome) IDOLookup
					.getHome(AuthorizationCode.class);
		} catch (IDOLookupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private Logger getLogger(){
		return Logger.getLogger(LoginAuthorizationFilter.class.getName());
	}
	public void doFilter(ServletRequest req, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		try {
			AuthorizationCodeHome authorizationCodeHome = getAuthorizationCodeHome();
			HttpServletRequest request = (HttpServletRequest) req;
			String authorization = request.getHeader(USER_AUTHORIZATION_HEADER);
			User user = null;
			if (!StringUtil.isEmpty(authorization)) {
				user = (User) authorizationCodeHome.authorize(authorization,
						User.class);
			}
			request.setAttribute(ATTRIBUTE_LOGGED_ON_USER, user);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed authorizing user", e);
		}
		filterChain.doFilter(req, response);
	}
	

	public void init(FilterConfig filterConfig) throws ServletException {
		// We can initialize a filter using the init-params here
		// (which we defined in the deployment descriptor - web.xml)
	}
}
