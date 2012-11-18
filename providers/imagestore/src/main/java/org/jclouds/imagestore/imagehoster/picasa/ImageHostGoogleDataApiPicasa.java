/**
 * 
 */
package org.jclouds.imagestore.imagehoster.picasa;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.imageio.ImageIO;

import org.jclouds.imagestore.imagehoster.IImageHost;

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
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.media.MediaByteArraySource;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.photos.UserFeed;
import com.google.gdata.util.ServiceException;

/**
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class ImageHostGoogleDataApiPicasa implements IImageHost {

    private final static String PICASA_USER = "jclouds.imagestore.picasauser";

    private final static String PICASA_PASSWORD = "jclouds.imagestore.picasapass";

    /** The maximum image width. */
    private static final int MAX_IMAGE_WIDTH = 2048;
    /** The maximum image height. */
    private static final int MAX_IMAGE_HEIGHT = 2048;

    private static String ROOTURL = "https://picasaweb.google.com/data/feed/api/user/default";

    private final PicasawebService service;

    public ImageHostGoogleDataApiPicasa() {
        try {

            service = new PicasawebService("imageuploader");
            String[] user = getUserCredentials();
            if (user.length > 0) {
                service.setUserCredentials(user[0], user[1]);
            } else {
                Credential credential = authorize();
                credential.getRefreshToken();
                service.setOAuth2Credentials(credential);
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
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
        // hack to try it multiple times in a row
        Exception thrown = null;
        int i = 10;
        do {
            i--;
            try {
                if (getAlbumByName(imageSetTitle) == null) {
                    AlbumEntry myAlbum = new AlbumEntry();
                    myAlbum.setTitle(new PlainTextConstruct(imageSetTitle));
                    service.insert(new URL(ROOTURL), myAlbum);
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException exc) {
                thrown = exc;
            } catch (IOException exc) {
                thrown = exc;
            } catch (ServiceException exc) {
                thrown = exc;
            }
        } while (i >= 0);
        throw new RuntimeException(thrown);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean imageExists(String imageSetTitle, String imageTitle) {
        return getPhotoByNameNormal(imageSetTitle, imageTitle) != null;

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
    public boolean deleteImage(String imageSetTitle, String imageTitle) {
        // hack to try it multiple times in a row
        Exception thrown = null;
        int i = 10;
        do {
            i--;
            PhotoEntry entry = getPhotoByNameNormal(imageSetTitle, imageTitle);
            if (entry != null) {
                try {
                    entry.delete();
                    return true;
                } catch (IOException exc) {
                    thrown = exc;
                } catch (ServiceException exc) {
                    thrown = exc;
                }
            } else {
                return false;
            }
        } while (i >= 0);
        throw new RuntimeException(thrown);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteImageSet(String imageSetTitle) {
        // hack to try it multiple times in a row
        Exception thrown = null;
        int i = 10;
        do {
            i--;
            AlbumEntry entry = getAlbumByName(imageSetTitle);
            if (entry != null) {
                try {
                    entry.delete();
                    return true;
                } catch (IOException exc) {
                    thrown = exc;
                } catch (ServiceException exc) {
                    thrown = exc;
                }
            } else {
                return false;
            }
        } while (i >= 0);
        throw new RuntimeException(thrown);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean uploadImage(String imageSetTitle, String imageTitle, BufferedImage image) {
        AlbumEntry entry = getAlbumByName(imageSetTitle);
        if (entry == null) {
            createImageSet(imageSetTitle);
            entry = getAlbumByName(imageSetTitle);
        }
        if (imageExists(imageSetTitle, imageTitle)) {
            return false;
        }

        // hack to try it multiple times in a row
        Exception thrown = null;
        int i = 10;
        do {
            i--;
            PhotoEntry myPhoto = new PhotoEntry();
            myPhoto.setTitle(new PlainTextConstruct(imageTitle));
            try {
                URL toPost = new URL(entry.getFeedLink().getHref());

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(image, "png", os);

                MediaByteArraySource myMedia = new MediaByteArraySource(os.toByteArray(), "image/png");
                myPhoto.setMediaSource(myMedia);

                service.insert(toPost, myPhoto);
                return true;
            } catch (IOException exc) {
                thrown = exc;
            } catch (ServiceException exc) {
                thrown = exc;
            }
        } while (i >= 0);
        throw new RuntimeException(thrown);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage downloadImage(String imageSetTitle, String imageTitle) {
        // hack to try it multiple times in a row
        int i = 10;
        Exception thrown = null;
        do {
            i--;
            PhotoEntry myPhoto = getPhotoByNameForDownload(imageSetTitle, imageTitle);
            try {
                if (myPhoto == null) {
                    return null;
                }
                String source = myPhoto.getMediaContents().get(0).getUrl();
                return ImageIO.read(new URL(source));
            } catch (IOException exc) {
                thrown = new RuntimeException(exc);
            }
        } while (thrown != null && i >= 0);
        throw new RuntimeException(thrown);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> imageSetContent(String imageSetTitle) {

        // hack to try it multiple times in a row
        Exception thrown = null;
        int i = 10;

        AlbumEntry entry = getAlbumByName(imageSetTitle);
        do {
            i--;
            try {
                Set<String> returnVal = new HashSet<String>();
                if (entry != null) {
                    AlbumFeed feed = entry.getFeed("photo");
                    for (PhotoEntry photo : feed.getPhotoEntries()) {
                        returnVal.add(photo.getTitle().getPlainText());
                    }
                }
                return returnVal;
            } catch (IOException exc) {
                thrown = exc;
            } catch (ServiceException exc) {
                thrown = exc;
            }
        } while (i >= 10);
        throw new RuntimeException(thrown);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clearImageSet(String imageSetTitle) {
        if (deleteImageSet(imageSetTitle)) {
            return createImageSet(imageSetTitle);
        }
        return false;
    }

    private final PhotoEntry getPhotoByNameNormal(String albumTitle, String imageTitle) {
        AlbumEntry entry = getAlbumByName(albumTitle);
        if (entry != null) {
            // hack to try it multiple times in a row
            int i = 10;
            Exception thrown = null;
            do {
                i--;
                try {
                    AlbumFeed feed = entry.getFeed("photo");
                    for (PhotoEntry photo : feed.getPhotoEntries()) {
                        if (photo.getTitle().getPlainText().equals(imageTitle)) {
                            return photo;
                        }
                    }
                    return null;
                } catch (IOException exc) {
                    thrown = exc;
                } catch (ServiceException exc) {
                    thrown = exc;
                }
            } while (i >= 0);
            throw new RuntimeException(thrown);
        } else {
            return null;
        }

    }

    private final PhotoEntry getPhotoByNameForDownload(String albumTitle, String imageTitle) {
        AlbumEntry entry = getAlbumByName(albumTitle);
        if (entry != null) {
            // hack to try it multiple times in a row
            int i = 10;
            Exception thrown = null;
            do {
                i--;
                try {
                    URL url = new URL(ROOTURL + "/albumid/" + entry.getGphotoId() + "?imgmax=d");
                    AlbumFeed feed = service.getFeed(url, AlbumFeed.class);
                    for (PhotoEntry photo : feed.getPhotoEntries()) {
                        if (photo.getTitle().getPlainText().equals(imageTitle)) {
                            return photo;
                        }
                    }
                    return null;
                } catch (IOException exc) {
                    thrown = exc;
                } catch (ServiceException exc) {
                    thrown = exc;
                }
            } while (i >= 0);
            throw new RuntimeException(thrown);
        } else {
            return null;
        }

    }

    private final AlbumEntry getAlbumByName(String albumTitle) {
        // hack to try it multiple times in a row
        int i = 10;
        Exception thrown = null;
        do {
            i--;
            try {
                URL feedUrl = new URL(ROOTURL + "?kind=album");
                UserFeed myUserFeed = service.getFeed(feedUrl, UserFeed.class);

                for (AlbumEntry myAlbum : myUserFeed.getAlbumEntries()) {
                    if (myAlbum.getTitle().getPlainText().equals(albumTitle)) {
                        return myAlbum;
                    }
                }
                return null;
            } catch (MalformedURLException exc) {
                thrown = exc;
            } catch (IOException exc) {
                thrown = exc;
            } catch (ServiceException exc) {
                thrown = exc;
            }
        } while (i >= 0);
        throw new RuntimeException(thrown);
    }

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Authorizes the installed application to access user's protected data. */
    private static Credential authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, ImageHostGoogleDataApiPicasa.class
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
                .singleton("https://picasaweb.google.com/data/")).setCredentialStore(credentialStore).build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    private static String[] getUserCredentials() {
        File userStore =
            new File(System.getProperty("user.home"), new StringBuilder(".imagecredentials").append(
                File.separator).append("picasa.properties").toString());
        if (!userStore.exists()) {
            return new String[0];
        } else {
            Properties props = new Properties();
            try {
                props.load(new FileReader(userStore));
                return new String[] {
                    props.getProperty(PICASA_USER), props.getProperty(PICASA_PASSWORD)
                };

            } catch (IOException exc) {
                throw new RuntimeException(exc);
            }
        }

    }
}
