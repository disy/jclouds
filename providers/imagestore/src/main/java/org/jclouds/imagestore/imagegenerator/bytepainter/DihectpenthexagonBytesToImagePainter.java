package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

public class DihectpenthexagonBytesToImagePainter implements IBytesToImagePainter {

    /** The image type to be used. */
    private static final int BUFFERED_IMAGE_TYPE = BufferedImage.TYPE_BYTE_GRAY;
    /** Pixels needed per Byte. */
    private static final int PIXELS_PER_BYTE = 1;
    /** The numeral system. */
    private static final int NUMERAL_SYSTEM = 256;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getImageType() {
        return BUFFERED_IMAGE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float pixelsPerByte() {
        return PIXELS_PER_BYTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage storeBytesInImage(BufferedImage image, byte[] bs, int startP, int endP) {

        final int w = image.getWidth();
        final int h = image.getHeight();
        final WritableRaster raster = image.getRaster();

        // the length of the given byte-array
        int len = bs.length;
        // the current index position in the byte-array
        int bp = 0;

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                // absolute amount of pixels visited
                final int pix = hpix + x;

                // the difference between start position and pixels visited
                final int psPix = pix - startP;

                if (psPix < 0)
                    continue;

                if (pix >= endP)
                    return image;

                if (bp >= len)
                    return image;

                // get the color for the current pixel
                final int b = bs[bp++] & 0xFF;
                raster.setSample(x, y, 0, b);
            }
        }
        return image;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBytesFromImage(final BufferedImage img, final int startP, final int endP) {

        final ArrayList<Byte> li = new ArrayList<Byte>();
        final int w = img.getWidth();
        final int h = img.getHeight();
        final WritableRaster raster = img.getRaster();
        

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                // absolute amount of pixels visited
                final int pix = hpix + x;

                // the difference between start position and pixels visited
                final int psPix = pix - startP;

                if (psPix < 0)
                    continue;

                if (pix >= endP)
                    return HBytesToImagePainterHelper.arrayListToByteArray(li);

                final byte b = (byte)raster.getSample(x, y, 0);
                li.add(b);
            }
        }
        return HBytesToImagePainterHelper.arrayListToByteArray(li);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumSys() {
        return NUMERAL_SYSTEM;
    }

}
