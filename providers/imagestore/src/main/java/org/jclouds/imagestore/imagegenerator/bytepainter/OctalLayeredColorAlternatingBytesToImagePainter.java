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

    /** The number system. */
    private final int numberSystem = 8;

    /** The colors. */
    private final Color[][] colors = HBytesToImagePainterHelper
        .generateLayeredUniformlyDistributedColors(numberSystem);

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
    public BufferedImage storeBytesInImage(BufferedImage bi, final byte[] bs) {

        bi = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
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
                final int rgb = removeTransparencyFromRGB(colors[layer][idx].getRGB());

                // if second layer, add RGB-value to RGB-value of layer 1
                if (layer == 1) {
                    final int oldRGB = ciList.get(pix);
                    ciList.set(pix, rgb + oldRGB);
                } else {
                    ciList.add(rgb);
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
                    rgb += removeTransparencyFromRGB(colors[layer][idx].getRGB());
                }

                final Color cc = new Color(rgb);
                g.setColor(cc);
                g.drawLine(x, y, x, y);
            }

        }
        System.out.println(bp);
        return bi;
    }

    private int removeTransparencyFromRGB(int rgb) {
        return ((rgb << 8) >> 8);
    }

    /**
     * Gets the color from byte.
     * 
     * @param b
     *            the b
     * @param layer
     *            the layer
     * @param even
     *            the even
     * @return the color from byte
     */
    private int[] getColorIndexFromByte(final byte b, final boolean even) {

        // if even convert byte to integer, if uneven, add Byte.MAX_VALUE to
        // byte and convert to integer. This is done to alternate the colors.
        // colors1: between 0 and 255, colors2: between 256 and 511
        int it = even ? b & 0xFF : (b & 0xFF) + Byte.MAX_VALUE;

        String octs = Integer.toString(it, numberSystem);
        final int len = octs.length();
        int[] dc = new int[len];

        for (int i = 0; i < len; i++) {

            String val = octs.substring(i, i + 1);

            dc[i] = Integer.parseInt(val, numberSystem);
        }
        return dc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBytesFromImage(BufferedImage img) {

        final ArrayList<Byte> li = new ArrayList<Byte>();

        final int w = img.getWidth();
        final int h = img.getHeight();

        String[] hepts = new String[] {
            "", "", ""
        };

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;

                getHeptsFromPixel(img.getRGB(x, y), hepts);

                if (pix % 3 == 2) {

                    for (int i = 0; i < 3; i++) {
                        byte b = (byte)Integer.parseInt(hepts[i], 7);
                        li.add(b);
                    }

                    hepts = new String[] {
                        "", "", ""
                    };
                }
            }
        }
        return HBytesToImagePainterHelper.arrayListToByteArray(li);
    }

    /**
     * Gets the hepts from pixel.
     * 
     * @param pix
     *            the pix
     * @param hepts
     *            the hepts
     * @return the hepts from pixel
     */
    private void getHeptsFromPixel(final int pix, String[] hepts) {

        Color c = new Color(pix);
        int red = c.getRed();
        int green = c.getGreen();
        int blue = c.getBlue();

        for (int l = 0; l < 3; l++) {
            int dist = -1;
            int idx = -1;

            if (l == 0) {

                for (int i = 0; i < colors[l].length; i++) {
                    int cred = colors[l][i].getRed();

                    int currDist = Math.abs(cred - red);

                    if (dist == -1 || currDist < dist) {
                        dist = currDist;
                        idx = i;
                    }
                }

            } else if (l == 1) {

                for (int i = 0; i < colors[l].length; i++) {
                    int cgreen = colors[l][i].getGreen();

                    int currDist = Math.abs(cgreen - green);

                    if (dist == -1 || currDist < dist) {
                        dist = currDist;
                        idx = i;
                    }
                }
            } else {

                for (int i = 0; i < colors[l].length; i++) {
                    int cblue = colors[l][i].getBlue();

                    int currDist = Math.abs(cblue - blue);


                    if (dist == -1 || currDist < dist) {
                        dist = currDist;
                        idx = i;
                    }
                }
            }
            hepts[l] += Integer.toString(idx, 7);
        }
    }
}
