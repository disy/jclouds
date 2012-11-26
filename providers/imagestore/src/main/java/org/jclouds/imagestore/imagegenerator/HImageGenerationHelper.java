package org.jclouds.imagestore.imagegenerator;

import java.awt.image.BufferedImage;

import org.jclouds.imagestore.imagegenerator.bytepainter.BinaryBytesToImagePainter;

/**
 * This Class offers helper methods and constants for the ImageGenerator and the ImageExtractor.
 * 
 * @author Wolfgang Miller, University of Konstanz
 * 
 */
public final class HImageGenerationHelper {

    /** The byte array header offset. */
    static final int HEADER_OFFSET = 42;
    
    /** The robust painter. */
    static final IBytesToImagePainter ROBUST_PAINTER = new BinaryBytesToImagePainter();

    /**
     * Private constructor. Protects helper class from being instantiated.
     */
    private HImageGenerationHelper() {
    };

    /**
     * Returns the pixel where the image ends.
     * 
     * @param bi
     *            the BufferedImage
     * @return The pixel where the image ends
     */
    static int getEndPixel(final BufferedImage bi) {
        return bi.getHeight() * bi.getWidth();
    }
}
