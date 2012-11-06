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

    /** Dummy Picture for marking deleted albums. */
    private static final BufferedImage DUMMYIMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
    /** Deleted set name. */
    private static final String MARKERFORSET = "MARKER";

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
        String object_id;

        /** The photo count. */
        @Facebook
        int photo_count;

    }

    private static class FqlPhoto {

        /** Caption of picture. */
        @Facebook
        String caption;

        /** The photo id. */
        @Facebook
        String object_id;

        /** The big photo URL. */
        @Facebook
        String src_big;
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
    private List<FqlPhoto> getFacebookImageFql(final String imageSetId, final String imageTitle) {
        final StringBuilder builder =
            new StringBuilder("SELECT object_id, src_big, caption FROM photo WHERE album_object_id = \"");
        builder.append(imageSetId);
        if (!imageTitle.isEmpty()) {
            builder.append("\" AND caption = \"");
            builder.append(imageTitle);
        }
        builder.append("\" AND owner = me()");

        return fbClient.executeQuery(builder.toString(), FqlPhoto.class);
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
        return fAlbum == null ? "" : fAlbum.object_id;
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
        final List<FqlPhoto> fPhs = getFacebookImageFql(imageSetId, imageTitle);
        return fPhs.isEmpty() ? "" : fPhs.get(0).object_id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createImageSet(final String imageSetTitle) {
        try {
            FqlAlbum album = getFacebookImageSetFql(imageSetTitle);
            if (album == null) {
                fbClient.publish("me/albums", Album.class, Parameter.with("name", imageSetTitle));
                album = getFacebookImageSetFql(imageSetTitle);
                fbClient.publish(album.object_id + "/photos", FacebookType.class, BinaryAttachment.with(
                    MARKERFORSET + ".png", HImageHostHelper.getInputStreamFromImage(DUMMYIMAGE)), Parameter
                    .with("name", MARKERFORSET));
                return true;
            } else {
                if (album.photo_count == 0) {
                    String imageSetId = getFacebookImageSetId(imageSetTitle);
                    return fbClient.publish(
                        imageSetId + "/photos",
                        FacebookType.class,
                        BinaryAttachment.with(MARKERFORSET + ".png", HImageHostHelper
                            .getInputStreamFromImage(DUMMYIMAGE)), Parameter.with("name", MARKERFORSET))
                        .getId() != null;

                } else {
                    return false;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean imageExists(final String imageSetTitle, final String imageTitle) {
        final String imageSetId = getFacebookImageSetId(imageSetTitle);

        if (imageSetId.isEmpty()) {
            return false;
        }

        return !getFacebookImageId(imageSetId, imageTitle).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean imageSetExists(final String imageSetTitle) {
        final String imageSetId = getFacebookImageSetId(imageSetTitle);
        if (imageSetId.isEmpty()) {
            return false;
        } else {
            FqlAlbum album = getFacebookImageSetFql(imageSetTitle);
            if (album.photo_count >= 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean deleteImage(final String imageSetTitle, final String imageTitle) {
        final String imageSetId = getFacebookImageSetId(imageSetTitle);

        if (imageSetId.isEmpty()) {
            return false;
        }

        final String imageId = getFacebookImageId(imageSetId, imageTitle);

        if (imageId.isEmpty()) {
            return false;
        }

        return fbClient.deleteObject(imageId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteImageSet(final String imageSetTitle) {
        final String imageSetId = getFacebookImageSetId(imageSetTitle);
        if (imageSetId.isEmpty()) {
            return false;
        }

        // NOT WORKING DUE TO BUG IN FACEBOOK GRAPH API, STORING DUMMY LIKEWISE
        // https://developers.facebook.com/bugs/404510289586183
        // fbClient.deleteObject(imageSetId);

        // Instead, uploading dummy as only picture in album
        if (clearImageSet(imageSetTitle)) {

            List<FqlPhoto> markerPhotos = getFacebookImageFql(imageSetId, MARKERFORSET);
            if (!markerPhotos.isEmpty()) {
                fbClient.deleteObject(markerPhotos.get(0).object_id);
            }
            return true;
        } else {
            return false;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean
        uploadImage(final String imageSetTitle, final String imageTitle, final BufferedImage image) {
        if (!imageSetExists(imageSetTitle)) {
            createImageSet(imageSetTitle);
        }
        if (imageExists(imageSetTitle, imageTitle)) {
            return false;

        }
        String imageSetId = getFacebookImageSetId(imageSetTitle);

        // upload image to facebook
        try {
            fbClient.publish(imageSetId + "/photos", FacebookType.class,
                BinaryAttachment.with(imageTitle + ".png", HImageHostHelper.getInputStreamFromImage(image)),
                Parameter.with("name", imageTitle)).getId();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage downloadImage(final String imageSetTitle, final String imageTitle) {
        final String imageSetId = getFacebookImageSetId(imageSetTitle);

        if (imageSetId.isEmpty()) {
            throw new IllegalArgumentException("There is no facebook album with given title: \""
                + imageSetTitle + "\"!");
        }

        List<FqlPhoto> photos = getFacebookImageFql(imageSetId, imageTitle);
        if (photos.isEmpty()) {
            return null;
        }
        final FqlPhoto fPh = photos.get(0);

        try {
            return ImageIO.read(new URL(fPh.src_big));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countImagesInSet(final String imageSetTitle) {
        FqlAlbum fAlbum = getFacebookImageSetFql(imageSetTitle);

        if (fAlbum == null) {
            throw new IllegalArgumentException("There is no facebook album with given title: \""
                + imageSetTitle + "\"!");
        }

        return fAlbum.photo_count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clearImageSet(final String imageSetTitle) {
        final String imageSetId = getFacebookImageSetId(imageSetTitle);
        if (imageSetId.isEmpty())
            return false;

        final List<FqlPhoto> photos = getFacebookImageFql(imageSetId, "");

        for (FqlPhoto ph : photos) {
            if (!ph.caption.equals(MARKERFORSET)) {
                fbClient.deleteObject(ph.object_id);
            }
        }
        return true;
    }
}
