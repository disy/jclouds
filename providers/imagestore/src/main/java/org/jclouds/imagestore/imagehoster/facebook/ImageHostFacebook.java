package org.jclouds.imagestore.imagehoster.facebook;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

import org.jclouds.imagestore.imagehoster.HImageHostHelper;
import org.jclouds.imagestore.imagehoster.IImageHost;

import com.restfb.BinaryAttachment;
import com.restfb.Facebook;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Album;
import com.restfb.types.FacebookType;

public class ImageHostFacebook implements IImageHost {

    /** The maximum image width. */
    private static final int MAX_IMAGE_WIDTH = 720;
    /** The maximum image height. */
    private static final int MAX_IMAGE_HEIGHT = 720;
    /** The FacebookClient instance. */
    private FacebookClient fbClient;

    /**
     * The ImageHostFacebook constructor.
     */
    public ImageHostFacebook() {
        try {
            fbClient = FacebookOAuth.getNewFacebookClient();
        } catch (IOException e) {
            new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            new RuntimeException(e);
        }
    }

    private static class FqlAlbum {
        /** The album id. */
        @Facebook
        private String object_id;

        /** The photo count. */
        @Facebook
        private int photo_count;

        /**
         * Returns the album id.
         * 
         * @return The album id
         */
        String getAlbumId() {
            return object_id;
        }

        /**
         * Returns the photo count.
         * 
         * @return The photo count
         */
        int getPhotoCount() {
            return photo_count;
        }
    }

    private static class FqlPhoto {
        /** The photo id. */
        @Facebook
        private String object_id;

        /** The big photo URL. */
        @Facebook
        private String src_big;

        /**
         * Returns the big photo URL.
         * 
         * @return The photo URL
         * @throws MalformedURLException
         *             Signals that an MalformedURLException has occurred
         */
        URL getBigPhotoURL() throws MalformedURLException {
            return new URL(src_big);
        }

        /**
         * Returns the photo id.
         * 
         * @return The photo id
         */
        String getPhotoId() {
            return object_id;
        }
    }

    @Override
    public int getMaxImageWidth() {
        return MAX_IMAGE_WIDTH;
    }

    @Override
    public int getMaxImageHeight() {
        return MAX_IMAGE_HEIGHT;
    }

    /**
     * Searches for the image-set with given name and returns it.
     * 
     * @param imageSetTitle
     *            the set-title
     * @return the facebook album
     */
    private FqlAlbum getFacebookImageSetFql(final String imageSetTitle) {
        final String query =
            "SELECT object_id, photo_count FROM album WHERE name = \"" + imageSetTitle
                + "\" AND owner = me()";

        final List<FqlAlbum> albums = fbClient.executeQuery(query, FqlAlbum.class);

        if (albums.size() > 0) {
            return albums.get(0);
        }

        return null;
    }

    /**
     * Returns an FqlPhoto object for the given parameters.
     * 
     * @param imageSetId
     *            The set id
     * @param imageTitle
     *            The image title
     * @return The FqlPhoto object
     */
    private FqlPhoto getFacebookImageFql(final String imageSetId, final String imageTitle) {
        final String query =
            "SELECT object_id, src_big FROM photo WHERE album_object_id = \"" + imageSetId
                + "\" AND caption = \"" + imageTitle + "\" AND owner = me()";

        final List<FqlPhoto> photos = fbClient.executeQuery(query, FqlPhoto.class);

        if (photos.size() > 0) {
            return photos.get(0);
        }

        return null;
    }

    /**
     * Returns the set id from given set title.
     * 
     * @param imageSetTitle
     *            The set title
     * @return The album id
     */
    private String getFacebookImageSetId(final String imageSetTitle) {
        final FqlAlbum fAlbum = getFacebookImageSetFql(imageSetTitle);
        return fAlbum == null ? "" : fAlbum.getAlbumId();
    }

    /**
     * Returns the image id from given set id and image title.
     * 
     * @param imageSetId
     *            The set id
     * @param imageTitle
     *            The set title
     * @return The image id
     */
    private String getFacebookImageId(final String imageSetId, final String imageTitle) {
        final FqlPhoto fPh = getFacebookImageFql(imageSetId, imageTitle);
        return fPh == null ? "" : fPh.getPhotoId();
    }

    @Override
    public boolean createImageSet(final String imageSetTitle) {
        if (imageSetExists(imageSetTitle))
            return false;

        fbClient.publish("me/albums", Album.class, Parameter.with("name", imageSetTitle));
        return true;
    }

    @Override
    public boolean imageExists(final String imageSetTitle, final String imageTitle) {
        final String imageSetId = getFacebookImageSetId(imageSetTitle);

        if (imageSetId.isEmpty()) {
            throwAlbumNotFoundException(imageSetTitle);
        }

        return !getFacebookImageId(imageSetId, imageTitle).isEmpty();
    }

    @Override
    public boolean imageSetExists(final String imageSetTitle) {
        return !getFacebookImageSetId(imageSetTitle).isEmpty();
    }

    @Override
    public void deleteImage(final String imageSetTitle, final String imageTitle) {
        final String imageSetId = getFacebookImageSetId(imageSetTitle);

        if (imageSetId.isEmpty()) {
            throwAlbumNotFoundException(imageSetTitle);
        }

        final String imageId = getFacebookImageId(imageSetId, imageTitle);

        if (imageId.isEmpty()) {
            throwPhotoNotFoundException(imageSetTitle, imageTitle);
        }

        fbClient.deleteObject(imageId);
    }

    @Override
    public void deleteImageSet(final String imageSetTitle) {
        final String imageSetId = getFacebookImageSetId(imageSetTitle);
        if (imageSetId.isEmpty()) {
            throwAlbumNotFoundException(imageSetTitle);
        }

        // fbClient.deleteObject(imageSetId);
    }

    @Override
    public String uploadImage(final String imageSetTitle, final String imageTitle, final BufferedImage image) {
        String imageSetId = getFacebookImageSetId(imageSetTitle);
        if (imageSetId.isEmpty()) {
            imageSetId =
                fbClient.publish("me/albums", Album.class, Parameter.with("name", imageSetTitle)).getId();
        }

        // upload image to facebook
        try {
            return fbClient.publish(imageSetId + "/photos", FacebookType.class,
                BinaryAttachment.with(imageTitle + ".png", HImageHostHelper.getInputStreamFromImage(image)),
                Parameter.with("name", imageTitle)).getId();
        } catch (IOException e) {
            new RuntimeException(e);
        }
        return "";
    }

    @Override
    public String uploadImage(final String imageTitle, final BufferedImage image) {
        // "me" is the standard container on facebook
        return uploadImage("me", imageTitle, image);
    }

    @Override
    public BufferedImage downloadImage(final String imageSetTitle, final String imageTitle) {
        final String imageSetId = getFacebookImageSetId(imageSetTitle);

        if (imageSetId.isEmpty()) {
            throwAlbumNotFoundException(imageSetTitle);
        }

        final FqlPhoto fPh = getFacebookImageFql(imageSetId, imageTitle);

        if (fPh == null) {
            throwPhotoNotFoundException(imageSetTitle, imageTitle);
        }

        try {
            return ImageIO.read(fPh.getBigPhotoURL());
        } catch (MalformedURLException e) {
            new RuntimeException(e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            new RuntimeException(e);
        }
        return null;
    }

    @Override
    public int countImagesInSet(final String imageSetTitle) {
        FqlAlbum fAlbum = getFacebookImageSetFql(imageSetTitle);

        if (fAlbum == null) {
            throwAlbumNotFoundException(imageSetTitle);
        }

        return fAlbum.getPhotoCount();
    }

    @Override
    public void clearImageSet(final String imageSetTitle) {
        final String imageSetId = getFacebookImageSetId(imageSetTitle);
        if (imageSetId.isEmpty())
            return;

        final String query =
            "SELECT object_id FROM photo WHERE album_object_id = \"" + imageSetId + "\" AND owner = me()";
        final List<FqlPhoto> photos = fbClient.executeQuery(query, FqlPhoto.class);

        for (FqlPhoto ph : photos) {
            fbClient.deleteObject(ph.getPhotoId());
        }
    }

    /**
     * Throws an exception if an album with given title does not exist.
     * 
     * @param imageSetTitle
     *            The title
     */
    private void throwAlbumNotFoundException(final String imageSetTitle) {
        try {
            throw new IllegalArgumentException("There is no facebook album with given title: \""
                + imageSetTitle + "\"!");
        } catch (IllegalArgumentException e) {
            new RuntimeException(e);
        }
    }

    /**
     * Throws an exception if no photo with the given title in the given set exists.
     * 
     * @param imageSetTitle
     *            The set title
     * @param imageTitle
     *            The image title
     */
    private void throwPhotoNotFoundException(final String imageSetTitle, final String imageTitle) {
        try {
            throw new IllegalArgumentException("There is no image named \"" + imageTitle
                + "\" in your facebook album \"" + imageSetTitle + "\"!");
        } catch (IllegalArgumentException e) {
            new RuntimeException(e);
        }
    }
}
