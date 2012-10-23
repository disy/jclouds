/**
 * 
 */
package org.jclouds.imagestore.imagehoster.picasa;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import javax.imageio.ImageIO;

import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.imagestore.imagehoster.picasa.model.AlbumEntry;
import org.jclouds.imagestore.imagehoster.picasa.model.AlbumFeed;
import org.jclouds.imagestore.imagehoster.picasa.model.MediaContent;
import org.jclouds.imagestore.imagehoster.picasa.model.PhotoEntry;
import org.jclouds.imagestore.imagehoster.picasa.model.UserFeed;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
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
            if (!imageSetExists(imageSetTitle)) {
                // build URL for the default user feed of albums
                PicasaUrl url = PicasaUrl.relativeToRoot("feed/api/user/default");
                // execute GData request for the feed
                UserFeed feed = client.executeGetUserFeed(url);
                AlbumEntry newAlbum = new AlbumEntry();
                newAlbum.access = "public";
                newAlbum.title = imageSetTitle;
                client.executeInsert(new PicasaUrl(feed.getPostLink()), newAlbum);
                return true;
            } else {
                return false;
            }

        } catch (final IOException exc) {
            throw new RuntimeException(exc);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean imageExists(String imageSetTitle, String imageTitle) {
        return getPhotoByName(imageSetTitle, imageTitle) != null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean imageSetExists(String imageSetTitle) {
        return getAlbumByName(imageSetTitle) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteImage(String imageSetTitle, String imageTitle) {
        PhotoEntry entry = getPhotoByName(imageSetTitle, imageTitle);
        try {
            client.executeGetPhoto(new PicasaUrl(entry.getSelfLink()));
            client.executeDelete(entry);
        } catch (final IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteImageSet(String imageSetTitle) {
        try {
            AlbumEntry entry = getAlbumByName(imageSetTitle);
            client.executeGetAlbum(new PicasaUrl(entry.getSelfLink()));
            client.executeDelete(entry);
        } catch (final IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearImageSet(String imageSetTitle) {
        deleteImageSet(imageSetTitle);
        createImageSet(imageSetTitle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String uploadImage(String imageSetTitle, String imageTitle, BufferedImage image) {
        AlbumEntry album = getAlbumByName(imageSetTitle);
        if (album == null) {
            createImageSet(imageSetTitle);
            album = getAlbumByName(imageSetTitle);
        }
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            InputStreamContent content = new InputStreamContent("image/jpeg", is);
            PhotoEntry photo =
                client.executeInsertPhotoEntry(new PicasaUrl(album.getFeedLink()), content, imageTitle);
            is.close();
            return photo.title;
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage downloadImage(String imageSetTitle, String imageTitle) {
        PhotoEntry entry = getPhotoByName(imageSetTitle, imageTitle);
        MediaContent mediaContent = entry.mediaGroup.content;
        try {
            return ImageIO.read(new URL(mediaContent.url));
        } catch (MalformedURLException exc) {
            throw new RuntimeException(exc);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countImagesInSet(String imageSetTitle) {
        AlbumEntry album = getAlbumByName(imageSetTitle);
        if (album != null) {
            return album.numPhotos;
        } else {
            return 0;
        }
    }

    private PhotoEntry getPhotoByName(String imageSetTitle, String imageTitle) {
        try {
            AlbumEntry searchedSet = getAlbumByName(imageSetTitle);
            PhotoEntry returnVal = null;
            if (searchedSet != null) {
                PicasaUrl url = new PicasaUrl(searchedSet.getFeedLink());
                AlbumFeed albumFeed = client.executeGetAlbumFeed(url);
                for (PhotoEntry photo : albumFeed.photos) {
                    if (photo.title.equals(imageSetTitle)) {
                        returnVal = photo;
                        break;
                    }
                }
            }
            return returnVal;
        } catch (final IOException exc) {
            throw new RuntimeException(exc);
        }

    }

    private AlbumEntry getAlbumByName(String imageSetTitle) {
        try {
            // build URL for the default user feed of albums
            PicasaUrl url = PicasaUrl.relativeToRoot("feed/api/user/default");
            // execute GData request for the feed
            UserFeed feed = client.executeGetUserFeed(url);
            AlbumEntry searchedSet = null;
            for (AlbumEntry entry : feed.albums) {
                if (entry.title.equals(imageSetTitle)) {
                    searchedSet = entry;
                    break;
                }
            }
            return searchedSet;
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
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
