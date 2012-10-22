/**
 * 
 */
package org.jclouds.imagestore.imagehoster.picasa;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.imagestore.imagehoster.picasa.model.AlbumEntry;
import org.jclouds.imagestore.imagehoster.picasa.model.UserFeed;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class ImageHostPicasa implements IImageHost {

    /** The maximum image width. */
    private static final int MAX_IMAGE_WIDTH = 2048;
    /** The maximum image height. */
    private static final int MAX_IMAGE_HEIGHT = 2048;

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private PicasaClient client;

    public ImageHostPicasa() {
        try {
            Credential credential = authorize();
            client = new PicasaClient(HTTP_TRANSPORT.createRequestFactory(credential));
            client.setApplicationName("JClouds-ImageHoster");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxImageWidth() {
        return MAX_IMAGE_WIDTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxImageHeight() {
        return MAX_IMAGE_HEIGHT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createImageSet(String imageSetTitle) {
        try {
            // build URL for the default user feed of albums
            PicasaUrl url = PicasaUrl.relativeToRoot("feed/api/user/default");
            // execute GData request for the feed
            UserFeed feed = client.executeGetUserFeed(url);
            AlbumEntry newAlbum = new AlbumEntry();
            newAlbum.access = "public";
            newAlbum.title = imageSetTitle;
            newAlbum.summary = imageSetTitle;
            client.executeInsert(feed, newAlbum);
        } catch (final IOException exc) {
            throw new RuntimeException(exc);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean imageExists(String imageSetTitle, String imageTitle) {

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean imageSetExists(String imageSetTitle) {
        
        
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteImage(String imageSetTitle, String imageTitle) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteImageSet(String imageSetTitle) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String uploadImage(String imageSetTitle, String imageTitle, BufferedImage image) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage downloadImage(String imageSetTitle, String imageTitle) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countImagesInSet(String imageSetTitle) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearImageSet(String imageSetTitle) {
        // TODO Auto-generated method stub

    }

    /** Authorizes the installed application to access user's protected data. */
    private static Credential authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, PicasaSample.class
                .getResourceAsStream("/client_secrets.json"));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
            || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/ "
                + "into resources/client_secrets.json");
            System.exit(1);
        }
        // set up file credential store
        FileCredentialStore credentialStore =
            new FileCredentialStore(new File(System.getProperty("user.home"), ".credentials/picasa.json"),
                JSON_FACTORY);
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow =
            new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections
                .singleton(PicasaUrl.ROOT_URL)).setCredentialStore(credentialStore).build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

}
