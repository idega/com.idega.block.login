jQuery.noConflict();

jQuery(document).ready(function() {
	jQuery('a.passwordChanger').each(function() {
		jQuery(this).attr('type', 'ajax');
	});
	
	jQuery('a.passwordChanger').fancybox().trigger('click');
});