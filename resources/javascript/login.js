var LoginHelper = {};

LoginHelper.useSubmitLinks = false;
LoginHelper.errorMessage = 'Error logging in: make sure user name and password are entered!';
LoginHelper.loggingMessage = 'Logging in...';

//	Provide an object like: {url: 'linkToTheServer', userNameParam: 'userName', passwordParam: 'password'}
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
		
	if (LoginHelper.useSubmitLinks) {		
		jQuery("form.loginForm a").click(function() {
			LoginHelper.logIn();
			return false;
		});
	}
});

LoginHelper.logIn = function() {
	if (LoginHelper.remoteLogins == null) {
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
		alert(LoginHelper.errorMessage);
		return false;
	}
	var password = jQuery('#password').attr('value');
	if (password == null || password == '') {
		alert(LoginHelper.errorMessage);
		return false;
	}
	
	var loginObject = LoginHelper.remoteLogins[0];
	removeElementFromArray(LoginHelper.remoteLogins, loginObject);
	
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