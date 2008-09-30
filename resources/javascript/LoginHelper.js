function changeUserPassword(link){
	var width = Math.round(window.getWidth() * 0.25);
	var height = Math.round(window.getHeight() * 0.3);
	MOOdalBox.init({resizeDuration: 0, evalScripts: true, animateCaption: false, defContentsWidth: width, defContentsHeight: height});
	
	MOOdalBox.open(link, '', '');
}

function passwordChangeValidation(changeStatus, msgId, saveErrorMsg) {
	if(changeStatus = 'success') {
		MOOdalBox.close();
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
	