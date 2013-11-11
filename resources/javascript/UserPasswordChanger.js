jQuery.noConflict();

jQuery(document).ready(function() {
	jQuery('a.passwordChanger').each(function() {
		jQuery(this).attr('data-fancybox-type', 'ajax');
	});
	
	jQuery('a.passwordChanger').fancybox({type: 'ajax'}).trigger('click');
});