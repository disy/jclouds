package org.jclouds.imagestore.blobstore;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * The ImageHoster interface.
 * 
 * @author Wolfgang Miller
 */
public interface ImageHost {

    /**
     * Create image-set.
     * 
     * @param imageSetTitle
     *            the set-title
     * @return true if set was created
     */
    boolean createImageSet(final String imageSetTitle);

    /**
     * Check if image exists.
     * 
     * @param imageSetTitle
     *            the set-title
     * @param imageTitle
     *            the image-title
     * @return true if image exists
     */
    boolean imageExists(final String imageSetTitle, final String imageTitle);

    /**
     * Check if image-set exists.
     * 
     * @param imageSetTitle
     *            the set-title
     * @return true if image-set exists
     */
    boolean imageSetExists(final String imageSetTitle);

    /**
     * Delete image.
     * 
     * @param imageSetTitle
     *            the set-title
     * @param imageTitle
     *            the image-title
     */
    void deleteImage(final String imageSetTitle, final String imageTitle);

    /**
     * Delete image set.
     * 
     * @param imageSetTitle
     *            the set-title
     */
    void deleteImageSet(final String imageSetTitle);

    /**
     * Upload image and add it to a specified set.
     * 
     * @param imageSetTitle
     *            the set title
     * @param imageTitle
     *            the image title
     * @param image
     *            the image
     * @return the image id
     */
    String uploadImage(final String imageSetTitle, final String imageTitle, final BufferedImage image)
        throws IOException;

    /**
     * Upload image.
     * 
     * @param imageTitle
     *            the image title
     * @param image
     *            the image
     * @return the image id
     */
    String uploadImage(final String imageTitle, final BufferedImage image) throws IOException;

    /**
     * Download image.
     * 
     * @param imageSetTitle
     *            the set title
     * @param imageTitle
     *            the image title
     * @return the buffered image
     */
    BufferedImage downloadImage(final String imageSetTitle, final String imageTitle) throws IOException;

    /**
     * Returns number of images in given set.
     * 
     * @param imageSetTitle
     *            the set title
     * @return count of images in the set
     */
    int countImagesInSet(final String imageSetTitle);

    /**
     * Deletes all content of the image set without deleting the set itself.
     * 
     * @param imageSetTitle
     *            the set title
     */
    void clearImageSet(final String imageSetTitle);

}
