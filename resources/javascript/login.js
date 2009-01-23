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
	
	var state = jQuery(".loginForm input[name=login_state]").val();
	jQuery(".loginForm input[name=login_state]").val("");

	jQuery(".loginForm a, .loginForm div.submit input").click(function() {
		jQuery(".loginForm input[name=login_state]").val(state);
	});

	jQuery(".loginForm a").click(function() {
		jQuery(this).parents('form').submit();
		return false;
	});
});