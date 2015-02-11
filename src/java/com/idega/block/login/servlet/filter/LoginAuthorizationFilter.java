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

import com.idega.block.login.business.LoginRequestBean;
import com.idega.block.login.data.dao.LoginDAO;
import com.idega.user.data.User;
import com.idega.util.RequestUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

public class LoginAuthorizationFilter implements Filter {

	private static final Logger LOGGER = Logger.getLogger(LoginAuthorizationFilter.class.getName());

	public static final String	ATTRIBUTE_LOGGED_ON_USER = "com.idega.block.login.LoggedOnUser",
								USER_AUTHORIZATION_HEADER = RequestUtil.HEADER_AUTHORIZATION;

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		String authorization = request.getHeader(USER_AUTHORIZATION_HEADER);
		if (StringUtil.isEmpty(authorization)) {
			filterChain.doFilter(req, response);
			return;
		}

		try {
			LoginDAO loginDAO = ELUtil.getInstance().getBean(LoginDAO.BEAN_NAME);
			User user = loginDAO.authorize(authorization, User.class);
			if (user != null) {
				request.setAttribute(ATTRIBUTE_LOGGED_ON_USER, user);
				LoginRequestBean loginRequestBean = ELUtil.getInstance().getBean(LoginRequestBean.BEAN_NAME);
				loginRequestBean.setAuthorizedUser(user);
				loginRequestBean.setAuthorizedByFilter(true);
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed authorizing user", e);
		}
		filterChain.doFilter(req, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// We can initialize a filter using the init-params here
		// (which we defined in the deployment descriptor - web.xml)
	}

}