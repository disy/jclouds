/*
 * 
 */
package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

// TODO: Auto-generated Javadoc
/**
 * The Class OctalLayeredByteToPixelPainter.
 */
public class OctalLayeredColorAlternatingBytesToImagePainter implements IBytesToImagePainter {

    /** The number system. */
    private final int numberSystem = 8;

    /** The colors. */
    private final Color[][] colors = HBytesToImagePainterHelper
        .generateLayeredUniformlyDistributedColors(numberSystem);

    /** Bytes needed per pixel. */
    public static final float BYTES_PER_PIXEL = 3;

    @Override
    public float pixelsPerByte() {
        return BYTES_PER_PIXEL;
    }

    @Override
    public BufferedImage storeBytesInImage(final BufferedImage bi, final byte[] bs) {

        final int w = bi.getWidth();
        final int h = bi.getHeight();
        final Graphics g = bi.getGraphics();
        final int sumPix = w * h;

        int save = 0;

        ArrayList<Integer> cList = new ArrayList<Integer>(sumPix);

        int[] currByteColor = new int[0];

        /* byte index */
        int bIdx = 0;
        /* color index */
        int cIdx = 0;
        int len = bs.length;
        boolean moreBytes = true;

        for (int layer = 0; layer < 3; layer++) {

            for (int y = 0; y < h; y++) {

                final int hpix = w * y;

                for (int x = 0; x < w; x++) {

                    final int pix = hpix + x;

                    if (moreBytes) {

                        if (cIdx >= currByteColor.length) {

                            if (bIdx >= len) {
                                moreBytes = false;

                                if (layer < 2) {
                                    layer = 2;
                                    x = 0;
                                    y = 0;
                                }
                                continue;
                            }

                            final byte b = bs[bIdx++];
                            currByteColor = getColorFromByte(b, layer, bIdx % 2 == 0);
                            // System.out.println(currByteColor.length);
                            save += 3 - currByteColor.length;
                            cIdx = 0;
                        }

                        if (layer == 0) {
                            cList.add(currByteColor[cIdx++]);
                            continue;
                        }

                        int cc = cList.get(pix);
                        cList.set(pix, currByteColor[cIdx++] + cc);
                        if (layer == 1)
                            continue;

                    }

                    if (cList.size() <= pix) {
                        System.out.println("save:" + save / 3 + " bytes: " + bIdx);
                        return bi;
                    }

                    int ccc = cList.get(pix);

                    Color nc = new Color(ccc);
                    g.setColor(nc);
                    g.drawLine(x, y, x, y);
                }

            }
        }
        return bi;
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
    private int[] getColorFromByte(final byte b, final int layer, final boolean even) {
        int it = b & 0xFF;
        if (!even)
            it += 256;

        String hept = Integer.toString(it, numberSystem);
        final int l = hept.length();
        int[] byteColors = new int[l];

        for (int i = 0; i < l; i++) {

            String val = hept.substring(i, i + 1);

            int dc = Integer.parseInt(val, numberSystem);

            byteColors[i] = colors[layer][dc].getRGB();

        }
        return byteColors;
    }

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
