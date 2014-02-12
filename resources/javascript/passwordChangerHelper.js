jQuery(window).load(function () {
	jQuery("#passwordReset").validate({
		rules: {
			"passwordReset:newPassword" : {
				required:	true
			},
			"passwordReset:retypedPassword" : {
				required:	true,
				equalTo:	"input[id='passwordReset:newPassword']"
			}
		},
		messages: {
			"passwordReset:newPassword":		PasswordChangerHelper.FIELD_IS_REQUIRED,
			"passwordReset:retypedPassword":	PasswordChangerHelper.PASSWORDS_DO_NOT_MATCH
		}
	});
});

var PasswordChangerHelper = {
		FIELD_IS_REQUIRED: "Field is required!",
		PASSWORDS_DO_NOT_MATCH: "Passwords do not match!"
};