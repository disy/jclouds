/*
 * 
 */
package org.jclouds.imagestore.blobstore;

import java.awt.image.BufferedImage;

/**
 * The Interface ImageHoster.
 */
public interface ImageHost {
    
    /**
     * Create image-set.
     * 
     * @param imageSetTitle the set-title
     * @return true if set was created
     */
    boolean createImageSet(final String imageSetTitle);
    
    /**
     * Check if image exists.
     * 
     * @param imageSetTitle the set-title
     * @param imageTitle the image-title
     * @return true if image exists
     */
    boolean imageExists(final String imageSetTitle, final String imageTitle);
    
    /**
     * Check if image-set exists.
     * 
     * @param imageSetTitle the set-title
     * @return true if image-set exists
     */
    boolean imageSetExists(final String imageSetTitle);
    
    /**
     * Delete image.
     * 
     * @param imageSetTitle the set-title
     * @param imageTitle the image-title
     */
    void deleteImage(final String imageSetTitle, final String imageTitle);
    
    /**
     * Delete image-set.
     * 
     * @param imageSetTitle the set-title
     * @return true if set is deleted
     */
    boolean deleteAndVerifyImageSetGone(final String imageSetTitle);

    /**
     * Upload image and add it to a specified set.
     *
     * @param imageSetName the set-title
     * @param imageName the image-title
     * @param img the image
     * @return the image-id
     */
    String uploadImage(final String imageSetTitle, final String imageTitle, 
        final BufferedImage img);
    
    /**
     * Upload image.
     *
     * @param imageName the image-title
     * @param img the image
     * @return the image-id
     */
    String uploadImage(final String imageTitle, final BufferedImage img);

    /**
     * Download image.
     * 
     * @param imageSetName the set-title
     * @param imageTitle the image-title
     * @return the buffered image
     */
    BufferedImage downloadImage(final String imageSetTitle, 
        final String imageTitle);

}
