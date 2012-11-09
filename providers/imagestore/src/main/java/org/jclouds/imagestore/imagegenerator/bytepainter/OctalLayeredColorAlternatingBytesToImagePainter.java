/*
 * 
 */
package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

/**
 * The Class OctalLayeredByteToPixelPainter.
 */
public class OctalLayeredColorAlternatingBytesToImagePainter implements IBytesToImagePainter {

    /** The image type to be used. */
    private static final int BUFFERED_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;

    /** The amount of layers. */
    private static final int LAYERS = 3;

    /** The numeral system. */
    private static final int NUMERAL_SYSTEM = 8;

    /** The colors. */
    private final Color[][] colors = HBytesToImagePainterHelper
        .generate3LayeredUniformlyDistributedColors(NUMERAL_SYSTEM);

    /** Bytes needed per pixel. */
    public static final float BYTES_PER_PIXEL = 1;

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
        return BYTES_PER_PIXEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage storeBytesInImage(final BufferedImage bi, final byte[] bs, final int startP,
        final int endP) {

        final int w = bi.getWidth();
        final int h = bi.getHeight();
        final Graphics g = bi.getGraphics();

        // ArrayList with the RGB-values of the first two layers
        ArrayList<Integer> ciList = new ArrayList<Integer>();

        // the length of the byte-array
        final int len = bs.length;
        // the current index position in the byte-array
        int bp = 0;

        // the image's amount of pixels
        final int ps = w * h;
        // the current pixel
        int pix = 0;
        // the layer
        int layer = 0;
        // the color index
        int[] ci = null;

        FirstTwoLayersFinished: while (bp < len) {
            final byte b = bs[bp++];
            ci = getColorIndexFromByte(b, bp % 2 == 0);

            final int cLen = ci.length;
            for (int i = 0; i < cLen; i++) {

                if (pix / ps >= 1) {
                    ++layer;
                    pix = 0;

                    // break if first two layers are full
                    if (layer == 2) {
                        ci = Arrays.copyOfRange(ci, i, cLen);
                        break FirstTwoLayersFinished;
                    }
                }

                final int idx = ci[i];
                final Color c = colors[layer][idx];
                final int rgb = c.getRGB();
                final int colorVal = HBytesToImagePainterHelper.extractLayerColorFromRGB(rgb, layer);

                // if second layer, add RGB-value to RGB-value of layer 1
                if (layer == 1) {
                    final int oldRGB = ciList.get(pix);
                    ciList.set(pix, (colorVal << 8) + oldRGB);
                } else {
                    ciList.add(colorVal);
                }

                ++pix;
            }
        }

        final Iterator<Integer> ciListIt = ciList.iterator();

        // the position in the position color-index array
        int cip = 0;
        for (int y = 0; y < h; y++) {

            for (int x = 0; x < w; x++) {

                if (!ciListIt.hasNext())
                    return bi;

                final boolean ciHasNext = cip < ci.length;
                int rgb = ciListIt.next();

                if (bp < len && !ciHasNext) {
                    final byte b = bs[bp++];
                    ci = getColorIndexFromByte(b, bp % 2 == 0);
                    cip = 0;
                }

                if (ciHasNext) {
                    final int idx = ci[cip++];
                    final int red = colors[layer][idx].getRed();
                    rgb += red << 16;
                }

                final Color cc = new Color(rgb);
                g.setColor(cc);
                g.drawLine(x, y, x, y);
            }

        }
        return bi;
    }

    /**
     * Gets the color from byte.
     * 
     * @param b
     *            the byte-value
     * @param even
     *            boolean if the byte has an even index in the byte-array or not
     * @return the color from byte
     */
    private int[] getColorIndexFromByte(final byte b, final boolean even) {

        // if even convert byte to integer, if uneven, add Byte.MAX_VALUE to
        // byte and convert to integer. This is done to alternate the colors.
        // colors1: between 0 and 255, colors2: between 256 and 511
        final int it = even ? b & 0xFF : (b & 0xFF) + 255;

        final String octs = Integer.toString(it, NUMERAL_SYSTEM);
        final int len = octs.length();
        final int[] dc = new int[len];

        for (int i = 0; i < len; i++) {

            String val = octs.substring(i, i + 1);

            dc[i] = Integer.parseInt(val, NUMERAL_SYSTEM);
        }
        return dc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBytesFromImage(final BufferedImage image, final int startP, final int endP) {

        final ArrayList<Byte> li = new ArrayList<Byte>();

        final int w = image.getWidth();
        final int h = image.getHeight();

        String octs = "";
        boolean even = true;
        for (int layer = 0; layer < LAYERS; layer++) {
            for (int y = 0; y < h; y++) {

                for (int x = 0; x < w; x++) {

                    final int rgb = image.getRGB(x, y);

                    if (even && rgb < 0xff000024 || !even && rgb >= 0xff000024) {

                        int b = Integer.parseInt(octs, NUMERAL_SYSTEM);
                        if (even)
                            b -= 255;

                        li.add((byte)b);
                        even = !even;
                        octs = "";
                    }

                    final int colorVal = HBytesToImagePainterHelper.extractLayerColorFromRGB(rgb, layer);
                    octs +=
                        HBytesToImagePainterHelper.getLayeredNumeralValueFromPixelColor(layer, colors,
                            colorVal, NUMERAL_SYSTEM);

                }
            }
        }
        return HBytesToImagePainterHelper.arrayListToByteArray(li);
    }
    
    public String toString() {
        return "OctalLayered";
    }

}
