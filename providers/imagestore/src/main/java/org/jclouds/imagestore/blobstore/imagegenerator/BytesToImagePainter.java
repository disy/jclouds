/*
 * 
 */
package org.jclouds.imagestore.blobstore.imagegenerator;

import java.awt.image.BufferedImage;

/**
 * The Interface BytesToImagePainter.
 */
public interface BytesToImagePainter {

    /**
     * Stores bytes in an Image.
     * 
     * @param BufferedImage the BufferedImage
     * @param bs the bytes to be stored in an image
     * @return the image from the given bytes
     */
    BufferedImage storeBytesInImage(BufferedImage bi, byte[] bs);

    /**
     * Recovers bytes from the given image.
     * 
     * @param img the image
     * @return the bytes from pixels
     */
    byte[] getBytesFromImage(BufferedImage img);

}
