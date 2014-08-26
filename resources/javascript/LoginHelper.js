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
		LoginServices.savePassword(password,{
			callback : function(reply) { 
				if(reply.status == "OK" || reply.message == null){
					document.getElementById(msgId).innerHTML = "";
					passwordChangeValidation("success", msgId, saveErrorMsg); 
					return;
				}
				document.getElementById(msgId).innerHTML = reply.message;
			}
		});  
	}
}