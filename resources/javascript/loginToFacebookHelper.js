var loginToFacebookHelper = {};
window.fbAsyncInit = function() {
    FB.init({
      appId      : '124693474406865', // App ID
      channelUrl : 'http://127.0.0.1:8085/channel', // Channel File
      status     : true, // check login status
      cookie     : true, // enable cookies to allow the server to access the session
      xfbml      : true  // parse XFBML
    });
};