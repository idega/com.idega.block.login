jQuery(window).load(function () {
	jQuery("#passwordReminder").validate({
		errorClass: "errorMessage",

		rules: {
			"passwordReminder:email" : {
				required:	true
			},
			"passwordReminder:capchaText" : {
				required:	true
			}
		},

		messages: {
			"passwordReminder:email":		PasswordTokenCreatorHelper.FIELD_IS_REQUIRED,
			"passwordReminder:capchaText":	PasswordTokenCreatorHelper.FIELD_IS_REQUIRED
		}
	});
});

var PasswordTokenCreatorHelper = {
		FIELD_IS_REQUIRED: "Field is required!"
};