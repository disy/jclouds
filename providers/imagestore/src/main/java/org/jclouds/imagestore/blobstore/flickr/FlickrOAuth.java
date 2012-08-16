/*
 * 
 */
package org.jclouds.imagestore.blobstore.flickr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

// TODO: Auto-generated Javadoc
/**
 * The Class FlickrOAuth.
 */
public class FlickrOAuth {

    /** The app key. */
    private final String appKey;
    /** The shared secret. */
    private final String sharedSecret;
    /** The fl. */
    private final Flickr fl;
    /** The r token. */
    private OAuthToken rToken;
    /** The a token. */
    private OAuth aToken;
    /** The verifier. */
    private String verifier;
    /* The callback-URL. oob = out-of-band */
    /** The callback url. */
    private final String callbackURL = "oob";
    /** The flickr permission. */
    private final Permission FLICKR_PERMISSION = Permission.DELETE;
    /** The path to flickr properties file */
    private final String FLICKR_PROPS_URI = "src/main/resources/flickr.properties";
    /** The flickr properties */
    private final Properties fp = new Properties();

    public FlickrOAuth() {
        loadFlickrProperties();
        appKey = fp.getProperty("appKey");
        sharedSecret = fp.getProperty("sharedSecret");
        fl = new Flickr(appKey, sharedSecret);
    }

    /**
     * Gets the flickr.
     * 
     * @return the flickr
     */
    public Flickr getFlickr() {

        try {
            return authenticateAppAndGetFlickr();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FlickrException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Authenticate app and get flickr.
     * 
     * @return the flickr
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws FlickrException
     *             the flickr exception
     */
    private Flickr authenticateAppAndGetFlickr() throws IOException, FlickrException {

        if (!fp.containsKey("token")) {
            rToken = fl.getOAuthInterface().getRequestToken(callbackURL);
            System.out.println(generateAuthenticationURL());
            readInVerifier();

            aToken = getAccessToken();
            saveOAuthToPropertiesFile();
        } else {
            aToken = loadOAuthFromPropertiesFile();
        }

        return fl;
    }

    private void loadFlickrProperties() {
        try {
            fp.load(new FileInputStream(new File(FLICKR_PROPS_URI)));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Load o auth from file.
     * 
     * @return the o auth
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private OAuth loadOAuthFromPropertiesFile() throws IOException {

        final String token = fp.getProperty("token");
        final String tokenSecret = fp.getProperty("tokenSecret");
        final String userId = fp.getProperty("userId");
        final String username = fp.getProperty("username");

        User usr = new User();
        usr.setId(userId);
        usr.setUsername(username);
        OAuthToken tok = new OAuthToken(token, tokenSecret);

        OAuth oa = new OAuth();
        oa.setToken(tok);
        oa.setUser(usr);
        RequestContext.getRequestContext().setOAuth(oa);

        return oa;
    }

    /**
     * Saves OAuth information in flickr properties file.
     * 
     * @throws IOException
     */
    private void saveOAuthToPropertiesFile() {

        final String token = aToken.getToken().getOauthToken();
        final String tokenSecret = aToken.getToken().getOauthTokenSecret();
        final String userId = aToken.getUser().getId();
        final String username = aToken.getUser().getUsername();

        fp.setProperty("token", token);
        fp.setProperty("tokenSecret", tokenSecret);
        fp.setProperty("userId", userId);
        fp.setProperty("username", username);

        try {
            fp.store(new FileOutputStream(new File(FLICKR_PROPS_URI)), "Flickr Properties");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Gets the user.
     * 
     * @return the user
     */
    public User getUser() {
        return aToken.getUser();
    }

    /**
     * Gets the token.
     * 
     * @return the token
     */
    OAuthToken getToken() {
        return aToken.getToken();
    }

    /**
     * Generate authentication url.
     * 
     * @return the url
     * @throws MalformedURLException
     *             the malformed url exception
     */
    private URL generateAuthenticationURL() throws MalformedURLException {
        return fl.getOAuthInterface().buildAuthenticationUrl(FLICKR_PERMISSION, rToken);
    }

    /**
     * Read in verifier.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void readInVerifier() throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        System.out.println("Please enter your flickr authorization code:");
        verifier = br.readLine();
    }

    /**
     * Gets the access token.
     * 
     * @return the access token
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws FlickrException
     *             the flickr exception
     */
    private OAuth getAccessToken() throws IOException, FlickrException {
        return fl.getOAuthInterface().getAccessToken(rToken.getOauthToken(), rToken.getOauthTokenSecret(),
            verifier);
    }

}
