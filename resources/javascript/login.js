jQuery.noConflict();

jQuery(document).ready(function() {
	jQuery("form.loginForm input[type='text'], form.loginForm input[type='password']").keypress(function(event) {
		if (isEnterEvent(event)) {
			jQuery(this).parents('form').submit();
			return false;
		}
		else {
			return true;
		}
	});

	jQuery("form.loginForm a").click(function() {
		jQuery("form.loginForm").submit();
		return false;
	});
});