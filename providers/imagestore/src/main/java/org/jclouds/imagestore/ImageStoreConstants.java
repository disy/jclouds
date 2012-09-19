package org.jclouds.imagestore;

import org.jclouds.imagestore.imagehoster.flickr.FlickrOAuth;

import com.googlecode.flickrjandroid.auth.Permission;

public class ImageStoreConstants {

    // Fully qualified name of imagehoster-class
    public static final String PROPERTY_IMAGEHOSTER = "jclouds.imagestore.imagehoster";
    // Fully qualified name of imagepainter
    public static final String PROPERTY_BYTEPAINTER = "jclouds.imagestore.imagegenerator";


    /** The callback-URL. oob = out-of-band. */
    public static final String CALLBACK_URL = "oob";
    /** The flickr permission. */
    public static final Permission FLICKR_PERMISSION = Permission.DELETE;
    /** The flickr properties file name. */
    public static final String FLICKR_PROP_FILE_NAME = "flickr.properties";
    /** The path to Flickr properties file. */
    public static final String FLICKR_PROP_FILE_URI = FlickrOAuth.class.getResource(".").getPath()
        + "../../../../../" + ImageStoreConstants.FLICKR_PROP_FILE_NAME;

    // Properties Keys

    /** The token properties key. */
    public static final String PROPKEY_TOKEN = "token";
    /** The token secret properties key. */
    public static final String PROPKEY_TOKEN_SECRET = "tokenSecret";
    /** The user id properties key. */
    public static final String PROPKEY_USER_ID = "userId";
    /** The user name properties key. */
    public static final String PROPKEY_USERNAME = "username";

}
