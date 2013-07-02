/**
 * @(#)GoogleLoginServiceImpl.java    1.0.0 10:46:55 AM
 *
 * Idega Software hf. Source Code Licence Agreement x
 *
 * This agreement, made this 10th of February 2006 by and between 
 * Idega Software hf., a business formed and operating under laws 
 * of Iceland, having its principal place of business in Reykjavik, 
 * Iceland, hereinafter after referred to as "Manufacturer" and Agura 
 * IT hereinafter referred to as "Licensee".
 * 1.  License Grant: Upon completion of this agreement, the source 
 *     code that may be made available according to the documentation for 
 *     a particular software product (Software) from Manufacturer 
 *     (Source Code) shall be provided to Licensee, provided that 
 *     (1) funds have been received for payment of the License for Software and 
 *     (2) the appropriate License has been purchased as stated in the 
 *     documentation for Software. As used in this License Agreement, 
 *     Licensee shall also mean the individual using or installing 
 *     the source code together with any individual or entity, including 
 *     but not limited to your employer, on whose behalf you are acting 
 *     in using or installing the Source Code. By completing this agreement, 
 *     Licensee agrees to be bound by the terms and conditions of this Source 
 *     Code License Agreement. This Source Code License Agreement shall 
 *     be an extension of the Software License Agreement for the associated 
 *     product. No additional amendment or modification shall be made 
 *     to this Agreement except in writing signed by Licensee and 
 *     Manufacturer. This Agreement is effective indefinitely and once
 *     completed, cannot be terminated. Manufacturer hereby grants to 
 *     Licensee a non-transferable, worldwide license during the term of 
 *     this Agreement to use the Source Code for the associated product 
 *     purchased. In the event the Software License Agreement to the 
 *     associated product is terminated; (1) Licensee's rights to use 
 *     the Source Code are revoked and (2) Licensee shall destroy all 
 *     copies of the Source Code including any Source Code used in 
 *     Licensee's applications.
 * 2.  License Limitations
 *     2.1 Licensee may not resell, rent, lease or distribute the 
 *         Source Code alone, it shall only be distributed as a 
 *         compiled component of an application.
 *     2.2 Licensee shall protect and keep secure all Source Code 
 *         provided by this this Source Code License Agreement. 
 *         All Source Code provided by this Agreement that is used 
 *         with an application that is distributed or accessible outside
 *         Licensee's organization (including use from the Internet), 
 *         must be protected to the extent that it cannot be easily 
 *         extracted or decompiled.
 *     2.3 The Licensee shall not resell, rent, lease or distribute 
 *         the products created from the Source Code in any way that 
 *         would compete with Idega Software.
 *     2.4 Manufacturer's copyright notices may not be removed from 
 *         the Source Code.
 *     2.5 All modifications on the source code by Licencee must 
 *         be submitted to or provided to Manufacturer.
 * 3.  Copyright: Manufacturer's source code is copyrighted and contains 
 *     proprietary information. Licensee shall not distribute or 
 *     reveal the Source Code to anyone other than the software 
 *     developers of Licensee's organization. Licensee may be held 
 *     legally responsible for any infringement of intellectual property 
 *     rights that is caused or encouraged by Licensee's failure to abide 
 *     by the terms of this Agreement. Licensee may make copies of the 
 *     Source Code provided the copyright and trademark notices are 
 *     reproduced in their entirety on the copy. Manufacturer reserves 
 *     all rights not specifically granted to Licensee.
 *
 * 4.  Warranty & Risks: Although efforts have been made to assure that the 
 *     Source Code is correct, reliable, date compliant, and technically 
 *     accurate, the Source Code is licensed to Licensee as is and without 
 *     warranties as to performance of merchantability, fitness for a 
 *     particular purpose or use, or any other warranties whether 
 *     expressed or implied. Licensee's organization and all users 
 *     of the source code assume all risks when using it. The manufacturers, 
 *     distributors and resellers of the Source Code shall not be liable 
 *     for any consequential, incidental, punitive or special damages 
 *     arising out of the use of or inability to use the source code or 
 *     the provision of or failure to provide support services, even if we 
 *     have been advised of the possibility of such damages. In any case, 
 *     the entire liability under any provision of this agreement shall be 
 *     limited to the greater of the amount actually paid by Licensee for the 
 *     Software or 5.00 USD. No returns will be provided for the associated 
 *     License that was purchased to become eligible to receive the Source 
 *     Code after Licensee receives the source code. 
 */
package com.idega.block.login.business.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow.Builder;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfo;
import com.idega.block.login.business.GoogleLoginService;
import com.idega.block.login.data.dao.GoogleCredentialEntityDAO;
import com.idega.core.business.DefaultSpringBean;
import com.idega.core.file.util.MimeTypeUtil;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>TODO</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 Jun 25, 2013
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 */
@Service(GoogleLoginService.BEAN_NAME)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class GoogleLoginServiceImpl extends DefaultSpringBean implements
		GoogleLoginService {

	public static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
	
	public static final String PRM_CLIENT_ID = "client_id";
	
	/* Set up the HTTP transport and JSON factory */
	public static HttpTransport HTTP_TRANSPORT;

	/* Global instance of the JSON factory. */
	public static final JsonFactory JSON_FACTORY = new JacksonFactory();

	/** OAuth 2.0 scopes. */
	private static final List<String> SCOPES = Arrays.asList(
			"https://www.googleapis.com/auth/userinfo.profile",
			"https://www.googleapis.com/auth/userinfo.email");
	
	private GoogleCredentialEntityDAO googleCredentialEntityDAO;
	
	public Details getDetails(String clientId, String clientSecret) {
		Details details = new Details();
		details.setFactory(JSON_FACTORY);
		
		if (!StringUtil.isEmpty(clientId)) {
			details.setClientId(clientId);
		}
		
		if (!StringUtil.isEmpty(clientSecret)) {
			details.setClientSecret(clientSecret);
		}
		
		return details;
	}
	
	public GoogleClientSecrets getGoogleClientSecrets(String clientId, String clientSecret) {
		GoogleClientSecrets gcs = new GoogleClientSecrets();
		gcs.setFactory(JSON_FACTORY);
		gcs.setInstalled(getDetails(clientId, clientSecret));
		return gcs;
	}
	
	public AuthorizationCodeFlow getAuthorizationCodeFlow(String clientId, String clientSecret) {
		Builder builder = new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT, 
				JSON_FACTORY, 
				getGoogleClientSecrets(clientId, clientSecret), 
				SCOPES);

		builder = builder.setCredentialStore(getGoogleCredentialEntityDAO());
		return builder.build();
	}
	
	protected Credential authorize() {
//		com.google.api.client.googleapis.auth.oauth2.GoogleCredential.Builder gc = new GoogleCredential.Builder();
//		gc.setServiceAccountId("martynas@idega.is");
//		gc.setClientSecrets(getGoogleClientSecrets(
//				"519770810128.apps.googleusercontent.com", 
//				"-Z8kotDpMfbSZ1lmpApfsPvN"));
//		gc.setJsonFactory(JSON_FACTORY);
//				
//		GoogleAuthorizationCodeFlow flow = getGoogleAuthorizationCodeFlow("519770810128.apps.googleusercontent.com", "-Z8kotDpMfbSZ1lmpApfsPvN");
//		GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI);
//		String urlString = url.build();
//		
//		try {
//			return new AuthorizationCodeInstalledApp(
//					getGoogleAuthorizationCodeFlow(
//							"519770810128.apps.googleusercontent.com",
//							"-Z8kotDpMfbSZ1lmpApfsPvN"),
//					new LocalServerReceiver()).authorize("user");
//		} catch (IOException e) {
//			java.util.logging.Logger.getLogger(getClass().getName()).log(Level.WARNING, "", e);
//		}

		return null;
	}
	
	public static final String GOOGLE_HTTP_AUTHORIZATION_REQUEST_ENDPOINT = "https://accounts.google.com/o/oauth2/device/code";
	public static final String GOOGLE_HTTP_HOST = "accounts.google.com";
	
	public static final String PRM_HOST = "Host";
	public static final String PRM_ACCEPT = "Accept";
	public static final String PRM_CONTENT_TYPE = "Content-Type";
	
	protected HttpPost getAuthorizationRequest(UrlEncodedFormEntity parameters) {
		if (parameters == null) {
			return null;
		}
		
		HttpPost post = new HttpPost(GOOGLE_HTTP_AUTHORIZATION_REQUEST_ENDPOINT);
		post.setEntity(parameters);
		post.setHeader(PRM_HOST, GOOGLE_HTTP_HOST);
		post.setHeader(PRM_ACCEPT, MimeTypeUtil.MIME_TYPE_JSON);
		post.setHeader(PRM_CONTENT_TYPE, MimeTypeUtil.MIME_TYPE_ENCODED_URL);
		return null;
	}
	
	private Object getUserToken(String clientId, String clientSecret) {
		// Authentication to the services is accomplished through HTTP requests
		// see https://developers.google.com/accounts/docs/OAuth2ForDevices
		// OAuth 2.0 for devices doesn't seem to be available in libraries yet.

		BasicNameValuePair[] params = {
				new BasicNameValuePair(PRM_CLIENT_ID, clientId),
				new BasicNameValuePair("scope", SCOPES.get(0) + CoreConstants.SPACE + SCOPES.get(1))
		};

		
		
		
		UrlEncodedFormEntity urlEncodedFormEntity = null;
		try {
			urlEncodedFormEntity = new UrlEncodedFormEntity(Arrays.asList(params));
		} catch (UnsupportedEncodingException e1) {
			getLogger().log(
					Level.WARNING, 
					"URL encoding is not supported, cause of: ", e1);
		}

		//urlEncodedFormEntity.setContentEncoding(HTTP.UTF_8);
		
		
		HttpResponse response = null;
		ApacheHttpTransport transport = new ApacheHttpTransport();
		org.apache.http.client.HttpClient httpclient = transport.getHttpClient();
		try {
			response = httpclient.execute(getAuthorizationRequest(urlEncodedFormEntity));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GenericJson userCodeValues = null;
		
		if (response != null) {
			try {
				JacksonFactory factory = new JacksonFactory();
				
				InputStream is = response.getEntity().getContent();
				JsonParser parser = factory.createJsonParser(is);
				userCodeValues = parser.parse(GenericJson.class, null);
				is.close();
				
				BigDecimal expires = (BigDecimal) userCodeValues.get("expires_in");
				Properties properties = new Properties();
				properties.put("expires_in", Integer.toString(expires.intValue()));
				BigDecimal interval = (BigDecimal) userCodeValues.get("interval");
				properties.put("interval", Integer.toString(interval.intValue()));
				properties.put("device_code", userCodeValues.get("device_code"));
				properties.put("verification_url", userCodeValues.get("verification_url"));
				properties.put("user_code", userCodeValues.get("user_code"));		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	@Override
	public Object login(String clientId, String clientSecret) {

		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (GeneralSecurityException e) {
			getLogger().log(Level.WARNING, 
					"Unable to get secure session cause of:", e);
		} catch (IOException e) {
			getLogger().log(Level.WARNING, 
					"Unable to connect to secure session cause of:", e);
		}
		
		// authorization
        Credential credential = authorize();
        // set up global Oauth2 instance
        Oauth2 oauth2 = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build();
        // run commands
        try {
			tokenInfo(credential.getAccessToken(), 
					getGoogleClientSecrets(clientId, clientSecret), oauth2);
		} catch (IOException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(Level.WARNING, "", e);
		}
        // success!        
        try {
			userInfo(oauth2);
		} catch (IOException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(Level.WARNING, "", e);
		}
        
		return null;
		
	    // Generate the URL to which we will direct users
//	    String authorizeUrl = new GoogleAuthorizationRequestUrl(username,
//	        CALLBACK_URL, SCOPE).build();
//	    System.out.println("Paste this url in your browser: " + authorizeUrl);

	    // Wait for the authorization code
//	    System.out.println("Type the code you received here: ");
//	    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//	    String authorizationCode = in.readLine();
//
//	    // Exchange for an access and refresh token
//	    GoogleAuthorizationCodeGrant authRequest = new GoogleAuthorizationCodeGrant(TRANSPORT,
//	        JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, authorizationCode, CALLBACK_URL);
//	    authRequest.useBasicAuthorization = false;
//	    AccessTokenResponse authResponse = authRequest.execute();
//	    String accessToken = authResponse.accessToken;
//	    GoogleAccessProtectedResource access = new GoogleAccessProtectedResource(accessToken,
//	        TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, authResponse.refreshToken);
//	    HttpRequestFactory rf = TRANSPORT.createRequestFactory(access);
//	    System.out.println("Access token: " + authResponse.accessToken);
//
//	    // Make an authenticated request
//	    GenericUrl shortenEndpoint = new GenericUrl("https://www.googleapis.com/urlshortener/v1/url");
//	    String requestBody =
//	        "{\"longUrl\":\"http://farm6.static.flickr.com/5281/5686001474_e06f1587ff_o.jpg\"}";
//	    HttpRequest request = rf.buildPostRequest(shortenEndpoint,
//	        ByteArrayContent.fromString("application/json", requestBody))
//	    HttpResponse shortUrl = request.execute();
//	    BufferedReader output = new BufferedReader(new InputStreamReader(shortUrl.getContent()));
//	    System.out.println("Shorten Response: ");
//	    for (String line = output.readLine(); line != null; line = output.readLine()) {
//	      System.out.println(line);
//	    }
//
//	    // Refresh a token (SHOULD ONLY BE DONE WHEN ACCESS TOKEN EXPIRES)
//	    access.refreshToken();
//	    System.out.println("Original Token: " + accessToken + " New Token: " + access.getAccessToken());
	}
	
	private static void tokenInfo(String accessToken, GoogleClientSecrets gcs, Oauth2 oauth2) throws IOException {
	    Tokeninfo tokeninfo = oauth2.tokeninfo().setAccessToken(accessToken).execute();
	    System.out.println(tokeninfo.toPrettyString());
	    if (!tokeninfo.getAudience().equals(gcs.getDetails().getClientId())) {
	      System.err.println("ERROR: audience does not match our client ID!");
	    }
	  }

	private static void userInfo(Oauth2 oauth2) throws IOException {
		Userinfo userinfo = oauth2.userinfo().get().execute();
		System.out.println(userinfo.toPrettyString());
	}

	protected GoogleCredentialEntityDAO getGoogleCredentialEntityDAO() {
		if (this.googleCredentialEntityDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		
		return this.googleCredentialEntityDAO;
	}
}
