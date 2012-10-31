package org.jclouds.imagestore;

import org.jclouds.imagestore.imagehoster.facebook.FacebookOAuth;
import org.jclouds.imagestore.imagehoster.flickr.FlickrOAuth;

import com.googlecode.flickrjandroid.auth.Permission;

/**
 * This Class offers constants for the image store providers.
 * 
 * @author Sebastian Graf, Wolfgang Miller
 * 
 */
public final class ImageStoreConstants {

    /**
     * Private constructor. Protects helper class from being instantiated.
     */
    private ImageStoreConstants() {
    };

    /** Fully qualified name of imagehoster-class. */
    public static final String PROPERTY_IMAGEHOSTER = "jclouds.imagestore.imagehoster";
    /** Fully qualified name of imagepainter. */
    public static final String PROPERTY_BYTEPAINTER = "jclouds.imagestore.imagegenerator";
    /** Fully qualified name of encoder. */
    public static final String PROPERTY_ENCODER = "jclouds.imagestore.encoder";

    /**
     * Image host Flickr.
     */

    /** The flickr app key. */
    public static final String FLICKR_APP_KEY = "3e6f5174edc3744e57c496db5d780ee8";
    /** The flickr shared secrect. */
    public static final String FLICKR_SHARED_SECRET = "a23933fe38c54919";

    /** The callback-URL. oob = out-of-band. */
    public static final String FLICKR_CALLBACK_URL = "oob";
    /** The flickr permission. */
    public static final Permission FLICKR_PERMISSION = Permission.DELETE;
    /** The flickr properties file name. */
    public static final String FLICKR_PROP_FILE_NAME = "flickr.properties";
    /** The path to Flickr properties file. */
    public static final String FLICKR_PROP_FILE_URI = FlickrOAuth.class.getResource(".").getPath()
        + "../../../../../" + ImageStoreConstants.FLICKR_PROP_FILE_NAME;

    // Properties Keys

    /** The token properties key. */
    public static final String FLICKR_PROPKEY_TOKEN = "token";
    /** The token secret properties key. */
    public static final String FLICKR_PROPKEY_TOKEN_SECRET = "tokenSecret";
    /** The user id properties key. */
    public static final String FLICKR_PROPKEY_USER_ID = "userId";
    /** The user name properties key. */
    public static final String FLICKR_PROPKEY_USERNAME = "username";

    /**
     * Image host Facebook.
     */

    /** The Facebook application-id. */
    public static final String FACEBOOK_APP_ID = "262726663843525";
    /** The Facebook application-secret. */
    public static final String FACEBOOK_APP_SECRET = "decc85c7e0785336f8c4c115d626294d";

    /** The Facebook URL-parameter client-id. */
    public static final String FACEBOOK_URL_PARAM_CLIENT_ID = "client_id";
    /** The Facebook URL-parameter client-secret. */
    public static final String FACEBOOK_URL_PARAM_CLIENT_SECRET = "client_secret";
    /** The Facebook URL-parameter code. */
    public static final String FACEBOOK_URL_PARAM_CODE = "code";
    /** The Facebook URL-parameter scope. */
    public static final String FACEBOOK_URL_PARAM_SCOPE = "scope";
    /** The Facebook URL-parameter redirect-URI. */
    public static final String FACEBOOK_URL_PARAM_REDIRECT_URI = "redirect_uri";
    /** The Facebook URL-parameter grant_type. */
    public static final String FACEBOOK_URL_PARAM_GRANT_TYPE = "grant_type";
    /** The Facebook URL-parameter fb_exchange_token. */
    public static final String FACEBOOK_URL_PARAM_FB_EXCHANGE_TOKEN = "fb_exchange_token";

    /** The Facebook authentication-URL. */
    public static final String FACEBOOK_USER_AUTH_URL = "https://graph.facebook.com/oauth/authorize";
    /** The Facebook authentication-scope. */
    public static final String FACEBOOK_USER_AUTH_SCOPE = "user_photos,publish_stream";
    /** The Facebook callback-URL. */
    public static final String FACEBOOK_CALLBACK_URL = "https://www.facebook.com/connect/login_success.html";
    /** The Facebook token-URL. */
    public static final String FACEBOOK_TOKEN_URL = "https://graph.facebook.com/oauth/access_token";
    /** The Facebook URL to users profile. */
    public static final String FACEBOOK_USER_URL = "https://graph.facebook.com/me";

    /** The Facebook properties file name. */
    public static final String FACEBOOK_PROP_FILE_NAME = "facebook.properties";
    /** The path to Facebook properties file. */
    public static final String FACEBOOK_PROP_FILE_URI = FacebookOAuth.class.getResource(".").getPath()
        + "../../../../../" + ImageStoreConstants.FACEBOOK_PROP_FILE_NAME;
    /** The Facebook access token properties key. */
    public static final String FACEBOOK_PROPKEY_TOKEN = "access_token";

}
