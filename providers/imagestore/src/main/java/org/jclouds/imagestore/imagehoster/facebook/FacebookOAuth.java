package org.jclouds.imagestore.imagehoster.facebook;

import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_APP_ID;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_APP_SECRET;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_CALLBACK_URL;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_PROPKEY_TOKEN;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_PROP_FILE_NAME;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_PROP_FILE_URI;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_TOKEN_URL;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_URL_PARAM_CLIENT_ID;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_URL_PARAM_CLIENT_SECRET;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_URL_PARAM_CODE;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_URL_PARAM_REDIRECT_URI;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_URL_PARAM_SCOPE;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_USER_AUTH_SCOPE;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_USER_AUTH_URL;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_USER_URL;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_URL_PARAM_GRANT_TYPE;
import static org.jclouds.imagestore.ImageStoreConstants.FACEBOOK_URL_PARAM_FB_EXCHANGE_TOKEN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultWebRequestor;
import com.restfb.FacebookClient;
import com.restfb.WebRequestor.Response;

/**
 * This class offers Facebook OAuth authentication.
 * 
 * @author Wolfgang Miller
 * 
 */
public final class FacebookOAuth {

    /** The Facebook properties. */
    private final Properties fp = new Properties();

    /** The HTTP-request service. */
    private final DefaultWebRequestor transport;

    /**
     * The FacbookOAuth constructor.
     * 
     * @throws ParserConfigurationException
     *             Signals that an ParserConfigurationException has occurred
     * @throws IOException
     *             Signals that an I/O exception has occurred
     */
    private FacebookOAuth() throws ParserConfigurationException, IOException {
        transport = new DefaultWebRequestor();
    }

    /**
     * Returns a new FacebookClient instance.
     * 
     * @return The created instance
     * @throws ParserConfigurationException
     *             Signals that an ParserConfigurationException has occurred
     * @throws IOException
     *             Signals that an I/O exception has occurred
     */
    static FacebookClient getNewFacebookClient() throws IOException, ParserConfigurationException {
        final FacebookOAuth foa = new FacebookOAuth();
        return new DefaultFacebookClient(foa.authenticateAppAndGetAccessToken());
    }

    /**
     * Authenticates the application and returns an access token.
     * 
     * @return The access token
     * @throws IOException
     *             Signals that an I/O exception has occurred
     */
    private String authenticateAppAndGetAccessToken() throws IOException {
        loadFacebookProperties();
        String fbAccessToken = fp.getProperty(FACEBOOK_PROPKEY_TOKEN);

        // check if there is an access token and test if the token is expired
        if (fbAccessToken != null) {

            if (!tokenHasExpired(fbAccessToken)) {
                return fbAccessToken;
            }

            System.out.println("Your access token has expired!");
        }

        System.out.println("Please log in to Facebook and visit the following URL:");
        System.out.println(generateUserAuthenticationURL());
        System.out.println("Now copy the URL of the visited Facebook site here:");
        final String verifier = readInVerifier();
        final String tokenURL = generateTokenURL(verifier);
        fbAccessToken = getAccessToken(tokenURL);
        final String longLivedTokenURL = generateLongLivedAccessTokenURL(fbAccessToken);
        final String fbLongLivedAccessToken = getAccessToken(longLivedTokenURL);
        saveAccessTokenToPropertiesFile(fbLongLivedAccessToken);
        return fbAccessToken;
    }

    /**
     * Checks if token has expired.
     * 
     * @param token
     *            The access token
     * @return True if connection was successful, false else
     * @throws IOException
     *             Signals that an I/O exception has occurred
     */
    private boolean tokenHasExpired(final String token) throws IOException {
        final Response rp =
            transport.executeGet(FACEBOOK_USER_URL + "?" + FACEBOOK_PROPKEY_TOKEN + "=" + token);
        return rp.getStatusCode() != 200;
    }

    /**
     * Fetches the access token from Facebook.
     * 
     * @param tokenURL
     *            The token URL
     * @return The access token
     * @throws IOException
     *             Signals that an I/O exception has occurred
     */
    private String getAccessToken(final String tokenURL) throws IOException {
        final Response response = transport.executeGet(tokenURL);
        // String with response
        final String rs = response.getBody();
        final int first = rs.indexOf('=') + 1;
        final int last = rs.indexOf('&');
        return rs.substring(first, last);
    }

    /**
     * Loads Facebook properties from Facebook properties file.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void loadFacebookProperties() throws IOException {
        File propFile = new File(FACEBOOK_PROP_FILE_URI);

        if (!propFile.exists()) {
            propFile.createNewFile();
        }

        fp.load(new FileInputStream(propFile));
    }

    /**
     * Saves the access token in the properties file.
     * 
     * @param token
     *            The access token
     * @throws IOException
     *             Signals that an I/O exception has occurred
     */
    private void saveAccessTokenToPropertiesFile(final String token) throws IOException {
        fp.setProperty(FACEBOOK_PROPKEY_TOKEN, token);
        File propFile = new File(FACEBOOK_PROP_FILE_URI);
        fp.store(new FileOutputStream(propFile), FACEBOOK_PROP_FILE_NAME);
    }

    /**
     * Returns the Facebook long-lived access token URL.
     * 
     * @param shortLivedUserToken
     *            The short-lived user access token
     * @return The long-lived user access token.
     */
    private String generateLongLivedAccessTokenURL(final String shortLivedUserToken) {

        // https://graph.facebook.com/oauth/access_token?client_id=APP_ID&client_secret=APP_SECRET&grant_type=fb_exchange_token
        // &fb_exchange_token=EXISTING_ACCESS_TOKEN
        return FACEBOOK_TOKEN_URL + "?" + FACEBOOK_URL_PARAM_CLIENT_ID + "=" + FACEBOOK_APP_ID + "&"
            + FACEBOOK_URL_PARAM_CLIENT_SECRET + "=" + FACEBOOK_APP_SECRET + "&"
            + FACEBOOK_URL_PARAM_GRANT_TYPE + "=" + FACEBOOK_URL_PARAM_FB_EXCHANGE_TOKEN + "&"
            + FACEBOOK_URL_PARAM_FB_EXCHANGE_TOKEN + "=" + shortLivedUserToken;
    }

    /**
     * Returns the Facebook token URL.
     * 
     * @param verifier
     *            The verifier string
     * @return The token URL.
     */
    private String generateTokenURL(final String verifier) {
        // Example URL:
        // https://graph.facebook.com/oauth/access_token?client_id=…&redirect_uri=http://yoururl/callbackhandler
        // &client_secret=…&code=…
        return FACEBOOK_TOKEN_URL + "?" + FACEBOOK_URL_PARAM_CLIENT_ID + "=" + FACEBOOK_APP_ID + "&"
            + FACEBOOK_URL_PARAM_REDIRECT_URI + "=" + FACEBOOK_CALLBACK_URL + "&"
            + FACEBOOK_URL_PARAM_CLIENT_SECRET + "=" + FACEBOOK_APP_SECRET + "&" + FACEBOOK_URL_PARAM_CODE
            + "=" + verifier;
    }

    /**
     * Returns the Facebook user authentication URL.
     * 
     * @return the authentication URL.
     */
    private String generateUserAuthenticationURL() {
        // Example URL:
        // https://graph.facebook.com/oauth/authorize?client_id=…&scope=user_photos,user_videos,publish_stream
        // &redirect_uri=http://yoururl/callbackhandler
        return FACEBOOK_USER_AUTH_URL + "?" + FACEBOOK_URL_PARAM_CLIENT_ID + "=" + FACEBOOK_APP_ID + "&"
            + FACEBOOK_URL_PARAM_SCOPE + "=" + FACEBOOK_USER_AUTH_SCOPE + "&"
            + FACEBOOK_URL_PARAM_REDIRECT_URI + "=" + FACEBOOK_CALLBACK_URL;
    }

    /**
     * Reads in and extracts the verifier from the Facebook-URL.
     * 
     * @return The verifier string
     * @throws IOException
     *             IOException Signals that an I/O exception has occurred
     */
    private String readInVerifier() throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        final String facebookAuthURL = br.readLine();
        return facebookAuthURL.split("=")[1];
    }

}
