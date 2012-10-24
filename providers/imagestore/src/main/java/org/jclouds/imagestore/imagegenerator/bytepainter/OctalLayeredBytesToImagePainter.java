package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.image.BufferedImage;

public class OctalLayeredBytesToImagePainter extends AAbstractLayeredBytesToImagePainter {

    /** The image type to be used. */
    private static final int BUFFERED_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;
    /** The used numeral system. */
    private static final int NUMERAL_SYSTEM = 8;
    /** Pixels needed per Byte in one layer. */
    private static final int PIXELS_PER_BYTE_PER_LAYER = 3;

    /**
     * Constructor. Invokes AAbstractLayeredBytesToImagePainter with given numeral system and the amount of
     * pixels needed to store one byte in the image.
     */
    public OctalLayeredBytesToImagePainter() {
        super(NUMERAL_SYSTEM, PIXELS_PER_BYTE_PER_LAYER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getImageType() {
        return BUFFERED_IMAGE_TYPE;
    }

}
