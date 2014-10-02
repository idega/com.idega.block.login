var LoginHelper = {};

LoginHelper.useSubmitLinks = false;
LoginHelper.errorMessage = 'Error logging in: make sure user name and password are entered!';
LoginHelper.errorMessageNoSMSCode = 'Error logging in: make sure SMS is entered!';
LoginHelper.loggingMessage = 'Logging in...';

//	Provide an object like: {initUrl: 'optional', url: 'linkToTheServer', userNameParam: 'userName', passwordParam: 'password'}
LoginHelper.remoteLogins = null;

jQuery(window).load(function() {
	jQuery("form.loginForm input[type='text'], form.loginForm input[type='password']").keypress(function(event) {
		if (isEnterEvent(event)) {
			LoginHelper.logIn();
			return false;
		} else {
			return true;
		}
	});
	
	jQuery("input[type='submit']", jQuery('form.loginForm')).click(function() {
		LoginHelper.logIn();
		return false;
	});
	
	jQuery("a.loginButton").click(function() {
		LoginHelper.logIn();
		return false;
	});
	
	
	jQuery("input[type='submit']", jQuery('form.smsCodeForm')).click(function() {
		LoginHelper.logInWithSMSCode();
		return false;
	});

	jQuery("a.smsCodeLoginButton").click(function() {
		LoginHelper.logInWithSMSCode();
		return false;
	});
	
	jQuery("input[type='button']", jQuery('form.smsCodeForm')).click(function() {
		LoginHelper.cancelWithSMSCode();
		return false;
	});
	
	jQuery("a.smsCodeCancelButton").click(function() {
		LoginHelper.cancelWithSMSCode();
		return false;
	});
	
});

LoginHelper.logIn = function() {
	if (jQuery('div.loggedIn').length > 0)
		LoginHelper.remoteLogins = null;
	
	if (jQuery('div.loginFailed').length > 0 || LoginHelper.remoteLogins == null || LoginHelper.remoteLogins.length == 0) {
		var form = jQuery("form.loginForm");
		form.removeAttr('onsubmit');
		form.submit();
		return true;
	}
	
	showLoadingMessage(LoginHelper.loggingMessage);
	LoginHelper.doRemoteLogins();
	return false;
}

LoginHelper.remoteLoginInAction = false;

LoginHelper.doRemoteLogins = function() {
	LoginHelper.remoteLoginInAction = false;
	
	if (LoginHelper.remoteLogins == null || LoginHelper.remoteLogins.length == 0)
		LoginHelper.continueLoggingIn();
	
	var userName = jQuery('#username').attr('value');
	if (userName == null || userName == '') {
		closeAllLoadingMessages();
		alert(LoginHelper.errorMessage);
		return false;
	}
	var password = jQuery('#password').attr('value');
	if (password == null || password == '') {
		closeAllLoadingMessages();
		alert(LoginHelper.errorMessage);
		return false;
	}
	
	var loginObject = LoginHelper.remoteLogins[0];
	removeElementFromArray(LoginHelper.remoteLogins, loginObject);
	
	if (loginObject.initUrl != null) {
		jQuery(document.body).append('<iframe onload="window.parent.LoginHelper.doRemoteLogin({url: \'' + loginObject.url + '\', userNameParam: \'' + loginObject.userNameParam +
			'\', passwordParam: \'' + loginObject.passwordParam + '\'}, \'' + userName + '\', \'' +	password + '\');" style="display: none;" src="' + loginObject.initUrl + '" />');
	} else {
		LoginHelper.doRemoteLogin(loginObject, userName, password);
	}
}

LoginHelper.doRemoteLogin = function(loginObject, userName, password) {
	var url = loginObject.url;
	var firstParam = '&';
	if (url.indexOf('?') == -1)
		firstParam = '?';
	url += firstParam + loginObject.userNameParam + '=' + userName + '&' + loginObject.passwordParam + '=' + password;
	var onLoadAction = 'window.parent.' + (LoginHelper.remoteLogins == null || LoginHelper.remoteLogins.length == 0 ?
		'LoginHelper.continueLoggingIn()' : 'LoginHelper.doRemoteLogins()') + ';';
	
	try {
		LoginHelper.remoteLoginInAction = true;
		jQuery(document.body).append('<iframe onload="' + onLoadAction + '" style="display: none;" src="' + url + '"></iframe>');
		
		//	Timer that checks for the errors during remote login
		window.setTimeout(function() {
			if (LoginHelper.remoteLoginInAction) {
				LoginHelper.doRemoteLogins();
			}
		}, 5000);
	} catch (e) {
		LoginHelper.doRemoteLogins();
	}
}

LoginHelper.continueLoggingIn = function() {
	LoginHelper.remoteLoginInAction = false;
	LoginHelper.remoteLogins = null;
	
	LoginHelper.logIn();
}




LoginHelper.logInWithSMSCode = function() {
	if (jQuery('div.loggedIn').length > 0)
		LoginHelper.remoteLogins = null;
	
	if (jQuery('div.loginFailed').length > 0 || LoginHelper.remoteLogins == null || LoginHelper.remoteLogins.length == 0) {
		var form = jQuery("form.smsCodeForm");
		form.removeAttr('onsubmit');
		form.submit();
		return true;
	}
	 
	showLoadingMessage(LoginHelper.loggingMessage);
	LoginHelper.doRemoteLoginsForSMSCode();
	return false;
}

LoginHelper.doRemoteLoginsForSMSCode = function() {
	LoginHelper.remoteLoginInAction = false;
	
	if (LoginHelper.remoteLogins == null || LoginHelper.remoteLogins.length == 0)
		LoginHelper.continueLoggingInWithSMS();
	
	var smsCode = jQuery('#smsCode').attr('value');
	if (smsCode == null || smsCode == '') {
		closeAllLoadingMessages();
		alert(LoginHelper.errorMessageNoSMSCode);
		return false;
	}
	
	var loginObject = LoginHelper.remoteLogins[0];
	removeElementFromArray(LoginHelper.remoteLogins, loginObject);
	
	if (loginObject.initUrl != null) {
		jQuery(document.body).append('<iframe onload="window.parent.LoginHelper.doRemoteLoginWithSMS({url: \'' + loginObject.url + '\', smsCodeParam: \'' + loginObject.smsCodeParam +
			 '\'}, \'' + smsCode + '\');" style="display: none;" src="' + loginObject.initUrl + '" />');
	} else {
		LoginHelper.doRemoteLoginWithSMS(loginObject, smsCode);
	}
}

LoginHelper.doRemoteLoginWithSMSCode = function(loginObject, smsCode) {
	var url = loginObject.url;
	var firstParam = '&';
	if (url.indexOf('?') == -1)
		firstParam = '?';
	url += firstParam + loginObject.smsCodeParam + '=' + smsCode;
	var onLoadAction = 'window.parent.' + (LoginHelper.remoteLogins == null || LoginHelper.remoteLogins.length == 0 ?
		'LoginHelper.continueLoggingInWithSMS()' : 'LoginHelper.doRemoteLoginsForSMSCode()') + ';';
	
	try {
		LoginHelper.remoteLoginInAction = true;
		jQuery(document.body).append('<iframe onload="' + onLoadAction + '" style="display: none;" src="' + url + '"></iframe>');
		
		//	Timer that checks for the errors during remote login
		window.setTimeout(function() {
			if (LoginHelper.remoteLoginInAction) {
				LoginHelper.doRemoteLoginsForSMSCode();
			}
		}, 5000);
	} catch (e) {
		LoginHelper.doRemoteLoginsForSMSCode();
	}
}

LoginHelper.continueLoggingInWithSMS = function() {
	LoginHelper.remoteLoginInAction = false;
	LoginHelper.remoteLogins = null;
	
	LoginHelper.logInWithSMS();
}

LoginHelper.cancelWithSMSCode = function() {
	jQuery('input[name="isCancel"]').val('true');
	
	if (jQuery('div.loggedIn').length > 0)
		LoginHelper.remoteLogins = null;
	
	if (jQuery('div.loginFailed').length > 0 || LoginHelper.remoteLogins == null || LoginHelper.remoteLogins.length == 0) {
		var form = jQuery("form.smsCodeForm");
		form.removeAttr('onsubmit');
		form.submit();
		return true;
	}
	 
	showLoadingMessage(LoginHelper.loggingMessage);
	LoginHelper.doRemoteLoginsForSMSCode();
	return false;
}

