/**
 * 
 */
package org.jclouds.imagestore.imagehoster.picasa;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

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

    /** The maximum image width. */
    private static final int MAX_IMAGE_WIDTH = 2048;
    /** The maximum image height. */
    private static final int MAX_IMAGE_HEIGHT = 2048;

    private static String ROOTURL = "https://picasaweb.google.com/data/feed/api/user/default";

    private final PicasawebService service;

    private final Credential credential;

    public ImageHostGoogleDataApiPicasa() {
        try {
            credential = authorize();
            service = new PicasawebService("imageuploader");
            service.setOAuth2Credentials(credential);
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
            throw new RuntimeException(exc);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        } catch (ServiceException exc) {
            throw new RuntimeException(exc);
        }
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
    public void deleteImage(String imageSetTitle, String imageTitle) {
        PhotoEntry entry = getPhotoByNameNormal(imageSetTitle, imageTitle);
        if (entry != null) {
            try {
                entry.delete();
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            } catch (ServiceException exc) {
                throw new RuntimeException(exc);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteImageSet(String imageSetTitle) {
        AlbumEntry entry = getAlbumByName(imageSetTitle);
        if (entry != null) {
            try {
                entry.delete();
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            } catch (ServiceException exc) {
                throw new RuntimeException(exc);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String uploadImage(String imageSetTitle, String imageTitle, BufferedImage image) {
        AlbumEntry entry = getAlbumByName(imageSetTitle);
        if (entry == null) {
            createImageSet(imageSetTitle);
            entry = getAlbumByName(imageSetTitle);
        }
        PhotoEntry myPhoto = new PhotoEntry();
        myPhoto.setTitle(new PlainTextConstruct(imageTitle));
        try {
            URL toPost = new URL(entry.getFeedLink().getHref());

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);

            MediaByteArraySource myMedia = new MediaByteArraySource(os.toByteArray(), "image/png");
            myPhoto.setMediaSource(myMedia);

            service.insert(toPost, myPhoto);
            return imageTitle;
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        } catch (ServiceException exc) {
            throw new RuntimeException(exc);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage downloadImage(String imageSetTitle, String imageTitle) {
        PhotoEntry myPhoto = getPhotoByNameForDownload(imageSetTitle, imageTitle);
        try {
            String source = myPhoto.getMediaContents().get(0).getUrl();
            return ImageIO.read(new URL(source));
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countImagesInSet(String imageSetTitle) {
        AlbumEntry entry = getAlbumByName(imageSetTitle);
        if (entry != null) {
            return entry.getPhotosUsed();
        } else {
            return 0;
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

    private final PhotoEntry getPhotoByNameNormal(String albumTitle, String imageTitle) {
        AlbumEntry entry = getAlbumByName(albumTitle);
        if (entry != null) {
            try {
                AlbumFeed feed = entry.getFeed("photo");
                for (PhotoEntry photo : feed.getPhotoEntries()) {
                    if (photo.getTitle().getPlainText().equals(imageTitle)) {
                        return photo;
                    }
                }
                return null;
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            } catch (ServiceException exc) {
                throw new RuntimeException(exc);
            }
        } else {
            return null;
        }

    }

    private final PhotoEntry getPhotoByNameForDownload(String albumTitle, String imageTitle) {
        AlbumEntry entry = getAlbumByName(albumTitle);
        if (entry != null) {
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
                throw new RuntimeException(exc);
            } catch (ServiceException exc) {
                throw new RuntimeException(exc);
            }
        } else {
            return null;
        }

    }

    private final AlbumEntry getAlbumByName(String albumTitle) {
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
            throw new RuntimeException(exc);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        } catch (ServiceException exc) {
            throw new RuntimeException(exc);
        }

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

}
