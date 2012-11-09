package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

/**
 * This Class offers a byte painter.
 * <p/>
 * Numeral System: 256 <br/>
 * Layers: 3 <br/>
 * 1 Byte = 1/3 Pixel <br/>
 * 256^3 colors <br/>
 * <p/>
 * Working with
 * <ul>
 * <li>Picasa</li>
 * </ul>
 * Not working with
 * <ul>
 * <li>Facebook</li>
 * <li>Flickr</li>
 * </ul>
 * 
 * @author Wolfgang Miller, University of Konstanz
 */
public class DihectpenthexagonLayeredBytesToImagePainter implements IBytesToImagePainter {

    /** The image type to be used. */
    private static final int BUFFERED_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;
    /** Pixels needed per Byte in one layer. */
    private static final int PIXELS_PER_BYTE_PER_LAYER = 1;
    /** The amount of layers. */
    private static final int LAYERS = 3;

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
        return PIXELS_PER_BYTE_PER_LAYER / (float)LAYERS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage storeBytesInImage(final BufferedImage image, final byte[] bs, final int startP,
        final int endP) {

        final int w = image.getWidth();
        final int h = image.getHeight();
        final Graphics g = image.getGraphics();

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

                // get the color for the curren pixel
                Color nc = getPixelColorFromBytes(bs, len, bp);
                g.setColor(nc);
                g.drawLine(x, y, x, y);
                bp += LAYERS;
            }
        }
        return image;
    }

    /**
     * Returns the Color composed from the next three bytes in the byte array.
     * 
     * @param bs
     *            The byte array
     * @param len
     *            The lenght of the byte array
     * @param bp
     *            The current position in the byte array
     * @return the pixels color
     */
    private Color getPixelColorFromBytes(final byte[] bs, final int len, final int bp) {

        int c = 0;

        // for every layer get one byte
        for (int i = 0; i < LAYERS; i++) {
            final int pos = bp + i;

            // if all bytes are stored break loop
            if (pos >= len)
                break;

            final int b = bs[pos] & 0xFF;
            // shift byte to match right position in integer container
            final int shift = b << (i * 8);
            c += shift;
        }
        return new Color(c);
    }

    @Override
    public byte[] getBytesFromImage(final BufferedImage image, final int startP, final int endP) {
        final ArrayList<Byte> al = new ArrayList<Byte>();

        final int w = image.getWidth();
        final int h = image.getHeight();

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
                    return HBytesToImagePainterHelper.arrayListToByteArray(al);

                final int rgb = image.getRGB(x, y);

                // extract bytes from layers
                for (int layer = 0; layer < LAYERS; layer++) {
                    final byte b = (byte)HBytesToImagePainterHelper.extractLayerColorFromRGB(rgb, layer);
                    al.add(b);
                }

            }
        }

        return HBytesToImagePainterHelper.arrayListToByteArray(al);
    }

    public String toString() {
        return "DiHectPentHexagonLayered";
    }
    
}
