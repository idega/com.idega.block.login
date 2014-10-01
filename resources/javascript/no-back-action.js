/**
 * Main idea is taken from http://jordanhollinger.com/2012/06/08/disable-the-back-button-using-html5/
 */
if (NoBackActionHelper == null) var NoBackActionHelper = {};

NoBackActionHelper.initialize = function(params) {
	if (params.loggedOut) {
		var history_api = typeof history.pushState !== 'undefined'
		if ( history_api ) history.pushState(null, '', '#no-back')
		else location.hash = '#no-back'
	}
	
	NoBackActionHelper.doPreventBack();
}

NoBackActionHelper.doPreventBack = function() {
	// It works without the History API, but will clutter up the history
	var history_api = typeof history.pushState !== 'undefined'
	
	// The previous page asks that it not be returned to
	if (location.hash == '#no-back') {
	  // Push "#no-back" onto the history, making it the most recent "page"
	  if ( history_api ) history.pushState(null, '', '#logged-out')
	  else location.hash = '#logged-out'
	  
	  // When the back button is pressed, it will harmlessly change the url
	  // hash from "#logged-out" to "#no-back", which triggers this function
	  window.onhashchange = function() {
	    // User tried to go back; rinse and repeat
	    if ( location.hash == '#no-back' ) {
	      if ( history_api ) history.pushState(null, '', '#logged-out')
	      else location.hash = '#logged-out'
	    }
	  }
	}
}