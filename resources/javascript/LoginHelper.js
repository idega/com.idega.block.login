function passwordChangeValidation(changeStatus, msgId, saveErrorMsg) {
	if(changeStatus = 'success') {
		parent.jQuery.fancybox.close();
	} else {
		document.getElementById(msgId).innerHTML = saveErrorMsg;
	}
}

function validateAndSavePassword(msgId, validateErrorMsg, saveErrorMsg) {
	document.getElementById(msgId).innerHTML = '';
	var password = document.getElementById('password').value;
	var password2 = document.getElementById('password2').value;
	
	if(password != password2) {
		document.getElementById(msgId).innerHTML = validateErrorMsg;
	} else {
		UserBusiness.changeUserPassword(password, function(str) { passwordChangeValidation(str, msgId, saveErrorMsg); });  
	}
}